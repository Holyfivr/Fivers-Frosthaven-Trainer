package se.holyfivr.trainer.model.enums;

public enum RulesetFileName {
    BASE_RULESET("Base.ruleset"),
    ORIGINAL_BACKUP("ORIGINAL_BACKUP.ruleset");

    private final String fileName;

    RulesetFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public static RulesetFileName fromString(String value) {
        for (RulesetFileName f : RulesetFileName.values()) {
            if (f.fileName.equalsIgnoreCase(value)) {
                return f;
            }
        }
        return null;
    }
}
