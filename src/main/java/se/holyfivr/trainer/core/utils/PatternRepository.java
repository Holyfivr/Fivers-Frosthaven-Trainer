package se.holyfivr.trainer.core.utils;

import java.util.regex.Pattern;

/**
 * Central repository for all regular expressions used when parsing
 * and mutating the Frosthaven ruleset file.
 *
 * IMPORTANT:
 * These regex patterns are used for byte-exact, surgical modifications 
 * of an already trusted file format — NOT for semantic parsing.
 *
 * Parsers use control flow.
 * Savers use regex.
 * 
 * I hate regex because it's so damn confusing, but it's so powerful, 
 * so it's more or less unavoidable here.
 * 
 */
public final class PatternRepository {

    private PatternRepository() {
        // Utility class – no instances allowed
    }

    /* ====================================================================== */
    /*                           PARSER IDENTIFIERS                           */
    /* ====================================================================== */

    /**
     * Matches a full block that represents a Character parser section.
     *
     * (?s)  DOTALL        -> '.' also matches line breaks
     * (?m)  MULTILINE     -> '^' and '$' work per line
     *
     * The pattern ensures that somewhere in the block there is
     * a line that contains ONLY the word "Character".
     *
     * Used to identify block type before routing to a parser.
     */
    public static final Pattern CHARACTER_PARSER_PATTERN =
            Pattern.compile("(?s)(?m).*^\\s*Character\\s*$.*");

    /**
     * Same strategy as CHARACTER_PARSER_PATTERN,
     * but for GameMode blocks.
     */
    public static final Pattern GAMEMODE_PARSER_PATTERN =
            Pattern.compile("(?s)(?m).*^\\s*GameMode\\s*$.*");

    /**
     * Identifies ItemCard blocks.
     */
    public static final Pattern ITEM_PARSER_PATTERN =
            Pattern.compile("(?s)(?m).*^\\s*ItemCard\\s*$.*");

    /**
     * Identifies FHItem blocks.
     */
    public static final Pattern FH_ITEM_PARSER_PATTERN =
            Pattern.compile("(?s)(?m).*^\\s*FHItem\\s*$.*");

    /**
     * Identifies AbilityCard blocks.
     */
    public static final Pattern ABILITY_CARD_PARSER_PATTERN =
            Pattern.compile("(?s)(?m).*^\\s*AbilityCard\\s*$.*");


    /* ====================================================================== */
    /*                          CHARACTER-RELATED PATTERNS                    */
    /* ====================================================================== */

    /**
     * Matches the entire UnlockedClasses array:
     *
     *   UnlockedClasses: [ClassA, ClassB, ClassC]
     *
     * Used to replace the full array in one operation.
     *
     * .*? is non-greedy to avoid spanning multiple arrays.
     */
    public static final Pattern UNLOCKED_CHARACTER_PATTERN =
            Pattern.compile("\\bUnlockedClasses:\\s*\\[.*?\\]");

    /**
     * Matches:
     *
     *   NumberAbilityCardsInBattle: <digits>
     *
     * Used when updating character card limits.
     */
    public static final Pattern CARD_AMOUNT_PATTERN =
            Pattern.compile("\\bNumberAbilityCardsInBattle:\\s*\\d+");

    /**
     * Matches:
     *
     *   HealthTable: [x, x, x, x, x, x, x, x, x]
     *
     * Used to replace the full HP table in one pass.
     */
    public static final Pattern HEALTH_TABLE_PATTERN =
            Pattern.compile("\\bHealthTable:\\s*\\[.*?\\]");


    /* ====================================================================== */
    /*                          GENERIC ATTRIBUTE PATTERNS                    */
    /* ====================================================================== */

    /**
     * Matches numeric attributes with optional nested "Amount" or "Strength".
     *
     * Example matches:
     *
     *   Attack: +3
     *   Heal:
     *       Amount: 2
     *   Move:
     *       Strength: 4
     *
     * Breakdown:
     * - ':' followed by optional whitespace
     * - optionally matches one or more indented lines containing
     *   "Amount:" or "Strength:"
     * - captures the final numeric value (with optional + or -)
     *
     * Group 1 = the numeric value we want to replace.
     */
    public static final Pattern VALID_ATTRIBUTE_PATTERN =
            Pattern.compile(
                    ":[ \\t]*" +
                    "(?:(?:\\r?\\n[ \\t]*)+(?:Amount|Strength):[ \\t]*)?" +
                    "([+-]?\\d+)"
            );


    /* ====================================================================== */
    /*                               ITEM PATTERNS                            */
    /* ====================================================================== */

    /**
     * Matches:
     *
     *   TotalInGame: <digits>
     */
    public static final Pattern TOTAL_IN_GAME_PATTERN =
            Pattern.compile("\\bTotalInGame:\\s*\\d+");

    /**
     * Matches:
     *
     *   Cost: <digits>
     */
    public static final Pattern COST_PATTERN =
            Pattern.compile("\\bCost:\\s*\\d+");

    /**
     * Matches:
     *
     *   Usage: <word>
     *
     * Example: Usage: Consumed
     */
    public static final Pattern USAGE_PATTERN =
            Pattern.compile("\\bUsage:\\s*\\w+");

    /**
     * Matches:
     *
     *   ProsperityRequirement: <digits>
     */
    public static final Pattern PROSPERITY_PATTERN =
            Pattern.compile("\\bProsperityRequirement:\\s*\\d+");


    /* ====================================================================== */
    /*                       ABILITY CARD ELEMENT PATTERNS                    */
    /* ====================================================================== */

    /**
     * Matches a Consumes line, but explicitly excludes "Any":
     *
     *   Consumes: Fire
     *   Consumes: [Fire, Ice]
     *
     * Does NOT match:
     *   Consumes: Any  <-- We do not want to change these, because it can cause weird in-game behavior.
     *
     * Used when updating element consumption.
     */
    public static final Pattern CONSUMES_PATTERN =
            Pattern.compile("(?mi)^Consumes:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");

    /**
     * Same as CONSUMES_PATTERN, but for Infuse.
     */
    public static final Pattern INFUSE_PATTERN =
            Pattern.compile("(?mi)^Infuse:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");


    /* ====================================================================== */
    /*                        GENERAL ATTRIBUTE PATTERNS                      */
    /* ====================================================================== */

    /**
     * Matches boolean Jump attributes:
     * 
     *   Jump: True
     *   Jump: False
     * 
     * I'm not sure if this one is even worth having, since the only items
     * where the Jump attribute exists are the ones where it is always true.
     * Meaning that if we want to *add* Jump to an item that doesn't have it,
     * we would need to change the structure of the item block, which is
     * impossible with the current approach. So basically this only allows
     * us to remove Jump from items that have it. lol

     */
    public static final Pattern JUMP_PATTERN =
            Pattern.compile("\\bJump:\\s*(True|False)");

    /**
     * Matches Conditions attributes:
     *
     *   Conditions: Stun
     *   Conditions: [Poison, Wound]
     */
    public static final Pattern CONDITIONS_PATTERN =
            Pattern.compile("\\bConditions:\\s*(\\[.*?\\]|\\w+)");


    /* ====================================================================== */
    /*                              FH ITEM PATTERNS                          */
    /* ====================================================================== */

    /* The FHItem versions of the itemcards have differente names for goldcost/cost and quantity/amount
     *  so we need separate patterns for those.
     *
     * Matches:
     *
     *   GoldCost: <digits>
     */
    public static final Pattern GOLD_COST_PATTERN =
            Pattern.compile("\\bGoldCost:\\s*\\d+");

    /**
     * Matches Quantity lines in FHItem blocks.
     *
     * (?m) MULTILINE is required so ^ and $ work per line.
     */
    public static final Pattern QUANTITY_PATTERN =
            Pattern.compile("(?m)\\bQuantity:\\s*\\d+");

}
