package xyz.jackoneill.litebans.templatestack.model;

public enum TemplateType {
    BAN,
    MUTE,
    WARN,
    KICK;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String getTableName() {
        return switch (this) {
            case BAN -> "{bans}";
            case MUTE -> "{mutes}";
            case KICK -> "{kicks}";
            case WARN -> "{warnings}";
        };
    }

    public String getPastTense() {
        return switch (this) {
            case BAN -> "banned";
            case MUTE -> "muted";
            case KICK -> "kicked";
            case WARN -> "warned";
        };
    }
}
