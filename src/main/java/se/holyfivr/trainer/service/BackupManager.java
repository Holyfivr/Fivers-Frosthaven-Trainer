package se.holyfivr.trainer.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.RulesetLoader;


@Controller
public class BackupManager {
    private static final String REDIRECT_START = "redirect:/start";
    private static final String ORIGINAL_BACKUP = "ORIGINAL_BACKUP.ruleset";

    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;

    public BackupManager(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }


    /* ==================================================================== */
    /* This method creates a backup of the current ruleset file with the    */
    /* specified name. It copies the current ruleset file to a new file     */
    /* with the given name in the same directory.                           */
    /* ==================================================================== */
    @GetMapping("/create-backup")
    public String createBackup(@RequestParam("name") String name) {
        Path path = activeSessionData.getRulesetPath();
        try {
            // copies the original file and makes a copy of it, rewriting the existing copy if there is already one.
            Files.copy(path, path.resolveSibling(name + ".ruleset"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup created successfully at: " + path.resolveSibling(name + ".ruleset"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return REDIRECT_START;
    }

    /* ==================================================================== */
    /* This method restores the ruleset file from a backup file named       */
    /* "ORIGINAL_BACKUP.ruleset", which was created first time the          */
    /* ruleset was opened.                                                  */
    /* It copies the backup file over the current ruleset file and reloads  */
    /* the ruleset into memory.                                             */
    /* ==================================================================== */
    @GetMapping("/restore-backup")
    public String restoreBackup() {
        Path path = activeSessionData.getRulesetPath();
        Path backupDir = path.getParent().resolve("original ruleset");
        Path backupPath = backupDir.resolve(ORIGINAL_BACKUP);

        if (Files.exists(backupPath)) {
            try {
                // copies the backup file over the current ruleset file
                Files.copy(backupPath, path, StandardCopyOption.REPLACE_EXISTING);
                
                // reloads the file into memory, so changes are updated in the application
                rulesetLoader.loadRuleset();
                System.out.println("Backup restored successfully.");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not restore: No backup file found.");
        }
        return REDIRECT_START;
    }

}
