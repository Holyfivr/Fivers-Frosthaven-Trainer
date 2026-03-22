package se.holyfivr.trainer.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.holyfivr.trainer.core.ActiveSessionData;

import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.service.AbilityCardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller for handling requests related to ability cards, 
 * including fetching all cards, 
 * fetching cards by class, fetching details of a specific card, 
 * and saving/updating card information.
 */

@Controller
public class AbilityCardController {

    private final AbilityCardService abilityCardService;
    private final ActiveSessionData activeSessionData;

    public AbilityCardController(AbilityCardService abilityCardService, ActiveSessionData activeSessionData) {
        this.abilityCardService = abilityCardService;
        this.activeSessionData = activeSessionData;
    }

    @GetMapping("/ability-cards/all")
    public String getAllCards(Model model) {
        Map<String, AbilityCard> allCards = activeSessionData.getAbilityCards();
        model.addAttribute("abilityCards", allCards);
        return "ability-cards";
    }

    @GetMapping("/ability-cards/{className}")
    public String getClassCards(Model model, @PathVariable("className") String className) {
        Map<String, AbilityCard> classCards = abilityCardService.getCardsByClass(className);
        model.addAttribute("currentClass", className);
        model.addAttribute("abilityCards", classCards);
        return "ability-cards";
    }

    @GetMapping("/card-details")
    public String getSelectedCard(@RequestParam("id") String cardName, Model model) {        
        AbilityCard abilityCard = activeSessionData.getAbilityCards().get(cardName);
        model.addAttribute("abilityCard", abilityCard);
        return "card-details";
    }

    @PostMapping("/save-ability-card")
    public String saveCard(@ModelAttribute AbilityCard abilityCard) {
        AbilityCard existingAbilityCard = activeSessionData.getAbilityCards().get(abilityCard.getName());
        
        abilityCardService.saveCard(existingAbilityCard, abilityCard);
        
        return "redirect:/card-details?id=" + abilityCard.getName();
    }

    @PostMapping("/update-all-cards")
    public String setCardValues(@RequestParam Map<String, String> allFields) {
        String init2 = allFields.remove("setInitiative2");
        allFields.forEach((action, value) -> {
            if (action.equals("setInitiative") && init2 != null && !init2.isBlank()) {
                abilityCardService.updateAlternatingInitiative(value, init2);
            } else {
                abilityCardService.updateAllCards(action, value);
            }
        });
        return "redirect:/how";
    }
    

}
