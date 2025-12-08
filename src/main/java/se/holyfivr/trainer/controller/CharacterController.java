package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.service.State;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CharacterController {

    private final State state;

    CharacterController(State state) {
        this.state = state;
    }

    @GetMapping("/character/{characterName}")
    public String getCharacter(@PathVariable("characterName") String characterName, Model model) {

        PlayerCharacter selectedCharacter = state.getCharacters().get(characterName);

        model.addAttribute("character", selectedCharacter);

        return "character";
    }

    @GetMapping("/character/submit")
    public String submitCharacter(@RequestParam("maxAbilityCard") String maxAbilityCard,
            @RequestParam("characterName") String characterName,
            @RequestParam("hpFieldOne") String hpFieldOne,
            @RequestParam("hpFieldTwo") String hpFieldTwo,
            @RequestParam("hpFieldThree") String hpFieldThree,
            @RequestParam("hpFieldFour") String hpFieldFour,
            @RequestParam("hpFieldFive") String hpFieldFive,
            @RequestParam("hpFieldSix") String hpFieldSix,
            @RequestParam("hpFieldSeven") String hpFieldSeven,
            @RequestParam("hpFieldEight") String hpFieldEight,
            @RequestParam("hpFieldNine") String hpFieldNine
        ) {
        PlayerCharacter selectedCharacter = state.getCharacters().get(characterName);
        if (selectedCharacter != null) {
            System.out.println("User modifying character: " + characterName);
            System.out.println("  -> Setting cards to: " + maxAbilityCard);
            
            selectedCharacter.setCardAmount(maxAbilityCard);
            selectedCharacter.setHpLvlOne(hpFieldOne);
            selectedCharacter.setHpLvlTwo(hpFieldTwo);
            selectedCharacter.setHpLvlThree(hpFieldThree);
            selectedCharacter.setHpLvlFour(hpFieldFour);
            selectedCharacter.setHpLvlFive(hpFieldFive);
            selectedCharacter.setHpLvlSix(hpFieldSix);
            selectedCharacter.setHpLvlSeven(hpFieldSeven);
            selectedCharacter.setHpLvlEight(hpFieldEight);
            selectedCharacter.setHpLvlNine(hpFieldNine);
        }
        return "redirect:/character/" + characterName;
    }


    @GetMapping("/maxcards")
    public String maxCards(Model model) {
        for (PlayerCharacter character : state.getCharacters().values()) {
            // Only update card amount if it's not null (some characters might not have cards)
            if (character.getCardAmount() != null) {
                character.setCardAmount("20");
            }
            
            // Also set HP to 99 as requested by the "Max Cards" feature description
            character.setHpLvlOne("99");
            character.setHpLvlTwo("99");
            character.setHpLvlThree("99");
            character.setHpLvlFour("99");
            character.setHpLvlFive("99");
            character.setHpLvlSix("99");
            character.setHpLvlSeven("99");
            character.setHpLvlEight("99");
            character.setHpLvlNine("99");
        }
        model.addAttribute("maxcards", true);
        return "redirect:/start";
    }
        @GetMapping("/maxhp")
    public String maxHp(Model model
    ) {
        for (PlayerCharacter character : state.getCharacters().values()) {
            character.setHpLvlOne("99");
            character.setHpLvlTwo("99");
            character.setHpLvlThree("99");
            character.setHpLvlFour("99");
            character.setHpLvlFive("99");
            character.setHpLvlSix("99");
            character.setHpLvlSeven("99");
            character.setHpLvlEight("99");
            character.setHpLvlNine("99");
        }
        model.addAttribute("maxhp", true);
        return "redirect:/start";
    }

}
