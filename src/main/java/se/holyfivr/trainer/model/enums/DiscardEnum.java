package se.holyfivr.trainer.model.enums;

public enum DiscardEnum {
    NONE(null),
    DISCARD_DISCARD("[Discard,Discard]"),
    DISCARD_LOST   ("[Discard,Lost]"),
    LOST_DISCARD   ("[Lost,Discard]"),
    LOST_LOST      ("[Lost,Lost]");

    private final String value;

    DiscardEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DiscardEnum fromString(String value) {

        if (value == null || value.equals("null")) {
            return NONE;
        }
        for (DiscardEnum d : DiscardEnum.values()) {
            if (d.name().equalsIgnoreCase(value) || (d.value != null && d.value.equalsIgnoreCase(value))) {
                return d;
            }
        }
        return NONE;
    }
}