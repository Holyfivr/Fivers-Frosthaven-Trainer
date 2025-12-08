package se.holyfivr.trainer.model;

public class PlayerCharacter {

    private String name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(String cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getHpLvlOne() {
        return hpLvlOne;
    }

    public void setHpLvlOne(String hpLvlOne) {
        this.hpLvlOne = hpLvlOne;
    }

    public String getHpLvlTwo() {
        return hpLvlTwo;
    }

    public void setHpLvlTwo(String hpLvlTwo) {
        this.hpLvlTwo = hpLvlTwo;
    }

    public String getHpLvlThree() {
        return hpLvlThree;
    }

    public void setHpLvlThree(String hpLvlThree) {
        this.hpLvlThree = hpLvlThree;
    }

    public String getHpLvlFour() {
        return hpLvlFour;
    }

    public void setHpLvlFour(String hpLvlFour) {
        this.hpLvlFour = hpLvlFour;
    }

    public String getHpLvlFive() {
        return hpLvlFive;
    }

    public void setHpLvlFive(String hpLvlFive) {
        this.hpLvlFive = hpLvlFive;
    }

    public String getHpLvlSix() {
        return hpLvlSix;
    }

    public void setHpLvlSix(String hpLvlSix) {
        this.hpLvlSix = hpLvlSix;
    }

    public String getHpLvlSeven() {
        return hpLvlSeven;
    }

    public void setHpLvlSeven(String hpLvlSeven) {
        this.hpLvlSeven = hpLvlSeven;
    }

    public String getHpLvlEight() {
        return hpLvlEight;
    }

    public void setHpLvlEight(String hpLvlEight) {
        this.hpLvlEight = hpLvlEight;
    }

    public String getHpLvlNine() {
        return hpLvlNine;
    }

    public void setHpLvlNine(String hpLvlNine) {
        this.hpLvlNine = hpLvlNine;
    }

}
