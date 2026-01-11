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
    /* This method opens the filechooser dialog for the user to select a file. */
    /* It pauses the execution until the user has made a selection, by using a */
    /* countdownLatch and running the dialog in the JavaFX thread.             */
    /* When the file is selected, the chosen fil is processed by the           */
    /* performedFileSelection() method.                                        */
    /* ======================================================================= */
    public void openFileWithDialog() {
        CountDownLatch waitForUser = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                performFileSelection();
            } finally {
                waitForUser.countDown();
            }
        });
        try {
            waitForUser.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* ================================================================================ */
    /* Handles the file selection dialog and processes the selected rulese file.        */
    /* - Opens a file chooser for ruleset files (*.ruleset).                            */
    /* - Sets the selected file as the active ruleset path.                             */
    /* - Creates a permanent reset-backup of the ruleset if one does not already exist. */
    /* - Loads the ruleset into the application.                                        */
    /* - Saves the directory to use as the standard path, when the user opens it agian  */
    /* ================================================================================ */
    public void performFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ruleset Files", "*.ruleset"));
        File initialDir = getLastUsedDirectory();
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }
        File selectedFile = fileChooser.showOpenDialog(WebContainer.primaryStage);
        if (selectedFile != null) {
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
            if (Files.notExists(backupFile)) {
                try {
                    Files.copy(original, backupFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            rulesetLoader.loadRuleset();
            saveLastUsedDirectory(selectedFile.getParentFile());
        }
    }

    /* ============================================================ */
    /* Fetches the last directory the ruleset file was loaded from. */
    /* ============================================================ */
    public File getLastUsedDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(StartController.class);
        String path = prefs.get("lastDir", null);
        if (path != null) {
            File directory = new File(path);
            if (directory.exists() && directory.isDirectory()) {
                return directory;
            }
        }
        return null;
    }

    /* ============================================================ */
    /* Saves the last directory used to open the ruleset file from. */
    /* ============================================================ */
    public void saveLastUsedDirectory(File directory) {
        if (directory != null) {
            Preferences prefs = Preferences.userNodeForPackage(StartController.class);
            prefs.put("lastDir", directory.getAbsolutePath());
        }
    }
}
