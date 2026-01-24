package se.holyfivr.trainer.model.enums;

public enum CardAttribute {
    NAME            ("Name"),
    INITIATIVE      ("Initiative"),
    DISCARD         ("Discard"),
    RETALIATE       ("Retaliate"),
    PIERCE          ("Pierce"),
    CONSUMES        ("Consumes"),
    XP              ("XP"),
    DAMAGE          ("Damage"),
    ATTACK          ("Attack"),
    HEAL            ("Heal"),
    MOVE            ("Move"),
    RANGE           ("Range"),
    SHIELD          ("Shield"),
    HEALTH          ("Health"),
    PULL            ("Pull"),
    PUSH            ("Push"),
    TARGET          ("Target"),
    LOOT            ("Loot"),
    JUMP            ("Jump"),
    INFUSE          ("Infuse");

    private final String attribute;

    CardAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String get() {
        return attribute;
    }

    public static CardAttribute fromString(String value) {
        for (CardAttribute a : CardAttribute.values()) {
            if (a.attribute.equalsIgnoreCase(value)) {
                return a;
            }
        }
        return null;
    }
}
