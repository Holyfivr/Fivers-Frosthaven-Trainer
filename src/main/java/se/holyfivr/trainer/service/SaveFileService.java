package se.holyfivr.trainer.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.SaveCardChoiceEditor;
import se.holyfivr.trainer.core.SaveEnhancementEditor;
import se.holyfivr.trainer.core.parser.SaveFileParser;
import se.holyfivr.trainer.model.EnhancementGroup;
import se.holyfivr.trainer.model.SaveCharacter;
import se.holyfivr.trainer.model.SaveField;

/* ===================================== SAVE FILE SERVICE ===================================== */
/*                                                                                               */
/* Handles opening, editing and saving Frosthaven save files (.dat).                             */
/*                                                                                               */
/* On open:  a one-time backup copy is created next to the save file (with a .bak extension,     */
/*           NOT .dat — the game scans the campaign folder and would happily load a stray .dat   */
/*           backup instead of the real save). The file is then read into memory and parsed.     */
/*                                                                                               */
/* On save:  each submitted value is written as an in-place 4-byte overwrite at the exact offset */
/*           recorded during parsing. Only offsets that were found by the parser can be written  */
/*           to, so the client can never write to arbitrary positions. Resets that change the    */
/*           file size re-parse the session immediately, so the offsets always stay in sync.     */
/* ============================================================================================= */

@Service
public class SaveFileService {

    /* Outcome of an open attempt, so the controller can react appropriately:      */
    /* OPENED = success, CANCELLED = user closed the chooser (stay silent),         */
    /* FAILED = a file was chosen but couldn't be opened as a valid save.           */
    public enum OpenResult { OPENED, CANCELLED, FAILED }

    private final ActiveSessionData activeSessionData;
    private final FileService fileService;
    private final SaveFileParser saveFileParser;
    private final SaveEnhancementEditor enhancementEditor;
    private final SaveCardChoiceEditor cardChoiceEditor;

    public SaveFileService(ActiveSessionData activeSessionData, FileService fileService,
            SaveFileParser saveFileParser, SaveEnhancementEditor enhancementEditor,
            SaveCardChoiceEditor cardChoiceEditor) {
        this.activeSessionData = activeSessionData;
        this.fileService = fileService;
        this.saveFileParser = saveFileParser;
        this.enhancementEditor = enhancementEditor;
        this.cardChoiceEditor = cardChoiceEditor;
    }

    /* ======================================================================== */
    /* Opens the file chooser for save files (.dat), creates a backup, reads    */
    /* the file into memory and parses the character records into the session.  */
    /* Returns an OpenResult so the frontend can tell success, a plain cancel,   */
    /* and a genuine failure apart (and not show a false toast on cancel).       */
    /* ======================================================================== */
    public OpenResult openSaveFileWithDialog() {
        // Ruleset and save file sessions are mutually exclusive. The menu option
        // is greyed out while a ruleset is open; this guard backs that up.
        if (activeSessionData.getRulesetPath() != null) {
            return OpenResult.CANCELLED;
        }
        File selectedFile = fileService.chooseFile("Frosthaven Save Files", "*.dat", "lastSaveDir");
        if (selectedFile == null) {
            return OpenResult.CANCELLED;
        }
        try {
            Path savePath = selectedFile.toPath();

            byte[] saveBytes = Files.readAllBytes(savePath);
            List<SaveCharacter> characters = saveFileParser.parse(saveBytes);

            // No town or character records means this isn't a Frosthaven campaign
            // save (or the format changed). Treat it as a failed open rather than
            // showing an empty editor, and don't back up a file we can't edit.
            if (characters.isEmpty()) {
                activeSessionData.clearSaveFile();
                return OpenResult.FAILED;
            }

            // One-time backup next to the save file. Uses .bak so the game
            // never mistakes the backup for a real save.
            Path backupPath = savePath.resolveSibling(selectedFile.getName() + ".bak");
            if (Files.notExists(backupPath)) {
                Files.copy(savePath, backupPath);
            }

            activeSessionData.setSaveFilePath(savePath);
            activeSessionData.setSaveFileBytes(saveBytes);
            activeSessionData.setSaveCharacters(characters);
            activeSessionData.setEnhancementGroups(enhancementEditor.parseGroups(saveBytes));
            return OpenResult.OPENED;
        } catch (IOException e) {
            e.printStackTrace();
            activeSessionData.clearSaveFile();
            return OpenResult.FAILED;
        }
    }

    /* ======================================================================== */
    /* Applies the submitted values and writes the save file back to disk.      */
    /* The form posts inputs named "field_<offset>". We only look up offsets    */
    /* that the parser found, patch each value in place in the in-memory copy,  */
    /* and write the resulting file back.                                       */
    /* Returns false if anything goes wrong (e.g. the game holds a file lock),  */
    /* in which case the session is kept open so the user can try again.        */
    /* ======================================================================== */
    public boolean saveAndClose(Map<String, String> submittedValues) {
        byte[] saveBytes = activeSessionData.getSaveFileBytes();
        Path savePath = activeSessionData.getSaveFilePath();
        if (saveBytes == null || savePath == null) {
            return false;
        }
        try {
            for (SaveCharacter character : activeSessionData.getSaveCharacters()) {
                for (SaveField field : character.getFields()) {
                    String submitted = submittedValues.get("field_" + field.getOffset());
                    if (submitted == null || submitted.isBlank()) {
                        continue;
                    }
                    // Clamp to the field's allowed range (0 .. max), matching
                    // the limits enforced by the form inputs
                    int value = Integer.parseInt(submitted.trim());
                    value = Math.clamp(value, 0, field.getMax());
                    writeIntLE(saveBytes, field.getOffset(), value);
                }
            }
            Files.write(savePath, saveBytes);
            activeSessionData.clearSaveFile();
            return true;
        } catch (Exception e) {
            // IOException (locked file), NumberFormatException (bad submitted value), or any
            // unexpected RuntimeException (e.g. an out-of-range offset) all fail the save
            // gracefully via the error modal rather than surfacing a 500.
            e.printStackTrace();
            return false;
        }
    }

    /* ======================================================================== */
    /* Removes all of one class's card enhancements from the in-memory save.    */
    /* Enhancements are class-persistent, so this is the "reset a character's    */
    /* enhancements" action. The change is applied to the in-memory bytes only  */
    /* and persisted on Save & Close (consistent with the resource edits).      */
    /* Because a wipe shifts byte offsets, the save is fully re-parsed so the    */
    /* resource fields and enhancement summary stay in sync with the new bytes. */
    /* Returns false if there's no open save.                                   */
    /* ======================================================================== */
    public boolean resetEnhancements(String className) {
        byte[] saveBytes = activeSessionData.getSaveFileBytes();
        if (saveBytes == null || className == null || className.isBlank()) {
            return false;
        }
        byte[] wiped = enhancementEditor.wipeClass(saveBytes, className);
        activeSessionData.setSaveFileBytes(wiped);
        activeSessionData.setSaveCharacters(saveFileParser.parse(wiped));
        activeSessionData.setEnhancementGroups(enhancementEditor.parseGroups(wiped));
        return true;
    }

    /* ======================================================================== */
    /* Lists the card choice resets that can be inferred safely from the save.  */
    /* Returns an empty list for unsupported saves, so the tab just shows none. */
    /* ======================================================================== */
    public List<SaveCardChoiceEditor.ResetOption> getCardChoiceResets() {
        byte[] saveBytes = activeSessionData.getSaveFileBytes();
        if (saveBytes == null) {
            return List.of();
        }
        try {
            return cardChoiceEditor.availableResets(saveBytes);
        } catch (IllegalArgumentException e) {
            return List.of(); // Unknown save format: do not offer binary edits.
        } catch (RuntimeException e) {
            e.printStackTrace(); // Unexpected failure: hide the feature, keep the page working.
            return List.of();
        }
    }

    /* ======================================================================== */
    /* Reopens spent card choices above targetLevel in the in-memory save.      */
    /* The result is parsed before it replaces the session, so a failed reset   */
    /* leaves the session unchanged. Persisted on Save & Close, like the        */
    /* enhancement reset. Returns false if the reset was refused or failed.     */
    /* ======================================================================== */
    public boolean resetCardChoices(int characterIndex, int targetLevel) {
        byte[] saveBytes = activeSessionData.getSaveFileBytes();
        if (saveBytes == null) {
            return false;
        }
        try {
            byte[] reset = cardChoiceEditor.reset(saveBytes, characterIndex, targetLevel);
            List<SaveCharacter> characters = saveFileParser.parse(reset);
            List<EnhancementGroup> groups = enhancementEditor.parseGroups(reset);
            if (characters.isEmpty()) {
                return false;
            }
            activeSessionData.setSaveFileBytes(reset);
            activeSessionData.setSaveCharacters(characters);
            activeSessionData.setEnhancementGroups(groups);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // The editor refused this reset.
        } catch (RuntimeException e) {
            e.printStackTrace(); // Unexpected failure: keep the session unchanged.
            return false;
        }
    }

    /* ======================================================================== */
    /* Closes the save file session without writing anything to disk.           */
    /* ======================================================================== */
    public void closeSaveFile() {
        activeSessionData.clearSaveFile();
    }

    // Helper: writes a 4-byte little-endian int at the given offset
    private void writeIntLE(byte[] data, int offset, int value) {
        data[offset] = (byte) (value & 0xff);
        data[offset + 1] = (byte) ((value >> 8) & 0xff);
        data[offset + 2] = (byte) ((value >> 16) & 0xff);
        data[offset + 3] = (byte) ((value >> 24) & 0xff);
    }
}
