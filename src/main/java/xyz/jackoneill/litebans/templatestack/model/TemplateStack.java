package xyz.jackoneill.litebans.templatestack.model;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import xyz.jackoneill.litebans.templatestack.LiteBansManager;
import xyz.jackoneill.litebans.templatestack.TemplateStackPlugin;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.util.*;
import java.util.stream.Stream;

@Getter
public class TemplateStack {
    private final String name;
    private final int expirationDays;

    private Template banTemplate = null;
    private Template muteTemplate = null;
    private Template kickTemplate = null;
    private Template warnTemplate = null;

    public TemplateStack(String name, int expirationDays) {
        this.name = name;
        this.expirationDays = expirationDays;
    }

    public void setBanTemplate(Template template) {
        this.banTemplate = applyExpirationDay(template);
    }

    public void setMuteTemplate(Template template) {
        this.muteTemplate = applyExpirationDay(template);
    }

    public void setKickTemplate(Template template) {
        this.kickTemplate = applyExpirationDay(template);
    }

    public void setWarnTemplate(Template template) {
        this.warnTemplate = applyExpirationDay(template);
    }

    private Template applyExpirationDay(Template template) {
        if (template != null) {
            template.setExpirationDays(this.expirationDays);
        }
        return template;
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

    public int getTemplateCount() {
        return (int) Stream.of(this.banTemplate, this.muteTemplate, this.warnTemplate, this.kickTemplate).filter(Objects::nonNull).count();
    }

    @Override
    public String toString() {
        return this.name + ": expirationDays=" + this.expirationDays
                + ",templates=" + this.getTemplateCount();
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

    public Map<Template, List<Punishment>> getAllPunishments(OfflinePlayer player) {
        Map<Template, List<Punishment>> punishmentMap = new LinkedHashMap<>();
        LiteBansManager manager = TemplateStackPlugin.getInstance().getLiteBansManager();

        Stream.of(banTemplate, muteTemplate, kickTemplate, warnTemplate)
                .filter(Objects::nonNull)
                .forEach(t -> punishmentMap.put(t, manager.getActiveLadderPunishments(player, t)));
        return punishmentMap;
    }

    public @Nullable Template getNextPunishment(Map<Template, List<Punishment>> playerPunishments) {
        Template previousIterationPunishment = null;

        Template lastOrderPunishment = null;
        Template selectedTemplate = null;

        for (Map.Entry<Template, List<Punishment>> entry : playerPunishments.entrySet()) {
            lastOrderPunishment = entry.getKey();
            if (!entry.getValue().isEmpty()) {
                // there are already punishments in this template
                if (entry.getValue().size() < entry.getKey().getPunishments() || entry.getKey().getPunishments() == -1) {
                    // template accepts more punishments, or template is last = infinite punishments
                    selectedTemplate = entry.getKey();
                }
                if (entry.getValue().size() >= entry.getKey().getPunishments()) {
                    // template has reach its limit, use previous template (should be set by previous iteration)
                    selectedTemplate = previousIterationPunishment;
                }
            }
            if (selectedTemplate != null) {
                break;
            }
            previousIterationPunishment = entry.getKey();
        }

        if (selectedTemplate == null) {
            selectedTemplate = lastOrderPunishment;
        }
        return selectedTemplate;
    }
}
