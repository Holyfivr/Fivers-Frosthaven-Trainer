package se.holyfivr.trainer.model.enums;

public enum ItemAttribute {
    STRING_ID("StringID"),
    ID("ID"),
    COST("Cost"),
    TOTAL_IN_GAME("TotalInGame"),
    SLOT("Slot"),
    RARITY("Rarity"),
    USAGE("Usage"),
    PROSPERITY_REQ("ProsperityRequirement"),
    CONSUMES("Consumes"),
    HEAL("Heal"),
    ATTACK("Attack"),
    RANGE("Range"),
    TARGET("Target"),
    SHIELD("Shield"),
    SHIELD_VALUE("ShieldValue"),
    RETALIATE("Retaliate"),
    MOVE("Move"),
    OMOVE("OMove"),
    AMOVE("AMove"),
    PULL("Pull"),
    PUSH("Push"),
    JUMP("Jump"),
    CONDITIONS("Conditions"),
    INFUSE("Infuse");

    private final String value;

    ItemAttribute(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ItemAttribute fromString(String value) {
        for (ItemAttribute attr : ItemAttribute.values()) {
            if (attr.value.equalsIgnoreCase(value)) {
                return attr;
            }
        }
        return null;
    }
}
