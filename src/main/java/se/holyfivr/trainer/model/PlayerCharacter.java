package se.holyfivr.trainer.model;

public class PlayerCharacter {

    private String name;
    private String displayName;
    private boolean isAvailableFromStart;

    private String cardAmount;
    private String hpLvlOne;
    private String hpLvlTwo;
    private String hpLvlThree;
    private String hpLvlFour;
    private String hpLvlFive;
    private String hpLvlSix;
    private String hpLvlSeven;
    private String hpLvlEight;
    private String hpLvlNine;

    public PlayerCharacter(
            String name,
            String cardAmount,
            String hpLvlOne,
            String hpLvlTwo,
            String hpLvlThree,
            String hpLvlFour,
            String hpLvlFive,
            String hpLvlSix,
            String hpLvlSeven,
            String hpLvlEight,
            String hpLvlNine) {

        this.name = name;
        this.cardAmount = cardAmount;
        this.hpLvlOne = hpLvlOne;
        this.hpLvlTwo = hpLvlTwo;
        this.hpLvlThree = hpLvlThree;
        this.hpLvlFour = hpLvlFour;
        this.hpLvlFive = hpLvlFive;
        this.hpLvlSix = hpLvlSix;
        this.hpLvlSeven = hpLvlSeven;
        this.hpLvlEight = hpLvlEight;
        this.hpLvlNine = hpLvlNine;
    }

    public String getDisplayName() {

        // cuts out the "ID" part from the name
        if (name != null) {
            displayName = name.substring(0, name.length() - 2);
        }
        return displayName;
    }

    public String getName() {
        return name;
    }

    public PlayerCharacter setName(String name) {
        this.name = name;
        return this;
    }

    public String getCardAmount() {
        return cardAmount;
    }

    public PlayerCharacter setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
        return this;
    }

    public String getHpLvlOne() {
        return hpLvlOne;
    }

    public PlayerCharacter setHpLvlOne(String hpLvlOne) {
        this.hpLvlOne = hpLvlOne;
        return this;
    }

    public String getHpLvlTwo() {
        return hpLvlTwo;
    }

    public PlayerCharacter setHpLvlTwo(String hpLvlTwo) {
        this.hpLvlTwo = hpLvlTwo;
        return this;
    }

    public String getHpLvlThree() {
        return hpLvlThree;
    }

    public PlayerCharacter setHpLvlThree(String hpLvlThree) {
        this.hpLvlThree = hpLvlThree;
        return this;
    }

    public String getHpLvlFour() {
        return hpLvlFour;
    }

    public PlayerCharacter setHpLvlFour(String hpLvlFour) {
        this.hpLvlFour = hpLvlFour;
        return this;
    }

    public String getHpLvlFive() {
        return hpLvlFive;
    }

    public PlayerCharacter setHpLvlFive(String hpLvlFive) {
        this.hpLvlFive = hpLvlFive;
        return this;
    }

    public String getHpLvlSix() {
        return hpLvlSix;
    }

    public PlayerCharacter setHpLvlSix(String hpLvlSix) {
        this.hpLvlSix = hpLvlSix;
        return this;
    }

    public String getHpLvlSeven() {
        return hpLvlSeven;
    }

    public PlayerCharacter setHpLvlSeven(String hpLvlSeven) {
        this.hpLvlSeven = hpLvlSeven;
        return this;
    }

    public String getHpLvlEight() {
        return hpLvlEight;
    }

    public PlayerCharacter setHpLvlEight(String hpLvlEight) {
        this.hpLvlEight = hpLvlEight;
        return this;
    }

    public String getHpLvlNine() {
        return hpLvlNine;
    }

    public PlayerCharacter setHpLvlNine(String hpLvlNine) {
        this.hpLvlNine = hpLvlNine;
        return this;
    }

    public boolean isAvailableFromStart() {
        return isAvailableFromStart;
    }

    public PlayerCharacter setAvailableFromStart(boolean isAvailableFromStart) {
        this.isAvailableFromStart = isAvailableFromStart;
        return this;
    }

}
