package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import se.holyfivr.trainer.service.FileService;

/**
 * Controller for handling file-related requests, such as opening the ruleset file.
 */

@Controller
public class FileController {

    private final FileService fileService;
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /* ==================================================================== */
    /* Handles request to open ruleset file.                                */
    /* The fileOpened flag is only set when a file was actually selected,   */
    /* so cancelling the file chooser doesn't trigger a success toast.      */
    /* ==================================================================== */
    @GetMapping("/open-file")
    public String openFile() {
        boolean opened = fileService.openFileWithDialog();
        return opened ? "redirect:/start?fileOpened=true" : "redirect:/start";
    }
}
