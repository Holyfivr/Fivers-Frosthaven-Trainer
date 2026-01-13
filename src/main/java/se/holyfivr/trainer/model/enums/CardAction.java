package se.holyfivr.trainer.model.enums;

public enum CardAction {
    SET_INITIATIVE("setInitiative"),
    SET_DISCARD("setDiscard"),
    SET_DAMAGE("setDamage"),
    SET_ATTACK("setAttack"),
    SET_HEAL("setHeal"),
    SET_MOVE("setMove"),
    SET_RANGE("setRange"),
    SET_SHIELD("setShield"),
    SET_HEALTH("setHealth"),
    SET_TARGET("setTarget"),
    SET_PULL("setPull"),
    SET_PUSH("setPush"),
    SET_JUMP("setJump"),
    SET_RETALIATE("setRetaliate"),
    SET_PIERCE("setPierce"),
    SET_CONSUMES("setConsumes"),
    SET_INFUSES("setInfuses"),
    SET_XP("setXP");

    private final String attribute;

    CardAction(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public static CardAction fromString(String value) {
        for (CardAction a : CardAction.values()) {
            if (a.attribute.equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unknown attribute: " + value);
    }

}
