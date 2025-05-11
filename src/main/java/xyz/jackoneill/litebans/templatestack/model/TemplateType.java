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
}
