package se.holyfivr.trainer.service;


import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.core.ActiveSessionData;
import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.enums.CardAction;

@Service
public class AbilityCardService {

    private final ActiveSessionData activeSessionData;

    public AbilityCardService(ActiveSessionData activeSessionData) {
        this.activeSessionData = activeSessionData;
    }

    public void updateAllCards(String attribute, String value) {

        CardAction cardAttribute = CardAction.fromString(attribute);
        System.err.println("Attribute: " + attribute + " | Value: " + value);
        switch (cardAttribute) {
            case SET_ATTACK:
                activeSessionData.setAbilityCardAttack(value);
                break;
            case SET_DAMAGE:
                activeSessionData.setAbilityCardDamage(value);
                break;
            case SET_HEAL:
                activeSessionData.setAbilityCardHeal(value);
                break;
            case SET_HEALTH:
                activeSessionData.setAbilityCardHealth(value);
                break;
            case SET_MOVE:
                activeSessionData.setAbilityCardMove(value);
                break;
            case SET_RANGE:
                activeSessionData.setAbilityCardRange(value);
                break;
            case SET_SHIELD:
                activeSessionData.setAbilityCardShield(value);
                break;
            case SET_TARGET:
                activeSessionData.setAbilityCardTarget(value);
                break;
            case SET_PULL:
                activeSessionData.setAbilityCardPull(value);
                break;
            case SET_PUSH:
                activeSessionData.setAbilityCardPush(value);
                break;
            case SET_JUMP:
                activeSessionData.setAbilityCardJump(value);
                break;
            case SET_RETALIATE:
                activeSessionData.setAbilityCardRetaliate(value);
                break;
            case SET_PIERCE:
                activeSessionData.setAbilityCardPierce(value);
                break;
            case SET_INITIATIVE:
                activeSessionData.setAbilityCardInitiative(value);
                break;
            case SET_DISCARD:
                activeSessionData.setAbilityCardDiscard(value);
                break;
            case SET_CONSUMES:
                activeSessionData.setAbilityCardConsumes(value);
                break;
            case SET_INFUSES:
                activeSessionData.setAbilityCardInfuses(value);
                break;
            case SET_XP:
                activeSessionData.setAbilityCardXP(value);
                break;
            default:
                break;
        }
    }

    /* ================================================================= */
    /* Saves the specific card, when edited from it's specific card-page */
    /* ================================================================= */
    public void saveCard(AbilityCard existingAbilityCard, AbilityCard abilityCard) {

        if (existingAbilityCard != null) {
            existingAbilityCard
                    .setInitiative(abilityCard.getInitiative())
                    .setDiscard(abilityCard.getDiscard())
                    .setConsumes(abilityCard.getConsumes())
                    .setInfuses(abilityCard.getInfuses())
                    .setPierce(abilityCard.getPierce())
                    .setXP(abilityCard.getXP())
                    .setHeal(abilityCard.getHeal())
                    .setAttack(abilityCard.getAttack())
                    .setRange(abilityCard.getRange())
                    .setTarget(abilityCard.getTarget())
                    .setShield(abilityCard.getShield())
                    .setRetaliate(abilityCard.getRetaliate())
                    .setLoot(abilityCard.getLoot())
                    .setMove(abilityCard.getMove())
                    .setPull(abilityCard.getPull())
                    .setPush(abilityCard.getPush())
                    .setJump(abilityCard.getJump());

            existingAbilityCard
                    .setAttackValues(abilityCard.getAttackValues())
                    .setDamageValues(abilityCard.getDamageValues())
                    .setHealValues(abilityCard.getHealValues())
                    .setMoveValues(abilityCard.getMoveValues())
                    .setRangeValues(abilityCard.getRangeValues())
                    .setShieldValues(abilityCard.getShieldValues())
                    .setTargetValues(abilityCard.getTargetValues())
                    .setPullValues(abilityCard.getPullValues())
                    .setPushValues(abilityCard.getPushValues())
                    .setRetaliateValues(abilityCard.getRetaliateValues())
                    .setPierceValues(abilityCard.getPierceValues())
                    .setXpValues(abilityCard.getXpValues())
                    .setLootValues(abilityCard.getLootValues());
        }
        
    }

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
        System.err.println("class not found: " + formattedName);
        return new String[] { "Unknown", formattedName };
    }

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
