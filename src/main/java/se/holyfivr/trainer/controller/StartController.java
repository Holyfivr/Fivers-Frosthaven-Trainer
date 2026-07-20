package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javafx.application.Platform;
import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.core.RulesetLoader;

@Controller
public class StartController {

    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;

    public StartController(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }

    @GetMapping("/start")
    public String getStart(Model model,
            @RequestParam(required = false, defaultValue = "false") boolean loaded,
            @RequestParam(required = false, defaultValue = "false") boolean saveError,
            @RequestParam(required = false, defaultValue = "false") boolean fileOpened,
            @RequestParam(required = false, defaultValue = "false") boolean savefileOpened,
            @RequestParam(required = false, defaultValue = "false") boolean savefileError) {

        // loads the character map into the model
        model.addAttribute("characterMap", activeSessionData.getCharacters());

        // loads card classes to be used in the ability card dropdown
        model.addAttribute("abilityCardClasses", activeSessionData.getCardClasses());

        // loads ability card map into the model, so they can be used in the templates
        model.addAttribute("abilityCardMap", activeSessionData.getAbilityCards());

        // Sends info to frontend on whether a file is loaded or not
        // if not, appropriate menu-options are disabled
        model.addAttribute("rulesetLoaded", activeSessionData.getRulesetPath() != null);

        // Tells the frontend whether a save file (.dat) is open. While one is,
        // the contentframe shows the save editor and "Save Ruleset" is disabled.
        model.addAttribute("saveFileLoaded", activeSessionData.isSaveFileLoaded());

        // Tells the frontend whether to show the size-mismatch warning modal
        // (set when the opened file differs in size from the original backup)
        model.addAttribute("showSizeMismatchModal", activeSessionData.isSizeMismatchWarning());

        // If the file was just loaded, we show the success modal
        if (loaded) {
            model.addAttribute("showSuccessModal", true);
        }

        // Tells the frontend whether the last save attempt failed
        model.addAttribute("saveError", saveError);

        // Tells the frontend whether a ruleset/save file was JUST opened
        // (as opposed to already being open). Used for the success toasts,
        // so cancelling the file chooser doesn't show a false success.
        model.addAttribute("fileOpened", fileOpened);
        model.addAttribute("savefileOpened", savefileOpened);

        // Tells the frontend the chosen file couldn't be opened as a valid save
        model.addAttribute("savefileError", savefileError);

        return "start";
    }

    @PostMapping("/save")
    public String saveRuleset() {
        boolean saved = rulesetLoader.saveRuleset();
        if (saved) {
            // Saving also "closes" the ruleset: ruleset and save file sessions
            // are mutually exclusive, and this is how a ruleset session ends.
            // The parsed data stays in memory, but the menus grey out.
            activeSessionData.setRulesetPath(null);
            // Clear any pending mismatch warning: with no ruleset open, the
            // modal's restore/replace actions would have no file to work on.
            activeSessionData.setSizeMismatchWarning(false);
        }
        return saved ? "redirect:/start" : "redirect:/start?saveError=true";
    }

    @GetMapping("/exit")
    public String exitProgram() {
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
        return "redirect:/start";
    }

    @GetMapping("/how")
    public String redirectHow() {
        return "how";
    }
    
    @GetMapping("/about")
    public String redirectAbout() {
        return "about";
    }

    @GetMapping("/why")
    public String redirectWhy() {
        return "why";
    }

    @GetMapping("/troubleshooting")
    public String redirectTroubleshooting() {
        return "troubleshooting";
    }

}
