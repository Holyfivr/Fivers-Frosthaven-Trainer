package se.holyfivr.trainer.model;

import java.util.ArrayList;
import java.util.List;

import se.holyfivr.trainer.model.enums.DiscardEnum;

public class AbilityCard {
    private static final String UPPERCASE_LETTER = "([A-Z])";

    private String name;
    private String className;
    private String cardName;
    private String health;
    private String heal;
    private String damage;
    private String shield;
    private String range;
    private String attack;
    private String move;
    private String jump;
    private String target;
    private String pull;
    private String push;
    private String retaliate;
    private String loot;
    private DiscardEnum discard; // format in ruleset: [Discard/Lost, Discard/Lost]
    private String initiative;
    private String pierce;
    private String infuse;
    private String consumes;
    private String XP;

    // MVP multi-attribute support: store every occurrence in the block, in order.
    private List<String> attackValues    = new ArrayList<>();
    private List<String> damageValues    = new ArrayList<>();
    private List<String> healValues      = new ArrayList<>();
    private List<String> moveValues      = new ArrayList<>();
    private List<String> rangeValues     = new ArrayList<>();
    private List<String> shieldValues    = new ArrayList<>();
    private List<String> targetValues    = new ArrayList<>();
    private List<String> pullValues      = new ArrayList<>();
    private List<String> pushValues      = new ArrayList<>();
    private List<String> retaliateValues = new ArrayList<>();
    private List<String> pierceValues    = new ArrayList<>();
    private List<String> xpValues        = new ArrayList<>();
    private List<String> lootValues      = new ArrayList<>();

    // unused for now
    private List<String> consumeValues   = new ArrayList<>();
    private List<String> infuseValues    = new ArrayList<>();
    // 
    

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


    // These are unused for now
    public AbilityCard setConsumeValues(List<String> consumeValues) {
        this.consumeValues = consumeValues;
        return this;
    }

    public List<String> getConsumeValues() {
        return consumeValues;
    }

    public List<String> getInfuseValues() {
        return infuseValues;
    }

    public AbilityCard setInfuseValues(List<String> infuseValues) {
        this.infuseValues = infuseValues;
        return this;
    }  
    //
    

}
