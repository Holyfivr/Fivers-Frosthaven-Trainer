package se.holyfivr.trainer.service;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Service;
import se.holyfivr.trainer.WebContainer;
import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.RulesetLoader;
import se.holyfivr.trainer.model.enums.RulesetFileName;
import se.holyfivr.trainer.controller.StartController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;

@Service
public class FileService {
    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;


    public FileService(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }

    /* ======================================================================= */
    /* Opens the file chooser for ruleset files and processes the selection.   */
    /* The dialog itself is handled by the shared chooseFile() helper, which   */
    /* is also used by the SaveFileService for .dat save files.                */
    /* Returns true only if a file was actually selected and loaded, so the    */
    /* frontend doesn't show a false success toast when the user cancels.      */
    /* ======================================================================= */
    public boolean openFileWithDialog() {
        // Ruleset and save file sessions are mutually exclusive. The menu option
        // is greyed out while a save file is open; this guard backs that up.
        if (activeSessionData.isSaveFileLoaded()) {
            return false;
        }
        File selectedFile = chooseFile("Ruleset Files", "*.ruleset", "lastDir");
        if (selectedFile == null) {
            return false;
        }
        processSelectedRuleset(selectedFile);
        return true;
    }

    /* ======================================================================== */
    /* Shared file chooser dialog. Runs the JavaFX FileChooser on the FX thread */
    /* and pauses the calling (web) thread with a CountDownLatch until the user */
    /* has made a selection. The last used directory is remembered per prefKey, */
    /* so ruleset files and save files each keep their own start directory.     */
    /* Returns the selected file, or null if the user cancelled.               */
    /* ======================================================================== */
    public File chooseFile(String filterDescription, String filterPattern, String prefKey) {
        File[] result = new File[1];
        CountDownLatch waitForUser = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(filterDescription, filterPattern));
                File initialDir = getLastUsedDirectory(prefKey);
                if (initialDir != null) {
                    fileChooser.setInitialDirectory(initialDir);
                }
                result[0] = fileChooser.showOpenDialog(WebContainer.primaryStage);
            } finally {
                waitForUser.countDown();
            }
        });
        try {
            waitForUser.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result[0] != null) {
            saveLastUsedDirectory(result[0].getParentFile(), prefKey);
        }
        return result[0];
    }

    /* ================================================================================ */
    /* Processes the selected ruleset file.                                             */
    /* - Sets the selected file as the active ruleset path.                             */
    /* - Creates a permanent reset-backup of the ruleset if one does not already exist. */
    /* - Loads the ruleset into the application.                                        */
    /* ================================================================================ */
    private void processSelectedRuleset(File selectedFile) {
        activeSessionData.setRulesetPath(selectedFile.toPath());
        Path original = activeSessionData.getRulesetPath();
        Path backupDir = original.getParent().resolve("original ruleset");
        Path backupFile = backupDir.resolve(RulesetFileName.ORIGINAL_BACKUP.getFileName());
        if (Files.notExists(backupDir)) {
            try {
                Files.createDirectories(backupDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Compare the opened file against the existing original backup.
        // A size difference means the file was changed outside this tool:
        // either the game was patched (new, valid file) or the file is corrupted.
        // We can't tell which here, so we flag it and let the user decide on /start.
        boolean sizeMismatch = false;
        if (Files.notExists(backupFile)) {
            try {
                Files.copy(original, backupFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                sizeMismatch = Files.size(original) != Files.size(backupFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        rulesetLoader.loadRuleset();
        // Set the flag after loading, since loading resets parsed session data.
        activeSessionData.setSizeMismatchWarning(sizeMismatch);
    }

    /* ============================================================ */
    /* Fetches the last directory a file was loaded from, for the   */
    /* given preference key.                                        */
    /* ============================================================ */
    public File getLastUsedDirectory(String prefKey) {
        Preferences prefs = Preferences.userNodeForPackage(StartController.class);
        String path = prefs.get(prefKey, null);
        if (path != null) {
            File directory = new File(path);
            if (directory.exists() && directory.isDirectory()) {
                return directory;
            }
        }
        return null;
    }

    /* ============================================================ */
    /* Saves the last directory a file was opened from, under the   */
    /* given preference key.                                        */
    /* ============================================================ */
    public void saveLastUsedDirectory(File directory, String prefKey) {
        if (directory != null) {
            Preferences prefs = Preferences.userNodeForPackage(StartController.class);
            prefs.put(prefKey, directory.getAbsolutePath());
        }
    }
}
