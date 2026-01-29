package se.holyfivr.trainer.service;

import org.springframework.stereotype.Service;
import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.RulesetLoader;
import se.holyfivr.trainer.model.enums.RulesetFileName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class BackupService {
    private static final String REDIRECT_START = "redirect:/start";
    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;

    public BackupService(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }

    /* ==================================================================== */
    /* This method creates a backup of the current ruleset file with the    */
    /* specified name. It copies the current ruleset file to a new file     */
    /* with the given name in the same directory.                           */
    /* ==================================================================== */
    public String createBackup(String name) {
        Path path = activeSessionData.getRulesetPath();
        try {
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
    public String restoreBackup() {
        Path path = activeSessionData.getRulesetPath();
        Path backupDir = path.getParent().resolve("original ruleset");
        Path backupPath = backupDir.resolve(RulesetFileName.ORIGINAL_BACKUP.getFileName());
        if (Files.exists(backupPath)) {
            try {
                Files.copy(backupPath, path, StandardCopyOption.REPLACE_EXISTING);
                rulesetLoader.loadRuleset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Could not restore: No backup file found.");
        }
        return REDIRECT_START;
    }
}
