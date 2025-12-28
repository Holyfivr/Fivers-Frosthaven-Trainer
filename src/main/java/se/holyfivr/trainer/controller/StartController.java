package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import se.holyfivr.trainer.service.RulesetLoader;
import se.holyfivr.trainer.service.ActiveSessionData;

import javafx.application.Platform;

@Controller
public class StartController {

    private final ActiveSessionData activeSessionData;
    private final RulesetLoader rulesetLoader;

   


    public StartController(ActiveSessionData activeSessionData, RulesetLoader rulesetLoader) {
        this.activeSessionData = activeSessionData;
        this.rulesetLoader = rulesetLoader;
    }

    @GetMapping("/start")
    public String getStart(Model model, @RequestParam(required = false) boolean loaded) {
        
        // loads the character map into the model
        model.addAttribute("characterMap", activeSessionData.getCharacters());
        
        // Sends info to frontend on whether a file is loaded or not
        // if not, appropriate menu-options are disabled
        model.addAttribute("rulesetLoaded", activeSessionData.getRulesetPath() != null);
        
        // If the file was just loaded, we show the success modal
        if (loaded) {
            model.addAttribute("showSuccessModal", true);
        }

        return "start";
    }
    
    @GetMapping("/save")
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

}
