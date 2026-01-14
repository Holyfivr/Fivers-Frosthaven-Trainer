package se.holyfivr.trainer.model;

/**
 * Represents an Item parsed from the ruleset file.
 * This is a POJO designed to hold all possible fields of interest found in the
 * item definitions.
 * All fields are nullable, so we can handle all items, no matter what fields
 * they
 * use.
 */
public class Item {
    public static final String UPPERCASE_LETTER = "([A-Z])";
    // General item info
    public String itemName;
    public String id; // numeric ID - used to identify the FHItem version of the item when rewriting
    public String stringId; // "nameID"
    public String totalInGame; // integer
    public String cost; // integer
    public String slot; // Head/Body/Legs/SmallItem/OneHand/TwoHand/QuestItem
    public String rarity; // Rare/Relic
    public String usage; // Spent/Unrestricted/Consumed/Flip
    public String prosperityRequirement;
    public String consumes;
    public String infuse;

    // Effects (from each "Data" block)
    public String heal; // integer
    public String attack; // integer
    public String range; // integer
    public String target; // integer (-1 = adjacent(or rather aoe, depending on range))
    public String shield; // integer
    public String shieldValue;
    public String retaliate; // integer
    public String move; // integer
    public String oMove;
    public String aMove;
    public String pull; // integer
    public String push; // integer
    public String jump; // bool
    public String isMelee; // bool
    public String conditions; // (Muddle, Stun, Poison, Immobilize, Disarm, Stun, Curse, Bless, Strengthen,
                              // Invisible, Brittle, Bane, Impair)

    // Conditions & Status Effects ... We're leaving these out for now
    /*
     * public Boolean muddle;
     * public Boolean poison;
     * public Boolean wound;
     * public Boolean immobilize;
     * public Boolean disarm;
     * public Boolean stun;
     * public Boolean curse;
     * public Boolean bless;
     * public Boolean strengthen;
     * public Boolean invisible;
     * public Boolean brittle;
     * public Boolean bane;
     * public Boolean impair;
     */

    public Item() {
    }

    @Override
    public String toString() {
        return "";
    }

    public String getItemName() {
        itemName = stringId.substring(0, (stringId.length() - 2)).replaceAll(UPPERCASE_LETTER, " $1");

        if (itemName.endsWith("2")) {
            itemName = stringId.substring(0, (stringId.length() - 3)).replaceAll(UPPERCASE_LETTER, " $1");
            itemName = itemName + " (Back)";
        }

        return itemName;
    }

    public String getId() {
        return id;
    }

    public Item setId(String id) {
        this.id = id;
        return this;
    }

    public String getStringId() {
        return stringId;
    }

    public Item setStringId(String stringId) {
        this.stringId = stringId;
        return this;
    }

    public String getTotalInGame() {
        return totalInGame;
    }

    public Item setTotalInGame(String totalInGame) {
        this.totalInGame = totalInGame;
        return this;
    }

    public String getCost() {
        return cost;
    }

    public Item setCost(String cost) {
        this.cost = cost;
        return this;
    }

    public String getSlot() {
        return slot;
    }

    public Item setSlot(String slot) {
        this.slot = slot;
        return this;
    }

    public String getRarity() {
        return rarity;
    }

    public Item setRarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public String getUsage() {
        return usage;
    }

    public Item setUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public String getProsperityRequirement() {
        return prosperityRequirement;
    }

    public Item setProsperityRequirement(String prosperityRequirement) {
        this.prosperityRequirement = prosperityRequirement;
        return this;
    }

    public String getConsumes() {
        return consumes;
    }

    public Item setConsumes(String consumes) {
        this.consumes = consumes;
        return this;
    }

    public String getInfuse() {
        return infuse;
    }

    public Item setInfuse(String infuse) {
        this.infuse = infuse;
        return this;
    }

    public String getHeal() {
        return heal;
    }

    public Item setHeal(String heal) {
        this.heal = heal;
        return this;
    }

    public String getAttack() {
        return attack;
    }

    public Item setAttack(String attack) {
        this.attack = attack;
        return this;
    }

    public String getRange() {
        return range;
    }

    public Item setRange(String range) {
        this.range = range;
        return this;
    }

    public String getTarget() {
        return target;
    }

    public Item setTarget(String target) {
        this.target = target;
        return this;
    }

    public String getShield() {
        return shield;
    }

    public Item setShield(String shield) {
        this.shield = shield;
        return this;
    }

    public String getShieldValue() {
        return shieldValue;
    }

    public Item setShieldValue(String shieldValue) {
        this.shieldValue = shieldValue;
        return this;
    }

    public String getRetaliate() {
        return retaliate;
    }

    public Item setRetaliate(String retaliate) {
        this.retaliate = retaliate;
        return this;
    }

    public String getMove() {
        return move;
    }

    public Item setMove(String move) {
        this.move = move;
        return this;
    }

    public String getOMove() {
        return oMove;
    }

    public Item setOMove(String oMove) {
        this.oMove = oMove;
        return this;
    }

    public String getAMove() {
        return aMove;
    }

    public Item setAMove(String aMove) {
        this.aMove = aMove;
        return this;
    }

    public String getPull() {
        return pull;
    }

    public Item setPull(String pull) {
        this.pull = pull;
        return this;
    }

    public String getPush() {
        return push;
    }

    public Item setPush(String push) {
        this.push = push;
        return this;
    }

    public String getJump() {
        return jump;
    }

    public Item setJump(String jump) {
        this.jump = jump;
        return this;
    }

    public String getIsMelee() {
        return isMelee;
    }

    public Item setIsMelee(String isMelee) {
        this.isMelee = isMelee;
        return this;
    }

    public String getConditions() {
        return conditions;
    }

    public Item setConditions(String conditions) {
        this.conditions = conditions;
        return this;
    }

}
