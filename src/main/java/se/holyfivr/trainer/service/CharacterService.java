package se.holyfivr.trainer.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.PlayerCharacter;

@Service
public class CharacterService {


    /*
    LIST OF CHARACTERS THAT CAN BE ADDED SAFELY: Pyroclast, Snowdancer, Frozen Fist, Trapper, Hive, Infuser, Metal Mosaic, Pain Conduit
    LIST OF CHARACTERS THAT CAN BE ADDED BUT DO NOT SHOW UP: Crashing tide, Shattersong, Deepwraith
    LIST OF CHARACTERS THAT BREAK THE GAME: none ... 

    IMPORTANT NOTE:
    Adding too many characters may add more bytes than can safely be removed from the filler bank.
    This WILL crash the game. So avoid this until we have a more sophisticated way of handling the filler bank, that can share bytes between multiple banks.
    */


    // List of characters that are unlocked from the start and cannot be removed
    // from the unlocked list
    private static final String[] STARTING_CHARACTERS = {
        "BannerSpearID",
        "BoneshaperID",
        "DrifterID",
        "DeathwalkerID",
        "BlinkbladeID",
        "GeminateID"
    };

    // List of characters that are allowed to be added via the enable characters
    // feature (others do not seem to be added to the game properly yet)
    private static final String[] ALLOWED_CHARACTERS = { 
        "PyroclastID", 
        "SnowdancerID", 
        "FrozenFistID", 
        "TrapperID",
        "HIVEID",
        "InfuserID",
        "MetalMosaicID",
        "PainConduitID" 
    };

    private ActiveSessionData activeSessionData;

    public CharacterService(ActiveSessionData activeSessionData){
        this.activeSessionData = activeSessionData;
    }

    /* ======================================================================== */
    /* This method handles the submission of character details from the form.   */
    /* It updates the character's ability card amount and HP levels based on    */
    /* the provided parameters. It also manages the unlocked character list     */
    /* based on the "unlocked from start" checkbox state.                       */
    /* ======================================================================== */
    public void updateCharacter(String maxAbilityCard,
            String characterName,
            String hpFieldOne,
            String hpFieldTwo,
            String hpFieldThree,
            String hpFieldFour,
            String hpFieldFive,
            String hpFieldSix,
            String hpFieldSeven,
            String hpFieldEight,
            String hpFieldNine,
            boolean isUnlockedFromStart) {

        // Fetches the selected character from the getCharacters map in
        // activeSessionData
        // and updates its properties based on the form inputs.
        PlayerCharacter selectedCharacter = activeSessionData.getCharacters().get(characterName);
        if (selectedCharacter != null) {

            selectedCharacter.setCardAmount(maxAbilityCard)
                    .setHpLvlOne(hpFieldOne)
                    .setHpLvlTwo(hpFieldTwo)
                    .setHpLvlThree(hpFieldThree)
                    .setHpLvlFour(hpFieldFour)
                    .setHpLvlFive(hpFieldFive)
                    .setHpLvlSix(hpFieldSix)
                    .setHpLvlSeven(hpFieldSeven)
                    .setHpLvlEight(hpFieldEight)
                    .setHpLvlNine(hpFieldNine);

        }

        // Update unlocked list based on the "unlocked from start" checkbox state
        List<String> unlockedList = activeSessionData.getUnlockedCharacterList();
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
        } else if (!isUnlockedFromStart && isUnlocked && !isStartingCharacter) {
            unlockedList.removeIf(s -> s.trim().equals(characterName));
        }

    }

    /* ============================================================ */
    /* This method handles displaying the character details page.   */
    /* This method iterates through a list of all currently         */
    /* unlocked characters, to decide whether to select the         */
    /* checkbox or not, when their page is loaded                   */
    /* ============================================================ */
    public boolean isCharacterUnlocked(String characterName) {        
        // loops through characters and checks if the character is in the unlocked list
        // this determines the state of the "unlocked from start" checkbox
        boolean isUnlocked = false;
        for (String character : activeSessionData.getUnlockedCharacterList()) {
            if (character.trim().equals(characterName)) {
                isUnlocked = true;
                break;
            }
        }
        return isUnlocked;
    }

    /* ============================================================ */
    /* This method handles displaying the character details page.   */
    /* It retrieves the character based on the provided name,       */
    /* checks if the character is allowed to be unlocked from the   */
    /* start and adds relevant attributes to the model based on     */
    /* response.                                                    */
    /* ============================================================ */
    public boolean isCharacterAllowed(String characterName) {

        boolean isAllowed = false;
        for (String allowedCharacter : ALLOWED_CHARACTERS) {
            if (allowedCharacter.equals(characterName)) {
                isAllowed = true;
                break;
            }
        }
        return isAllowed;
    }

    /* ==================================================================== */
    /* This method enables all allowed characters by adding them to the     */
    /* unlocked character list in active session data.                      */
    /* It iterates through a predefined list of allowed characters and adds */
    /* any missing ones to the unlocked list.                               */
    /* When we saved to the file, this list replaces the array in the       */
    /* "unlockedClasses:" line from the first GameMode parser.              */
    /* ==================================================================== */
    public void enableAllCharacters() {
         // Access the unlocked character list from active session data
        List<String> unlockedList = activeSessionData.getUnlockedCharacterList();

        // Iterate through the allowed characters and add any missing ones
        for (String character : ALLOWED_CHARACTERS) {
            if (!unlockedList.contains(character)) {
                unlockedList.add(character);
            }
        }

    }



    // Amount of cards each class has available at level 1
    // Blinkblade    = 13
    // BannerSpear   = 13
    // Boneshaper    = 15
    // Drifter       = 15
    // Deathwalker   = 14
    // Geminate      = 18
    // Crashing Tide = 15
    // Deepwraith    = 13
    // Frozen Fist   = 11
    // HIVE          = 14
    // Infuser       = 14
    // Metal Mosaic  = 12
    // Pain Conduit  = 13
    // Pyroclast     = 13
    // Snowdancer    = 14
    // Shattersong   = 13
    // Trapper       = 12

    /* ============================================================================ */
    /* This method sets the maximum safe ability card amount for all characters.    */
    /* There are limits in place here on purpose, because of in-game mechanics.     */
    /* Entering combat without being able to fill up your hands will cause the game */
    /* to prompt you to fill your hand. If you have a higher card limit allowed     */
    /* than the amount of cards available, you will be locked from entering battle  */
    /* ============================================================================ */
    public void maxCharacterAbilityCards() {
        for (PlayerCharacter character : activeSessionData.getCharacters().values()) {
            // Only update card amount if it's not null
            if (character.getCardAmount() != null) {
                switch (character.getName()) {
                    case "GeminateID"     -> character.setCardAmount("18");
                    case "BoneshaperID"   -> character.setCardAmount("15");
                    case "DrifterID"      -> character.setCardAmount("15");
                    case "CrashingTideID" -> character.setCardAmount("15");
                    case "DeathwalkerID"  -> character.setCardAmount("14");
                    case "HIVEID"         -> character.setCardAmount("14");
                    case "SnowdancerID"   -> character.setCardAmount("14");
                    case "BlinkbladeID"   -> character.setCardAmount("13");
                    case "DeepwraithID"   -> character.setCardAmount("13");
                    case "BannerSpearID"  -> character.setCardAmount("13");
                    case "PainConduitID"  -> character.setCardAmount("13");
                    case "PyroclastID"    -> character.setCardAmount("13");
                    case "ShattersongID"  -> character.setCardAmount("13");
                    case "FrozenFistID"   -> character.setCardAmount("11");
                    case "TrapperID"      -> character.setCardAmount("12");
                    case "InfuserID"      -> character.setCardAmount("14");
                    case "MetalMosaicID"  -> character.setCardAmount("12");
                }
            }
        }
    }

    /* =============================================================== */
    /* This method sets the maximum HP levels to 99 for all characters */
    /* =============================================================== */
    public void maxAllCharacterHp() {

        for (PlayerCharacter character : activeSessionData.getCharacters().values()) {
            character
                    .setHpLvlOne("99")
                    .setHpLvlTwo("99")
                    .setHpLvlThree("99")
                    .setHpLvlFour("99")
                    .setHpLvlFive("99")
                    .setHpLvlSix("99")
                    .setHpLvlSeven("99")
                    .setHpLvlEight("99")
                    .setHpLvlNine("99");
        }
    }



}
