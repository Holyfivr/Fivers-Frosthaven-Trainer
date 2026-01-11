package se.holyfivr.trainer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.service.CharacterService;

import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CharacterController {

    private static final String REDIRECT_START = "redirect:/start";

    // Active session data to access characters and unlocked character list
    private final ActiveSessionData state;
    private final CharacterService characterService;

    CharacterController(ActiveSessionData state, CharacterService characterService) {
        this.state = state;
        this.characterService = characterService;
    }

    /* ========================================================================== */
    /* Handles request to fetch characterdata to display in their respective page */
    /* ========================================================================== */
    @GetMapping("/character/{characterName}")
    public String getCharacter(@PathVariable("characterName") String characterName,
            @RequestParam(required = false) Boolean saved, Model model) {

        PlayerCharacter selectedCharacter = state.getCharacters().get(characterName);
        model.addAttribute("character", selectedCharacter);
        model.addAttribute("saved", saved);

        boolean isUnlocked = characterService.isCharacterUnlocked(characterName);
        model.addAttribute("isUnlockedFromStart", isUnlocked);

        boolean isAllowed = characterService.isCharacterAllowed(characterName);
        model.addAttribute("allowedCharacter", isAllowed);

        return "character";
    }

    /* =============================================================================================== */
    /* Handles request to update character data, based on the form from the individual character pages */
    /* =============================================================================================== */
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
            @RequestParam("hpFieldNine") String hpFieldNine,
            @RequestParam(value = "isUnlockedFromStart", defaultValue = "false") boolean isUnlockedFromStart) {

        characterService.updateCharacter(maxAbilityCard, characterName, hpFieldOne, hpFieldTwo, hpFieldThree,
                hpFieldFour, hpFieldFive, hpFieldSix, hpFieldSeven, hpFieldEight, hpFieldNine, isUnlockedFromStart);

        return "redirect:/character/" + characterName + "?saved=true";
    }

    /* ================================================ */
    /* Handles request to enable all allowed characters */
    /* ================================================ */
    @GetMapping("/enablecharacters")
    public String enableCharacters() {
        characterService.enableAllCharacters();

        return REDIRECT_START;
    }

    /* ===================================================================== */
    /* Handles request to set max available ability cards for all characters */
    /* ===================================================================== */
    @GetMapping("/maxcards")
    public String maxCards(Model model) {
        characterService.maxCharacterAbilityCards();

        model.addAttribute("maxcards", true);

        return REDIRECT_START;
    }

    /* ================================================= */
    /* Handles request to set max hp for all characters. */
    /* ================================================= */
    @GetMapping("/maxhp")
    public String maxHp(Model model) {

        characterService.maxAllCharacterHp();
        model.addAttribute("maxhp", true);

        return REDIRECT_START;
    }

}
