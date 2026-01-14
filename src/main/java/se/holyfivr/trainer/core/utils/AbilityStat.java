package se.holyfivr.trainer.core.utils;

import java.util.List;
import java.util.function.Function;

import se.holyfivr.trainer.model.AbilityCard;

public enum AbilityStat {
    ATTACK("Attack", AbilityCard::getAttackValues, AbilityCard::getAttack),
    DAMAGE("Damage", AbilityCard::getDamageValues, AbilityCard::getDamage),
    HEAL("Heal", AbilityCard::getHealValues, AbilityCard::getHeal),
    MOVE("Move", AbilityCard::getMoveValues, AbilityCard::getMove),
    RANGE("Range", AbilityCard::getRangeValues, AbilityCard::getRange),
    SHIELD("Shield", AbilityCard::getShieldValues, AbilityCard::getShield),
    XP("XP", AbilityCard::getXpValues, AbilityCard::getXP),
    TARGET("Target", AbilityCard::getTargetValues, AbilityCard::getTarget),
    PULL("Pull", AbilityCard::getPullValues, AbilityCard::getPull),
    PUSH("Push", AbilityCard::getPushValues, AbilityCard::getPush),
    RETALIATE("Retaliate", AbilityCard::getRetaliateValues, AbilityCard::getRetaliate),
    LOOT("Loot", AbilityCard::getLootValues, AbilityCard::getLoot),
    PIERCE("Pierce", AbilityCard::getPierceValues, AbilityCard::getPierce);

    private final String key;
    private final Function<AbilityCard, List<String>> listGetter;
    private final Function<AbilityCard, String> singleGetter;

    AbilityStat(String key,
                Function<AbilityCard, List<String>> listGetter,
                Function<AbilityCard, String> singleGetter) {
        this.key = key;
        this.listGetter = listGetter;
        this.singleGetter = singleGetter;
    }

    public String getKey() {
        return key;
    }

    public List<String> getList(AbilityCard card) {
        return listGetter.apply(card);
    }

    public String getSingle(AbilityCard card) {
        return singleGetter.apply(card);
    }
}