package xyz.jackoneill.litebans.templatestack.model;

import lombok.Getter;
import lombok.Setter;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
public class TemplateStack {
    private final String name;
    private final int expirationDays;

    @Setter
    private Template banTemplate = null;
    @Setter
    private Template muteTemplate = null;
    @Setter
    private Template kickTemplate = null;
    @Setter
    private Template warnTemplate = null;

    public TemplateStack(String name, int expirationDays) {
        this.name = name;
        this.expirationDays = expirationDays;
    }

    public boolean hasBanTemplate() {
        return this.banTemplate != null;
    }

    public boolean hasMuteTemplate() {
        return this.muteTemplate != null;
    }

    public boolean hasKickTemplate() {
        return this.kickTemplate != null;
    }

    public boolean hasWarnTemplate() {
        return this.warnTemplate != null;
    }

    public boolean hasAtLeastOneTemplate() {
        return (this.hasBanTemplate() || this.hasKickTemplate() || this.hasMuteTemplate() || this.hasWarnTemplate());
    }

    @Override
    public String toString() {
        return "TemplateStack(" + this.name
                + "): expirationDays=" + this.expirationDays
                + " templates=" + this.getTemplateCount();
    }

    public int getTemplateCount() {
        return (int) Stream.of(this.banTemplate, this.muteTemplate, this.warnTemplate, this.kickTemplate).filter(Objects::nonNull).count();
    }

    public void logConsole() {
        Log.info(this.toString());
        Stream.of(banTemplate, muteTemplate, kickTemplate, warnTemplate)
                .filter(Objects::nonNull)
                .forEach(t -> Log.debug(t.toString()));
    }

    public List<String> getInfo() {
        List<String> output = new ArrayList<>();
        output.add(this.toString());
        Stream.of(banTemplate, muteTemplate, kickTemplate, warnTemplate)
                .filter(Objects::nonNull)
                .map(Template::toString)
                .forEach(output::add);
        return output;
    }
}
