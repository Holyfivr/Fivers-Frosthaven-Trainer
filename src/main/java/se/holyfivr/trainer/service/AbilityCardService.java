package se.holyfivr.trainer.service;


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.enums.CardAction;
import se.holyfivr.trainer.model.enums.DiscardEnum;

@Service
public class AbilityCardService {

    private final ActiveSessionData activeSessionData;

    public AbilityCardService(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }

    /**
     * Method used for bulk-updating a single attribute across every ability card
     * in the current session. Called from the controller when the user edits a value on
     * the "edit all cards" page.
     *
     * The attribute string is mapped to a CardAction enum, which then
     * determines which getter/setter pair (and optional list getter) to pass to the
     * generic helper (see further down).
     *
     */
    public void updateAllCards(String attribute, String value) {

        if (value == null || value.isBlank()){
            return;
        }

        CardAction cardAttribute = CardAction.fromString(attribute);
        System.err.println("[updateAllCards] Attribute: " + attribute + " | Value: " + value);

        switch (cardAttribute) {
            case SET_ATTACK     -> updateAllCards(AbilityCard::getAttack    , AbilityCard::setAttack    , value, AbilityCard::getAttackValues);
            case SET_DAMAGE     -> updateAllCards(AbilityCard::getAttack    , AbilityCard::setAttack    , value, AbilityCard::getAttackValues);
            case SET_HEAL       -> updateAllCards(AbilityCard::getHeal      , AbilityCard::setHeal      , value, AbilityCard::getHealValues);
            case SET_MOVE       -> updateAllCards(AbilityCard::getMove      , AbilityCard::setMove      , value, AbilityCard::getMoveValues);
            case SET_RANGE      -> updateAllCards(AbilityCard::getRange     , AbilityCard::setRange     , value, AbilityCard::getRangeValues);
            case SET_SHIELD     -> updateAllCards(AbilityCard::getShield    , AbilityCard::setShield    , value, AbilityCard::getShieldValues);
            case SET_TARGET     -> updateAllCards(AbilityCard::getTarget    , AbilityCard::setTarget    , value, AbilityCard::getTargetValues);
            case SET_PULL       -> updateAllCards(AbilityCard::getPull      , AbilityCard::setPull      , value, AbilityCard::getPullValues);
            case SET_PUSH       -> updateAllCards(AbilityCard::getPush      , AbilityCard::setPush      , value, AbilityCard::getPushValues);
            case SET_RETALIATE  -> updateAllCards(AbilityCard::getRetaliate , AbilityCard::setRetaliate , value, AbilityCard::getRetaliateValues);
            case SET_PIERCE     -> updateAllCards(AbilityCard::getPierce    , AbilityCard::setPierce    , value, AbilityCard::getPierceValues);
            case SET_LOOT       -> updateAllCards(AbilityCard::getLoot      , AbilityCard::setLoot      , value, AbilityCard::getLootValues);
            case SET_XP         -> updateAllCards(AbilityCard::getXP        , AbilityCard::setXP        , value, AbilityCard::getXpValues);
            case SET_JUMP       -> updateAllCards(AbilityCard::getJump      , AbilityCard::setJump      , value, null);
            case SET_HEALTH     -> updateAllCards(AbilityCard::getHealth    , AbilityCard::setHealth    , value, null);
            case SET_INITIATIVE -> updateAllCards(AbilityCard::getInitiative, AbilityCard::setInitiative, value, null);
            case SET_CONSUMES   -> updateAllCards(AbilityCard::getConsumes  , AbilityCard::setConsumes  , value, null);
            case SET_INFUSE     -> updateAllCards(AbilityCard::getInfuse    , AbilityCard::setInfuse    , value, null);
            case SET_DISCARD -> {
                for (AbilityCard card : activeSessionData.getAbilityCards().values()) {
                    card.setDiscard(DiscardEnum.fromString(value));
                }
            }
            // Special case for the discard, since we use an enum for those values, and it's not worth complicating the helpermethod even further just to deal with that.

        }
    }

/**
     * Generic helper that iterates over every ability card and updates both the
     * primary field and (optionally) the corresponding values list for a given attribute.
     *
     * This single method replaces what was previously 19 nearly identical
     * setter-methods in ActiveSessionData. It works by accepting functional references
     * to the card's getter and setter, so the same loop logic can be reused for any 
     * String-based attribute.
     *
     *   For each card, the getter is called to check if the attribute exists
     *   and isn't null. Cards that don't have the attribute are skipped entirely.
     *   If the attribute exists, the setter writes the new value to the
     *   primary field (e.g. card.setAttack(value)).
     *   If a listGetter is provided (not null), it retrieves the list of
     *   individual occurrences of that attribute on the card (e.g. a card may have
     *   multiple attack actions). Each entry in the list is then overwritten with
     *   the new value.
     *   List entries with the sentinel value '-1' are preserved and never
     *   overwritten. This is used for AoE markers on Range and Target, where
     *   '-1' signals "area of effect" rather than a numeric range/target value.
     *
     * There are three usage patterns from the switch in the caller-method.
     * 
     * * Attribute + list (most common): e.g. Attack, Move, Heal — passes both
     *   getter/setter and a listGetter. Updates the primary field and every occurrence
     *   in the values list.
     * * Attribute only: e.g. Initiative, Jump, Health — passes null
     *   as listGetter. Only updates the single primary field since these attributes
     *   never appear more than once on a card.
     * * Discard: This is not routed through this method at all, because it uses
     *    DiscardEnum instead of String.
     *
     * @param getter     a function that reads the current value of the attribute from a card.
     *                   Used as a null-check: if it returns null, the card is skipped.
     * @param setter     a biconsumer that writes the new value to the card's primary field.
     * @param value      the new value to assign.
     * @param listGetter a function that returns the list of all occurrences of this attribute
     *                   on the card (e.g. AbilityCard::getAttackValues), or null
     *                   if the attribute only ever appears once per card.
     */
    public void updateAllCards(Function<AbilityCard, String> getter, BiConsumer<AbilityCard, String> setter, String value,
            Function<AbilityCard, List<String>> listGetter) {
        for (AbilityCard card : activeSessionData.getAbilityCards().values()) {
            if (getter.apply(card) != null) {
                setter.accept(card, value);
                if (listGetter != null) {
                    List<String> values = listGetter.apply(card);
                    if (values != null && !values.isEmpty()) {
                        for (int i = 0; i < values.size(); i++) {
                            if ("-1".equals(values.get(i))) {
                                continue;
                            }
                            values.set(i, value);
                        }
                    }
                }
            }
        }
    }

    /**
     * Saves changes to a single ability card, typically when the user edits values
     * on the individual card-detail page.
     *
     * Copies every attribute and values list from the incoming abilityCard
     * onto the existingAbilityCard that is already stored in
     * ActiveSessionData. This is a full overwrite of all fields; both the
     * primary fields (attack, move, etc.) and their corresponding multi-value
     * lists (attackValues, moveValues, etc.).
     *
     * @param existingAbilityCard the card object currently held in session data.
     *                            If null, no changes are made.
     * @param abilityCard         the card object containing the new values from the form.
     */
    public void saveCard(AbilityCard existingAbilityCard, AbilityCard abilityCard) {

        if (existingAbilityCard != null) {
            existingAbilityCard
                .setInitiative      (abilityCard.getInitiative())
                .setDiscard         (abilityCard.getDiscard())
                .setConsumes        (abilityCard.getConsumes())
                .setInfuse          (abilityCard.getInfuse())
                .setPierce          (abilityCard.getPierce())
                .setXP              (abilityCard.getXP())
                .setHeal            (abilityCard.getHeal())
                .setAttack          (abilityCard.getAttack())
                .setRange           (abilityCard.getRange())
                .setTarget          (abilityCard.getTarget())
                .setShield          (abilityCard.getShield())
                .setRetaliate       (abilityCard.getRetaliate())
                .setLoot            (abilityCard.getLoot())
                .setMove            (abilityCard.getMove())
                .setPull            (abilityCard.getPull())
                .setPush            (abilityCard.getPush())
                .setJump            (abilityCard.getJump())
                .setAttackValues    (abilityCard.getAttackValues())
                .setDamageValues    (abilityCard.getDamageValues())
                .setHealValues      (abilityCard.getHealValues())
                .setMoveValues      (abilityCard.getMoveValues())
                .setRangeValues     (abilityCard.getRangeValues())
                .setShieldValues    (abilityCard.getShieldValues())
                .setTargetValues    (abilityCard.getTargetValues())
                .setPullValues      (abilityCard.getPullValues())
                .setPushValues      (abilityCard.getPushValues())
                .setRetaliateValues (abilityCard.getRetaliateValues())
                .setPierceValues    (abilityCard.getPierceValues())
                .setXpValues        (abilityCard.getXpValues())
                .setLootValues      (abilityCard.getLootValues());
        }
        
    }

    /**
     * Returns a map of ability cards filtered by character class name.
     *
     * If  className is  "all", the complete unfiltered map from
     * ActiveSessionData is returned. Otherwise, only cards whose
     * className matches the given value are included.
     *
     * The returned map uses the card's display name as its key and preserves
     * insertion order via LinkedHashMap.
     *
     * @param className the character class to filter by, or  "all" to return
     *                  every card in the session.
     * @return a map of matching ability cards keyed by card name.
     */
    public Map<String, AbilityCard> getCardsByClass(String className) {
        Map<String, AbilityCard> cardMap = activeSessionData.getAbilityCards();
        if ("all".equals(className)) {
            return cardMap;
        }
        Map<String, AbilityCard> cardClassMap = new LinkedHashMap<>();
        for (AbilityCard card : cardMap.values()) {
            if (card.getClassName() != null && card.getClassName().equals(className)) {
                cardClassMap.putIfAbsent((card.getCardName()), card);
            }
        }
        return cardClassMap;

    }

    /**
     * Extracts the character class name and card name from a raw ruleset asset name.
     *
     * The raw name typically looks like
     * "..._character_ability_cards_banner_spear_rallying_cry_123$".
     * This method strips the prefix and suffix, replaces underscores with spaces,
     * capitalizes each word, and then matches the result against the known
     * classList to split it into [className, cardName].
     *
     * @param name the raw internal asset name from the ruleset.
     * @return a two-element array: [className, cardName].
     *         If no known class matches, className is "Unknown".
     */
    public String[] formatCardDetails(String name) {

        String cleanedName = name.replaceAll("^.*_character_ability_cards_", "")
                .replaceAll("(?:_\\d+)?\\$$", "")
                .replaceAll("_", " ");

        String formattedName = capitalizeWords(cleanedName);

        for (String className : classList) {
            if (formattedName.toLowerCase().startsWith(className.toLowerCase())) {
                String cardName = formattedName.substring(className.length()).trim();
                return new String[] { className, cardName };
            }
        }
        return new String[] { "Unknown", formattedName };
    }

    /**
     * Capitalizes the first letter of each whitespace-delimited word in the given text.
     * The entire string is first lowercased before capitalization is applied.
     *
     * @param text the input string to capitalize.
     * @return the string with each word's first letter uppercased, e.g.
     *         "banner spear" becomes "Banner Spear".
     */
    public static String capitalizeWords(String text) {
        String[] parts = text.trim().toLowerCase().split("\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty())
                continue;
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return builder.toString().trim();
    }

    private final String[] classList = {
            "Banner Spear",
            "Blinkblade",
            "Boneshaper",
            "Crashing Tide",
            "Deathwalker",
            "Deepwraith",
            "Drifter",
            "Frozen Fist",
            "Geminate",
            "HIVE",
            "Infuser",
            "Metal Mosaic",
            "Pain Conduit",
            "Pyroclast",
            "Shattersong",
            "Snowdancer",
            "Trapper"
    };

}
