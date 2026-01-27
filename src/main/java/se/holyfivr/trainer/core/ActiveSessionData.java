package se.holyfivr.trainer.core;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import se.holyfivr.trainer.model.AbilityCard;
import se.holyfivr.trainer.model.Item;
import se.holyfivr.trainer.model.PlayerCharacter;
import se.holyfivr.trainer.model.enums.DiscardEnum;

@Service
public class ActiveSessionData {

    private Path rulesetPath;

    public Path getRulesetPath() {
        return rulesetPath;
    }

    public void setRulesetPath(Path rulesetPath) {
        this.rulesetPath = rulesetPath;
    }

    /* =========================== */
    /* STORE ALL GAME CHARACTERS */
    /* =========================== */

    private final Map<String, PlayerCharacter> characters = new LinkedHashMap<>();

    public Map<String, PlayerCharacter> getCharacters() {
        return characters;
    }

    /* ==================================================================== */
    /* This filters out the tutorial versions of bannerspear. */
    /* It basically says "if there is no match of the received name */
    /* add it to the map". There is already a check like this one */
    /* in the RulesetParser, but having it here as well adds an */
    /* extra layer of safety. */
    /* ==================================================================== */
    public void addCharacter(PlayerCharacter character) {
        if (character.getName() != null) {
            characters.putIfAbsent(character.getName(), character);
        }
    }

    public void clearCharacters() {
        characters.clear();
    }

    /* ============================= */
    /* STORE ALL UNLOCKED CHARACTERS */
    /* ============================= */
    private final List<String> unlockedCharacters = new ArrayList<>();

    public List<String> getUnlockedCharacterList() {
        return unlockedCharacters;
    }

    public void addUnlockedCharacter(String character) {
        unlockedCharacters.add(character);
    }

    public void clearUnlockedCharacters() {
        for (int i = unlockedCharacters.size() - 1; i >= 0; i--) {
            unlockedCharacters.remove(i);
        }
    }

    /* =============== */
    /* STORE ALL ITEMS */
    /* =============== */
    private final Map<String, Item> items = new LinkedHashMap<>();

    public Map<String, Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        if (item.getStringId() != null) {
            items.putIfAbsent(item.getStringId(), item);
        }
    }

    public void clearItems() {
        items.clear();
    }

    public void setID(String value) {
        for (Item item : items.values()) {
            if (item.getId() != null) {
                item.setId(value);
            }
        }
    }

    public void setGold(String value) {
        System.err.println("Setting gold to: " + value);
        for (Item item : items.values()) {
            if (item.getCost() != null && !item.getCost().trim().equals("0")) {
                item.setCost(value);
            }
        }
    }

    public void setProsperity(String value) {
        for (Item item : items.values()) {
            if (item.getProsperityRequirement() != null && !item.getProsperityRequirement().trim().equals("0")) {
                item.setProsperityRequirement(value);
            }
        }
    }

    public void setHeal(String value) {
        for (Item item : items.values()) {
            if (item.getHeal() != null) {
                item.setHeal(value);
            }
        }
    }

    public void setRetaliate(String value) {
        for (Item item : items.values()) {
            if (item.getRetaliate() != null) {
                item.setRetaliate(value);
            }
        }
    }

    public void setMovement(String value) {
        for (Item item : items.values()) {
            if (item.getMove() != null) {
                item.setMove(value);
            }
        }
    }

    public void setOMove(String value) {
        for (Item item : items.values()) {
            if (item.getOMove() != null) {
                item.setOMove(value);
            }
        }
    }

    public void setAMove(String value) {
        for (Item item : items.values()) {
            if (item.getAMove() != null) {
                item.setAMove(value);
            }
        }
    }

    public void setAttack(String value) {
        for (Item item : items.values()) {
            if (item.getAttack() != null) {
                item.setAttack(value);
            }
        }
    }

    public void setShield(String value) {
        for (Item item : items.values()) {
            if (item.getShield() != null) {
                item.setShield(value);
            }
        }
    }

    public void setShieldValue(String value) {
        for (Item item : items.values()) {
            if (item.getShieldValue() != null) {
                item.setShieldValue(value);
            }
        }
    }

    public void setUsage(String value) {
        for (Item item : items.values()) {
            if (item.getUsage() != null) {
                item.setUsage(value);
            }
        }
    }

    public void setRange(String value) {
        for (Item item : items.values()) {
            if (item.getRange() != null) {
                item.setRange(value);
            }
        }
    }

    public void setTotalInGame(String value) {
        for (Item item : items.values()) {
            if (item.getTotalInGame() != null) {
                item.setTotalInGame(value);
            }
        }
    }

    /* ====================== */
    /* STORE ALL ABILITYCARDS */
    /* ====================== */

    private final Map<String, AbilityCard> abilityCards = new LinkedHashMap<>();

    public Map<String, AbilityCard> getAbilityCards() {
        return abilityCards;
    }

    public List<String> getCardClasses() {

        List<String> classList = new ArrayList<>();
        for (AbilityCard card : abilityCards.values()) {
            if (!classList.contains(card.getClassName())) {
                classList.add(card.getClassName());
            }
        }
        return classList;
    }

    public void addAbilityCard(AbilityCard abilityCard) {
        if (abilityCard.getName() != null) {
            abilityCards.putIfAbsent(abilityCard.getName(), abilityCard);
        }
    }

    public void clearAbilityCards() {
        abilityCards.clear();
    }

    public void setAbilityCardAttack(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getAttack() != null) {
                abilityCard.setAttack(value);
                if (abilityCard.getAttackValues() != null && !abilityCard.getAttackValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getAttackValues().size(); i++) {
                        abilityCard.getAttackValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardHeal(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getHeal() != null) {
                abilityCard.setHeal(value);
                if (abilityCard.getHealValues() != null && !abilityCard.getHealValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getHealValues().size(); i++) {
                        abilityCard.getHealValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardHealth(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getHealth() != null) {
                abilityCard.setHealth(value);
            }
        }
    }

    public void setAbilityCardDamage(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getDamage() != null) {
                abilityCard.setDamage(value);
                if (abilityCard.getDamageValues() != null && !abilityCard.getDamageValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getDamageValues().size(); i++) {
                        abilityCard.getDamageValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardMove(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getMove() != null) {
                abilityCard.setMove(value);
                if (abilityCard.getMoveValues() != null && !abilityCard.getMoveValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getMoveValues().size(); i++) {
                        abilityCard.getMoveValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardRange(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getRange() != null) {
                abilityCard.setRange(value);
                if (abilityCard.getRangeValues() != null && !abilityCard.getRangeValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getRangeValues().size(); i++) {
                        // Preserve AoE sentinel values
                        if ("-1".equals(abilityCard.getRangeValues().get(i))) {
                            continue;
                        }
                        abilityCard.getRangeValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardShield(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getShield() != null) {
                abilityCard.setShield(value);
                if (abilityCard.getShieldValues() != null && !abilityCard.getShieldValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getShieldValues().size(); i++) {
                        abilityCard.getShieldValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardTarget(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getTarget() != null) {
                abilityCard.setTarget(value);
                if (abilityCard.getTargetValues() != null && !abilityCard.getTargetValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getTargetValues().size(); i++) {
                        // Preserve AoE sentinel values
                        if ("-1".equals(abilityCard.getTargetValues().get(i))) {
                            continue;
                        }
                        abilityCard.getTargetValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardPull(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getPull() != null) {
                abilityCard.setPull(value);
                if (abilityCard.getPullValues() != null && !abilityCard.getPullValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getPullValues().size(); i++) {
                        abilityCard.getPullValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardPush(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getPush() != null) {
                abilityCard.setPush(value);
                if (abilityCard.getPushValues() != null && !abilityCard.getPushValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getPushValues().size(); i++) {
                        abilityCard.getPushValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardJump(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getJump() != null) {
                abilityCard.setJump(value);
            }
        }
    }

    public void setAbilityCardRetaliate(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getRetaliate() != null) {
                abilityCard.setRetaliate(value);
                if (abilityCard.getRetaliateValues() != null && !abilityCard.getRetaliateValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getRetaliateValues().size(); i++) {
                        abilityCard.getRetaliateValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardPierce(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getPierce() != null) {
                abilityCard.setPierce(value);
                if (abilityCard.getPierceValues() != null && !abilityCard.getPierceValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getPierceValues().size(); i++) {
                        abilityCard.getPierceValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardInitiative(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getInitiative() != null) {
                abilityCard.setInitiative(value);
            }
        }
    }

    public void setAbilityCardDiscard(DiscardEnum value) {
        for (AbilityCard abilityCard : abilityCards.values()) {      
            abilityCard.setDiscard(value);
        }
    }

    public void setAbilityCardConsumes(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getConsumes() != null) {
                abilityCard.setConsumes(value);
            }
        }
    }

    public void setAbilityCardInfuse(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getInfuse() != null) {
                abilityCard.setInfuse(value);
            }
        }
    }

    public void setAbilityCardXP(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getXP() != null) {
                abilityCard.setXP(value);
                if (abilityCard.getXpValues() != null && !abilityCard.getXpValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getXpValues().size(); i++) {
                        abilityCard.getXpValues().set(i, value);
                    }
                }
            }
        }
    }

    public void setAbilityCardLoot(String value) {
        for (AbilityCard abilityCard : abilityCards.values()) {
            if (abilityCard.getLoot() != null) {
                abilityCard.setLoot(value);
                if (abilityCard.getLootValues() != null && !abilityCard.getLootValues().isEmpty()) {
                    for (int i = 0; i < abilityCard.getLootValues().size(); i++) {
                        abilityCard.getLootValues().set(i, value);
                    }
                }
            }
        }
    }
}
