package se.holyfivr.trainer.model;

/**
 * Represents an Item parsed from the ruleset file.
 * This is a POJO designed to hold all possible fields of interest found in the
 * item definitions.
 * All fields are nullable, so we can handle all items, no matter what fields they
 * use.
 */
public class Item {

    // General item info
    public String itemName;
    public String stringId;             // "nameID"
    public String totalInGame;          // integer
    public String cost;                 // integer
    public String slot;                 // Head/Body/Legs/SmallItem/OneHand/TwoHand/QuestItem
    public String rarity;               // Rare/Relic
    public String usage;                //Spent/Unrestricted/Consumed/Flip
    public String prosperityRequirement;
    public String consumes;

    // Effects (from each "Data" block)
    public String heal;         // integer
    public String attack;       // integer
    public String range;        // integer
    public String target;       // integer (-1 = adjacent(or rather aoe, depending on range))
    public String shield;       // integer
    public String retaliate;    // integer
    public String move;         // integer
    public String pull;         // integer
    public String push;         // integer
    public String jump;         // bool
    public String isMelee;      // bool

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

        this.itemName = stringId.substring(0, (stringId.length() -2)).replaceAll("([A-Z])", " $1" );
        
        return itemName;
    }


    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public String getTotalInGame() {
        return totalInGame;
    }

    public void setTotalInGame(String totalInGame) {
        this.totalInGame = totalInGame;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getProsperityRequirement() {
        return prosperityRequirement;
    }

    public void setProsperityRequirement(String prosperityRequirement) {
        this.prosperityRequirement = prosperityRequirement;
    }

    public String getConsumes() {
        return consumes;
    }

    public void setConsumes(String consumes) {
        this.consumes = consumes;
    }

    public String getHeal() {
        return heal;
    }

    public void setHeal(String heal) {
        this.heal = heal;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getShield() {
        return shield;
    }

    public void setShield(String shield) {
        this.shield = shield;
    }

    public String getRetaliate() {
        return retaliate;
    }

    public void setRetaliate(String retaliate) {
        this.retaliate = retaliate;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getPull() {
        return pull;
    }

    public void setPull(String pull) {
        this.pull = pull;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }


    public String getJump() {
        return jump;
    }

    public void setJump(String jump) {
        this.jump = jump;
    }


    public String getIsMelee() {
        return isMelee;
    }

    public void setIsMelee(String isMelee) {
        this.isMelee = isMelee;
    }

}
