package se.holyfivr.trainer.model;

import java.util.ArrayList;
import java.util.List;

public class PlayerCharacter {

    private String name;
    private String cardAmount;
    private String maxHealthLevelOne;
    private String maxHealthLevelTwo;
    private String maxHealthLevelThree;
    private String maxHealthLevelFour;
    private String maxHealthLevelFive;
    private String maxHealthLevelSix;
    private String maxHealthLevelSeven;
    private String maxHealthLevelEight;
    private String maxHealthLevelNine;

    public PlayerCharacter(
    String name,
    String cardAmount,
    String maxHealthLevelOne,
    String maxHealthLevelTwo,
    String maxHealthLevelThree,
    String maxHealthLevelFour,
    String maxHealthLevelFive,
    String maxHealthLevelSix,
    String maxHealthLevelSeven,
    String maxHealthLevelEight,
    String maxHealthLevelNine) {

        this.name = name;
        this.cardAmount = cardAmount;
        this.maxHealthLevelOne = maxHealthLevelOne;
        this.maxHealthLevelTwo = maxHealthLevelTwo;
        this.maxHealthLevelThree = maxHealthLevelThree;
        this.maxHealthLevelFour = maxHealthLevelFour;
        this.maxHealthLevelFive = maxHealthLevelFive;
        this.maxHealthLevelSix = maxHealthLevelSix;
        this.maxHealthLevelSeven = maxHealthLevelSeven;
        this.maxHealthLevelEight = maxHealthLevelEight;
        this.maxHealthLevelNine = maxHealthLevelNine;       
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

    public String getMaxHealthLevelOne() {
        return maxHealthLevelOne;
    }

    public void setMaxHealthLevelOne(String maxHealthLevelOne) {
        this.maxHealthLevelOne = maxHealthLevelOne;
    }

    public String getMaxHealthLevelTwo() {
        return maxHealthLevelTwo;
    }

    public void setMaxHealthLevelTwo(String maxHealthLevelTwo) {
        this.maxHealthLevelTwo = maxHealthLevelTwo;
    }

    public String getMaxHealthLevelThree() {
        return maxHealthLevelThree;
    }

    public void setMaxHealthLevelThree(String maxHealthLevelThree) {
        this.maxHealthLevelThree = maxHealthLevelThree;
    }

    public String getMaxHealthLevelFour() {
        return maxHealthLevelFour;
    }

    public void setMaxHealthLevelFour(String maxHealthLevelFour) {
        this.maxHealthLevelFour = maxHealthLevelFour;
    }

    public String getMaxHealthLevelFive() {
        return maxHealthLevelFive;
    }

    public void setMaxHealthLevelFive(String maxHealthLevelFive) {
        this.maxHealthLevelFive = maxHealthLevelFive;
    }

    public String getMaxHealthLevelSix() {
        return maxHealthLevelSix;
    }

    public void setMaxHealthLevelSix(String maxHealthLevelSix) {
        this.maxHealthLevelSix = maxHealthLevelSix;
    }

    public String getMaxHealthLevelSeven() {
        return maxHealthLevelSeven;
    }

    public void setMaxHealthLevelSeven(String maxHealthLevelSeven) {
        this.maxHealthLevelSeven = maxHealthLevelSeven;
    }

    public String getMaxHealthLevelEight() {
        return maxHealthLevelEight;
    }

    public void setMaxHealthLevelEight(String maxHealthLevelEight) {
        this.maxHealthLevelEight = maxHealthLevelEight;
    }

    public String getMaxHealthLevelNine() {
        return maxHealthLevelNine;
    }

    public void setMaxHealthLevelNine(String maxHealthLevelNine) {
        this.maxHealthLevelNine = maxHealthLevelNine;
    }

}
