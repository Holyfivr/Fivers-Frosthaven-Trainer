package se.holyfivr.trainer.model.enums;

public enum ItemAttribute {
    
    STRING_ID     ("StringID"             ),
    ITEM_NAME     ("ItemName"             ),
    ID            ("ID"                   ),
    COST          ("Cost"                 ),
    TOTAL_IN_GAME ("TotalInGame"          ),
    SLOT          ("Slot"                 ),
    RARITY        ("Rarity"               ),
    USAGE         ("Usage"                ),
    PROSPERITY_REQ("ProsperityRequirement"),
    OMOVE         ("OMove"                ),
    AMOVE         ("AMove"                ),
    SHIELD_VALUE  ("ShieldValue"          ),
    CONDITIONS    ("Conditions"           ),
    CONSUMES      ("Consumes"             ),
    RETALIATE     ("Retaliate"            ),
    MOVE          ("Move"                 ),
    PUSH          ("Push"                 ),
    JUMP          ("Jump"                 ),
    INFUSE        ("Infuse"               ),
    PULL          ("Pull"                 ),
    HEAL          ("Heal"                 ),
    ATTACK        ("Attack"               ),
    RANGE         ("Range"                ),
    TARGET        ("Target"               ),
    SHIELD        ("Shield"               ),
    PIERCE        ("Pierce"               ),
    DAMAGE        ("Damage"               );
    


    private final String value;

    ItemAttribute(String value) {
        this.value = value;
    }

    public String get() {
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

