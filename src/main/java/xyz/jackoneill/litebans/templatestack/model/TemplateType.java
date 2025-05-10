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
}
