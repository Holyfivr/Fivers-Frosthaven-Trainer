package se.holyfivr.trainer.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javafx.application.Platform;
import javafx.stage.FileChooser;
import se.holyfivr.trainer.WebContainer;
import se.holyfivr.trainer.controller.StartController;
import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.RulesetLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/* ========================================================================= */
/* This class is responsible for handling the file selection dialog          */
/* and updating the active session with the selected file path.              */
/* ========================================================================= */

@Controller
public class FileLocator {

    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;

    public FileLocator(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }

    /* ========================================================================= */
    /* This method opens a file chooser dialog for the user to select a */
    /* ruleset file. It updates the active session with the selected file */
    /* path and loads the ruleset. */
    /* ========================================================================= */
    @GetMapping("/open-file")
    public String openFile() {

        // Creates a countdown latch to let the user select a file before continuing
        // this acts like a "pause" in the code until the user has made a selection
        CountDownLatch waitForUser = new CountDownLatch(1);

        // Run the file chooser in the JavaFX thread
        Platform.runLater(() -> {
            try {
                // perform the file selection
                performFileSelection();
            } finally {
                // when finished, countdown the latch to continue
                waitForUser.countDown();
            }
        });

        try {
            // waits for the latch to countdown before continuing
            waitForUser.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "redirect:/start";
    }

    /* ========================================================================= */
    /* This method performs the actual file selection using a FileChooser. */
    /* It sets the selected file path in the active session and loads the */
    /* ruleset. */
    /* ========================================================================= */
    private void performFileSelection() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter for ruleset files
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ruleset Files", "*.ruleset"));

        // Loads last used directory from preferences, if one is available
        File initialDir = getLastUsedDirectory();
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }

        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(WebContainer.primaryStage);

        if (selectedFile != null) {

            // if a file was selected, set the path as the active ruleset path and load it
            activeSessionData.setRulesetPath(selectedFile.toPath());

            // First time the ruleset is loaded, a backup is made.
            // This will be used to reset to original values, if needed.
            Path original = activeSessionData.getRulesetPath();
            Path backupDir = original.getParent().resolve("original ruleset");
            Path backupFile = backupDir.resolve("ORIGINAL_BACKUP.ruleset");

            // Create directory if missing
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

            // load the ruleset
            rulesetLoader.loadRuleset();

            // Save the selected directory for next time
            saveLastUsedDirectory(selectedFile.getParentFile());
        }
    }

    // method to get last used directory from preferences
    private File getLastUsedDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(StartController.class);
        String path = prefs.get("lastDir", null);

        if (path != null) {

            File directory = new File(path);

            // Checks that the directory still exists
            if (directory.exists() && directory.isDirectory()) {
                return directory;
            }
        }
        return null;
    }

    // method to save last used directory to preferences
    private void saveLastUsedDirectory(File directory) {
        if (directory != null) {
            Preferences prefs = Preferences.userNodeForPackage(StartController.class);
            prefs.put("lastDir", directory.getAbsolutePath());
        }
    }

}
