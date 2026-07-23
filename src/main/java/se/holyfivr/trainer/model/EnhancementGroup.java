package se.holyfivr.trainer.model;

/* ======================================================================== */
/* One class's enhancements found in a save file: the class name and how    */
/* many of its cards are enhanced. Drives the "Reset enhancements" list in   */
/* the savefile editor (one row/button per class that has enhancements).    */
/* ======================================================================== */
public class EnhancementGroup {

    private final String className;
    private final int cardCount;

    public EnhancementGroup(String className, int cardCount) {
        this.className = className;
        this.cardCount = cardCount;
    }

    public String getClassName() {
        return className;
    }

    public int getCardCount() {
        return cardCount;
    }
}
