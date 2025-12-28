package se.holyfivr.trainer.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.service.ActiveSessionData;
import org.springframework.web.bind.annotation.RequestParam;

/*
LIST OF CHARACTERS THAT CAN BE ADDED SAFELY: Pyroclast, Snowdancer, Frozen fist, Trapper, Hive
LIST OF CHARACTERS THAT CAN BE ADDED BUT DO NOT SHOW UP: Crashing tide, Shattersong, Deepwraith, Infuser, MetalMosaic, PainConduit
LIST OF CHARACTERS THAT BREAK THE GAME: none ... * 

* However, adding too many characters will add more bytes than can safely be removed from the filler bank.
This WILL crash the game. So avoid this until we have a more sofisticated way of handling the filler bank, that can share bytes between multiple banks.


*/

@Controller
public class CharacterController {

    private static  final String REDIRECT_START = "redirect:/start";

    // List of characters that are unlocked from the start and cannot be removed from the unlocked list
    private static final String[] STARTING_CHARACTERS = {
            "BannerSpearID",
            "BoneshaperID",
            "DrifterID",
            "DeathwalkerID",
            "BlinkbladeID",
            "GeminateID"
    };

    // List of characters that are allowed to be added via the enable characters feature (others do not seem to be added to the game properly yet)
    private static final String[] ALLOWED_CHARACTERS = { "PyroclastID", "SnowdancerID", "FrozenFistID", "TrapperID", "HIVEID" };

    // Active session data to access characters and unlocked character list
    private final ActiveSessionData state;

    
    CharacterController(ActiveSessionData state) {
        this.state = state;
    }

    
        /* ======================================================================== */
        /* This method handles displaying the character details page.               */
        /* It retrieves the character based on the provided name,                   */
        /* checks if the character is unlocked from the start,                      */
        /* and adds relevant attributes to the model based on response.             */
        /* ======================================================================== */
    @GetMapping("/character/{characterName}")
    public String getCharacter(@PathVariable("characterName") String characterName, @RequestParam(required = false) Boolean saved, Model model) {

        PlayerCharacter selectedCharacter = state.getCharacters().get(characterName);

        model.addAttribute("character", selectedCharacter);
        model.addAttribute("saved", saved);

        // loops through characters and checks if the character is in the unlocked list
        // this determines the state of the "unlocked from start" checkbox
        boolean isUnlocked = false;
        for (String character : state.getUnlockedCharacterList()) {
            if (character.trim().equals(characterName)) {
                isUnlocked = true;
                break;
            }
        }
        model.addAttribute("isUnlockedFromStart", isUnlocked);

        // Check if character is in the allowed list for enabling via the enable characters feature
        // and add this info to the model. Since not all characters seem to be added properly to the
        // game yet, we need to restrict which characters can be added this way.
        boolean isAllowed = false;
        for (String allowedCharacter : ALLOWED_CHARACTERS) {
            if (allowedCharacter.equals(characterName)){
                isAllowed = true;
                break;
            }
        }
        model.addAttribute("allowedCharacter", isAllowed);

        return "character";
    }


        /* ======================================================================== */
        /* This method handles the submission of character details from the form.   */
        /* It updates the character's ability card amount and HP levels based on    */
        /* the provided parameters. It also manages the unlocked character list     */
        /* based on the "unlocked from start" checkbox state.                       */
        /* ======================================================================== */
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

        
        // Fetches the selected character from the getCharacters map in activeSessionData
        // and updates its properties based on the form inputs.
        PlayerCharacter selectedCharacter = state.getCharacters().get(characterName);
        if (selectedCharacter != null) {

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

        // Update unlocked list based on the "unlocked from start" checkbox state
        List<String> unlockedList = state.getUnlockedCharacterList();
        boolean isUnlocked = false;
        // Check if character is already in the unlocked list
        for (String unlocked : unlockedList) {
            if (unlocked.trim().equals(characterName)) {
                isUnlocked = true;
                break;
            }
        }
        // makes sure you cannot remove starting characters from the unlocked list
        boolean isStartingCharacter = Arrays.asList(STARTING_CHARACTERS).contains(characterName);

        // Add or remove from unlocked list based on the checkbox
        if (isUnlockedFromStart && !isUnlocked) {
            unlockedList.add(characterName);
            System.out.println("Added " + characterName + " to unlocked list."); // debug
        } else if (!isUnlockedFromStart && isUnlocked && !isStartingCharacter) {
            unlockedList.removeIf(s -> s.trim().equals(characterName));
        }

        return "redirect:/character/" + characterName + "?saved=true";
    }


        /* ==================================================================== */
        /* This method enables all allowed characters by adding them to the     */
        /* unlocked character list in active session data.                      */
        /* It iterates through a predefined list of allowed characters and adds */
        /* any missing ones to the unlocked list.                               */
        /* When we saved to the file, this list replaces the array in the       */
        /* "unlockedClasses:" line from the first GameMode parser.              */
        /* ==================================================================== */
    @GetMapping("/enablecharacters")
    public String enableCharacters() {

        // Access the unlocked character list from active session data
        List<String> unlockedList = state.getUnlockedCharacterList();

        // Iterate through the allowed characters and add any missing ones
        for (String character : ALLOWED_CHARACTERS) {
            if (!unlockedList.contains(character)) {
                unlockedList.add(character);
            }
        }

        return REDIRECT_START;
    }

    /* ==================================================================== */
    /* This method sets the maximum ability card amount to 20 for all       */
    /* characters.                                                          */
    /* ==================================================================== */
    @GetMapping("/maxcards")
    public String maxCards(Model model) {
        for (PlayerCharacter character : state.getCharacters().values()) {
            // Only update card amount if it's not null
            if (character.getCardAmount() != null) {
                character.setCardAmount("20");
            }
        }
        model.addAttribute("maxcards", true);
        return REDIRECT_START;
    }

    /* ==================================================================== */
    /* This method sets the maximum HP levels to 99 for all characters.     */
    /* ==================================================================== */
    @GetMapping("/maxhp")
    public String maxHp(Model model) {

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
        return REDIRECT_START;
    }

}
