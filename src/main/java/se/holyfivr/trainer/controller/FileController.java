package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import se.holyfivr.trainer.service.FileService;

@Controller
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /* ==================================== */
    /* Handles request to open ruleset file */
    /* ==================================== */
    @GetMapping("/open-file")
    public String openFile() {
        fileService.openFileWithDialog();
        return "redirect:/start";
    }
}
