package se.holyfivr.trainer.core.utils;

import java.util.regex.Pattern;



public final class PatternRepository {

    private PatternRepository() {
    }

    // Parser identifiers
    public static final Pattern CHARACTER_PARSER_PATTERN = Pattern.compile("(?s)(?m).*^\\s*Character\\s*$.*");
    public static final Pattern GAMEMODE_PARSER_PATTERN = Pattern.compile("(?s)(?m).*^\\s*GameMode\\s*$.*");
    public static final Pattern ITEM_PARSER_PATTERN = Pattern.compile("(?s)(?m).*^\\s*ItemCard\\s*$.*");
    public static final Pattern FH_ITEM_PARSER_PATTERN = Pattern.compile("(?s)(?m).*^\\s*FHItem\\s*$.*");
    public static final Pattern ABILITY_CARD_PARSER_PATTERN = Pattern.compile("(?s)(?m).*^\\s*AbilityCard\\s*$.*");

    // Character Patterns
    public static final Pattern UNLOCKED_CHARACTER_PATTERN = Pattern.compile("\\bUnlockedClasses:\\s*\\[.*?\\]");
    public static final Pattern CARD_AMOUNT_PATTERN = Pattern.compile("\\bNumberAbilityCardsInBattle:\\s*\\d+");
    public static final Pattern HEALTH_TABLE_PATTERN = Pattern.compile("\\bHealthTable:\\s*\\[.*?\\]");
    
    // Attribute Patterns
    public static final Pattern VALID_ATTRIBUTE_PATTERN = Pattern.compile(":[ \\t]*(?:(?:\\r?\\n[ \\t]*)+(?:Amount|Strength):[ \\t]*)?([+-]?\\d+)");



    // Item Patterns
    public static final Pattern TOTAL_IN_GAME_PATTERN = Pattern.compile("\\bTotalInGame:\\s*\\d+");
    public static final Pattern COST_PATTERN = Pattern.compile("\\bCost:\\s*\\d+");
    public static final Pattern USAGE_PATTERN = Pattern.compile("\\bUsage:\\s*\\w+");
    public static final Pattern PROSPERITY_PATTERN = Pattern.compile("\\bProsperityRequirement:\\s*\\d+");


    public static final Pattern CONSUMES_PATTERN = Pattern.compile("(?mi)^Consumes:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");
    public static final Pattern INFUSE_PATTERN = Pattern.compile("(?mi)^Infuse:\\s*(?!Any\\b)(\\[[A-Za-z0-9_]+\\]|[A-Za-z0-9_]+)\\s*$");
    
    // general attribute Patterns
    public static final Pattern JUMP_PATTERN = Pattern.compile("\\bJump:\\s*(True|False)");




    public static final Pattern CONDITIONS_PATTERN = Pattern.compile("\\bConditions:\\s*(\\[.*?\\]|\\w+)");

    // FHItem Patterns
    public static final Pattern GOLD_COST_PATTERN = Pattern.compile("\\bGoldCost:\\s*\\d+");
    public static final Pattern QUANTITY_PATTERN = Pattern.compile("(?m)\\bQuantity:\\s*\\d+");
    




}
