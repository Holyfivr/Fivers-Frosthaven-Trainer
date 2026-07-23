package se.holyfivr.trainer.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.service.SaveFileService;
import se.holyfivr.trainer.service.SaveFileService.OpenResult;

/**
 * Controller for the save file (.dat) editor: opening a save file,
 * displaying the per-character editor page, and saving/closing.
 */

@Controller
public class SaveFileController {

    private final SaveFileService saveFileService;
    private final ActiveSessionData activeSessionData;

    public SaveFileController(SaveFileService saveFileService, ActiveSessionData activeSessionData) {
        this.saveFileService = saveFileService;
        this.activeSessionData = activeSessionData;
    }

    /* ==================================================================== */
    /* Handles request to open a save file (.dat).                          */
    /* - OPENED   -> success toast                                          */
    /* - FAILED   -> error modal (not a valid save, or couldn't be read)    */
    /* - CANCELLED -> nothing (user closed the chooser)                     */
    /* ==================================================================== */
    @GetMapping("/open-savefile")
    public String openSaveFile() {
        OpenResult result = saveFileService.openSaveFileWithDialog();
        return switch (result) {
            case OPENED -> "redirect:/start?savefileOpened=true";
            case FAILED -> "redirect:/start?savefileError=true";
            case CANCELLED -> "redirect:/start";
        };
    }

    /* ================================================== */
    /* Shows the save file editor page in the contentframe */
    /* ================================================== */
    @GetMapping("/savefile")
    public String getSaveFile(Model model) {
        if (!activeSessionData.isSaveFileLoaded()) {
            return "redirect:/how";
        }
        model.addAttribute("saveFileName", activeSessionData.getSaveFilePath().getFileName().toString());
        model.addAttribute("saveCharacters", activeSessionData.getSaveCharacters());
        model.addAttribute("enhancementGroups", activeSessionData.getEnhancementGroups());
        return "savefile";
    }

    /* ======================================================================== */
    /* Removes one class's enhancements from the open save (in memory; written  */
    /* on Save & Close). GET so it works as a plain link inside the editor       */
    /* (JavaFX WebView blocks window.confirm, and this avoids a nested form).     */
    /* Redirects back to the editor, which re-renders with refreshed field       */
    /* offsets and the updated enhancement list.                                */
    /* ======================================================================== */
    @GetMapping("/reset-enhancements")
    public String resetEnhancements(@RequestParam("className") String className) {
        saveFileService.resetEnhancements(className);
        return "redirect:/savefile";
    }

    /* ======================================================================== */
    /* Applies the submitted values, writes the save file and closes the        */
    /* session. On failure the session stays open and the error modal is shown. */
    /* ======================================================================== */
    @PostMapping("/save-savefile")
    public String saveSaveFile(@RequestParam Map<String, String> submittedValues) {
        boolean saved = saveFileService.saveAndClose(submittedValues);
        return saved ? "redirect:/start" : "redirect:/start?saveError=true";
    }

    /* ============================================== */
    /* Closes the save file without saving any changes */
    /* ============================================== */
    @GetMapping("/close-savefile")
    public String closeSaveFile() {
        saveFileService.closeSaveFile();
        return "redirect:/start";
    }
}
