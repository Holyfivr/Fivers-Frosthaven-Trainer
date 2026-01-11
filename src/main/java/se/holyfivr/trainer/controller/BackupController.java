package se.holyfivr.trainer.controller;

import se.holyfivr.trainer.service.BackupService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class BackupController {
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /* ============================================= */
    /* Handles request to create a new custom backup */
    /* ============================================= */
    @GetMapping("/create-backup")
    public String createBackup(@RequestParam("name") String name) {
        return backupService.createBackup(name);
    }

    /* =========================================== */
    /* Handles request to restore original ruleset */
    /* =========================================== */
    @GetMapping("/restore-backup")
    public String restoreBackup() {
        return backupService.restoreBackup();
    }

}
