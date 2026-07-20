package se.holyfivr.trainer.model;

/* ======================================================================== */
/* Represents a single editable integer field inside a save file (.dat),    */
/* e.g. Gold or Lumber. Besides the name and current value, it stores the   */
/* exact byte offset where the 4-byte little-endian value lives in the      */
/* file, so it can be overwritten in place when saving.                     */
/* ======================================================================== */
public class SaveField {

    // Default cap for fields without a canonical in-game maximum (gold, resources)
    public static final int NO_MAX = 9999;

    private final String name;
    private final int value;
    private final int offset;
    private final int max;
    private final int step;

    public SaveField(String name, int value, int offset) {
        this(name, value, offset, NO_MAX, 1);
    }

    public SaveField(String name, int value, int offset, int max) {
        this(name, value, offset, max, 1);
    }

    public SaveField(String name, int value, int offset, int max, int step) {
        this.name = name;
        this.value = value;
        this.offset = offset;
        this.max = max;
        this.step = step;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getOffset() {
        return offset;
    }

    public int getMax() {
        return max;
    }

    public int getStep() {
        return step;
    }
}
