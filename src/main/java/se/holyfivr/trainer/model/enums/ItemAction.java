package se.holyfivr.trainer.model.enums;

public enum ItemAction {
    SET_GOLD_COST     ("setGoldCost"),
    SET_DAMAGE        ("setDamage"),
    SET_RANGE         ("setRange"),
    SET_HEAL          ("setHeal"),
    SET_RETALIATE     ("setRetaliate"),
    SET_PROSPERITY_REQ("setProsperityReq"),
    SET_USAGE         ("setUsage"),
    SET_TOTAL_IN_GAME ("setTotalInGame"),
    SET_SHIELD        ("setShield"),
    SET_MOVEMENT      ("setMovement");

    private final String action;

    ItemAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static ItemAction fromString(String value) {
        for (ItemAction a : ItemAction.values()) {
            if (a.action.equalsIgnoreCase(value)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unknown action: " + value);
    }
}
