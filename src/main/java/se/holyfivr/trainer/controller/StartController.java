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
    public String getStart(Model model, @RequestParam(required = false, defaultValue = "false") boolean loaded) {

        // loads the character map into the model
        model.addAttribute("characterMap", activeSessionData.getCharacters());

        // loads card classes to be used in the ability card dropdown
        model.addAttribute("abilityCardClasses", activeSessionData.getCardClasses());

        // loads ability card map into the model, so they can be used in the templates
        model.addAttribute("abilityCardMap", activeSessionData.getAbilityCards());

        // Sends info to frontend on whether a file is loaded or not
        // if not, appropriate menu-options are disabled
        model.addAttribute("rulesetLoaded", activeSessionData.getRulesetPath() != null);

        // Tells the frontend whether to show the size-mismatch warning modal
        // (set when the opened file differs in size from the original backup)
        model.addAttribute("showSizeMismatchModal", activeSessionData.isSizeMismatchWarning());

        // If the file was just loaded, we show the success modal
        if (loaded) {
            model.addAttribute("showSuccessModal", true);
        }

        return "start";
    }

    @PostMapping("/save")
    public String saveRuleset() {
        rulesetLoader.saveRuleset();
        return "redirect:/start";
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
