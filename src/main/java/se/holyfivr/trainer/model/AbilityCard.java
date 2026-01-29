package se.holyfivr.trainer.model;

import java.util.ArrayList;
import java.util.List;

import se.holyfivr.trainer.model.enums.DiscardEnum;

public class AbilityCard {
    public static final String UPPERCASE_LETTER = "([A-Z])";

    public String name;
    public String className;
    public String cardName;
    public String health;
    public String heal;
    public String damage;
    public String shield;
    public String range;
    public String attack;
    public String move;
    public String jump;
    public String target;
    public String pull;
    public String push;
    public String retaliate;
    public String loot;
    public DiscardEnum discard; // format in ruleset: [Discard/Lost, Discard/Lost]
    public String initiative;
    public String pierce;
    public String infuse;
    public String consumes;
    public String XP;

    // MVP multi-attribute support: store every occurrence in the block, in order.
    public List<String> attackValues = new ArrayList<>();
    public List<String> damageValues = new ArrayList<>();
    public List<String> healValues = new ArrayList<>();
    public List<String> moveValues = new ArrayList<>();
    public List<String> rangeValues = new ArrayList<>();
    public List<String> shieldValues = new ArrayList<>();
    public List<String> targetValues = new ArrayList<>();
    public List<String> pullValues = new ArrayList<>();
    public List<String> pushValues = new ArrayList<>();
    public List<String> retaliateValues = new ArrayList<>();
    public List<String> pierceValues = new ArrayList<>();
    public List<String> xpValues = new ArrayList<>();
    public List<String> lootValues = new ArrayList<>();
    
    

    /* ======================================================== */
    /*              Editing card visuals in game:               */
    /*                                                          */
    /* Some cards have: <sprite name="x"> y                     */
    /* In their blocks, where x is the name attribute and y is  */
    /* the value. These may not change automatically but we     */
    /* might be forced to add logic to also change these cases  */
    /* in order to display the correct values in the game.      */
    /* This needs to be done in the rulesetSaver.               */
    /* In some cases in the ruleset the text is:                */
    /* <sprite name="x"> *x*                                    */
    /* This means it MIGHT be possible to change all instances  */
    /* to this format. This remains to be tested.               */
    /* ======================================================== */


    // Full unchanged name
    public String getName() {
        return name;
    }

    public AbilityCard setName(String name) {
        this.name = name;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public AbilityCard setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getCardName() {
        return cardName;
    }

    public AbilityCard setCardName(String cardName) {
        this.cardName = cardName;
        return this;
    }

    public String getHealth() {
        return health;
    }

    public AbilityCard setHealth(String health) {
        this.health = health;
        return this;
    }

    public String getHeal() {
        return heal;
    }

    public AbilityCard setHeal(String heal) {
        this.heal = heal;
        return this;
    }

    public String getRange() {
        return range;
    }

    public AbilityCard setRange(String range) {
        this.range = range;
        return this;
    }

    public String getAttack() {
        return attack;
    }

    public AbilityCard setAttack(String attack) {
        this.attack = attack;
        return this;
    }

    public String getMove() {
        return move;
    }

    public AbilityCard setMove(String move) {
        this.move = move;
        return this;
    }

    public String getDamage() {
        return damage;
    }

    public AbilityCard setDamage(String damage) {
        this.damage = damage;
        return this;
    }

    public String getShield() {
        return shield;
    }

    public AbilityCard setShield(String shield) {
        this.shield = shield;
        return this;
    }

    public static String getUppercaseLetter() {
        return UPPERCASE_LETTER;
    }

    public String getJump() {
        return jump;
    }

    public AbilityCard setJump(String jump) {
        this.jump = jump;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public AbilityCard setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getPull() {
        return pull;
    }

    public AbilityCard setPull(String pull) {
        this.pull = pull;
        return this;
    }

    public String getPush() {
        return push;
    }

    public AbilityCard setPush(String push) {
        this.push = push;
        return this;
    }

    public String getRetaliate() {
        return retaliate;
    }

    public AbilityCard setRetaliate(String retaliate) {
        this.retaliate = retaliate;
        return this;
    }

    public String getLoot() {
        return loot;
    }

    public AbilityCard setLoot(String loot) {
        this.loot = loot;
        return this;
    }

    public DiscardEnum getDiscard() {
        return discard;
    }

    public AbilityCard setDiscard(DiscardEnum discard) {
        this.discard = discard;
        return this;
    }

    public String getInitiative() {
        return initiative;
    }

    public AbilityCard setInitiative(String initiative) {
        this.initiative = initiative;
        return this;
    }

    public String getPierce() {
        return pierce;
    }

    public AbilityCard setPierce(String pierce) {
        this.pierce = pierce;
        return this;
    }

    public String getInfuse() {
        return infuse;
    }

    public AbilityCard setInfuse(String infuse) {
        this.infuse = infuse;
        return this;
    }

    public String getConsumes() {
        return consumes;
    }

    public AbilityCard setConsumes(String consumes) {
        this.consumes = consumes;
        return this;
    }    

    public String getXP() {
        return XP;
    }

    public AbilityCard setXP(String xP) {
        XP = xP;
        return this;
    }

    public List<String> getAttackValues() {
        return attackValues;
    }

    public AbilityCard setAttackValues(List<String> attackValues) {
        this.attackValues = attackValues;
        return this;
    }

    public List<String> getDamageValues() {
        return damageValues;
    }

    public AbilityCard setDamageValues(List<String> damageValues) {
        this.damageValues = damageValues;
        return this;
    }

    public List<String> getHealValues() {
        return healValues;
    }

    public AbilityCard setHealValues(List<String> healValues) {
        this.healValues = healValues;
        return this;
    }

    public List<String> getMoveValues() {
        return moveValues;
    }

    public AbilityCard setMoveValues(List<String> moveValues) {
        this.moveValues = moveValues;
        return this;
    }

    public List<String> getRangeValues() {
        return rangeValues;
    }

    public AbilityCard setRangeValues(List<String> rangeValues) {
        this.rangeValues = rangeValues;
        return this;
    }

    public List<String> getShieldValues() {
        return shieldValues;
    }

    public AbilityCard setShieldValues(List<String> shieldValues) {
        this.shieldValues = shieldValues;
        return this;
    }

    public List<String> getTargetValues() {
        return targetValues;
    }

    public AbilityCard setTargetValues(List<String> targetValues) {
        this.targetValues = targetValues;
        return this;
    }

    public List<String> getPullValues() {
        return pullValues;
    }

    public AbilityCard setPullValues(List<String> pullValues) {
        this.pullValues = pullValues;
        return this;
    }

    public List<String> getPushValues() {
        return pushValues;
    }

    public AbilityCard setPushValues(List<String> pushValues) {
        this.pushValues = pushValues;
        return this;
    }

    public List<String> getRetaliateValues() {
        return retaliateValues;
    }

    public AbilityCard setRetaliateValues(List<String> retaliateValues) {
        this.retaliateValues = retaliateValues;
        return this;
    }

    public List<String> getPierceValues() {
        return pierceValues;
    }

    public AbilityCard setPierceValues(List<String> pierceValues) {
        this.pierceValues = pierceValues;
        return this;
    }

    public List<String> getXpValues() {
        return xpValues;
    }

    public AbilityCard setXpValues(List<String> xpValues) {
        this.xpValues = xpValues;
        return this;
    }

    public List<String> getLootValues() {
        return lootValues;
    }

    public AbilityCard setLootValues(List<String> lootValues) {
        this.lootValues = lootValues;
        return this;
    }

}
