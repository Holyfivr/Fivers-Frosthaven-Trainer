package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import se.holyfivr.trainer.service.RulesetLoader;
import se.holyfivr.trainer.service.ActiveSessionData;

import javafx.application.Platform;

@Controller
public class StartController {

    private final ActiveSessionData state;
    private final RulesetLoader ruleserLoader;

    public StartController(ActiveSessionData state, RulesetLoader ruleserLoader) {
        this.state = state;
        this.ruleserLoader = ruleserLoader;
    }


    @GetMapping("/start")
    public String getStart(Model model) {
        // Filter out tutorial characters for the UI to keep it clean
        java.util.Map<String, se.holyfivr.trainer.model.PlayerCharacter> allChars = state.getCharacters();
        java.util.Map<String, se.holyfivr.trainer.model.PlayerCharacter> uiCharacters = new java.util.LinkedHashMap<>();
        
        for (var entry : allChars.entrySet()) {
            if (!entry.getKey().toLowerCase().contains("tutorial")) {
                uiCharacters.put(entry.getKey(), entry.getValue());
            }
        }

        model.addAttribute("characterMap", uiCharacters);

        return "start";
    }
    
    @GetMapping("/save")
    public String saveRuleset() {
        ruleserLoader.saveRuleset();
        return "redirect:/start";
    }
    


    @GetMapping("/exit")
    public void exitProgram() {
        Platform.runLater(() -> {
            Platform.exit();
            System.exit(0);
        });
    }

}
