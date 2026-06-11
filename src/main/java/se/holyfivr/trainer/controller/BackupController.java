package se.holyfivr.trainer.controller;

import se.holyfivr.trainer.service.BackupService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling backup-related requests, including creating new backups and restoring original rulesets.
 */

@Controller
public class BackupController {

    private final BackupService backupService;
    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /* ============================================= */
    /* Handles request to create a new custom backup */
    /* ============================================= */
    @PostMapping("/create-backup")
    public String createBackup(@RequestParam("name") String name) {
        return backupService.createBackup(name);
    }

    /* =========================================== */
    /* Handles request to restore original ruleset */
    /* =========================================== */
    @PostMapping("/restore-backup")
    public String restoreBackup() {
        return backupService.restoreBackup();
    }

    /* ====================================================================== */
    /* Handles request to replace the original backup with the current file.  */
    /* Used when the game was patched and the old backup is outdated.          */
    /* ====================================================================== */
    @PostMapping("/replace-backup")
    public String replaceBackup() {
        return backupService.replaceOriginalBackup();
    }

}
