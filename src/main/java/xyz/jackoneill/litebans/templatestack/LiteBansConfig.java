package xyz.jackoneill.litebans.templatestack;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import xyz.jackoneill.litebans.templatestack.model.Template;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;
import xyz.jackoneill.litebans.templatestack.model.TemplateType;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class LiteBansConfig {

    private final TemplateStackPlugin plugin;
    private boolean valid = false;

    private final Map<TemplateType, Map<Integer, String>> templateNames = new HashMap<>();
    private final Map<TemplateType, Map<String, Integer>> templateIndices = new HashMap<>();

    private final List<TemplateStack> templateStacks = new ArrayList<>();

    public @Nullable TemplateStack getStackByName(String name) {
        return templateStacks.stream()
                .filter(stack -> stack.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    public LiteBansConfig(TemplateStackPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {

        this.valid = false;
        this.templateStacks.clear();
        this.templateNames.clear();
        this.templateIndices.clear();

        Plugin otherPlugin = Bukkit.getPluginManager().getPlugin("LiteBans");
        if (otherPlugin == null || !otherPlugin.isEnabled()) {
            Log.error("LiteBans is not enabled");
            return;
        }

        File configFile = new File(otherPlugin.getDataFolder(), "templates.yml");
        if (!configFile.exists()) {
            Log.error("LiteBans templates.yml not found");
            return;
        }

        FileConfiguration litebansTemplateConfig = YamlConfiguration.loadConfiguration(configFile);
        for (TemplateType type : TemplateType.values()) {
            Map<Integer, String> names = this.getIndexMap(litebansTemplateConfig, type);
            Map<String, Integer> indices = this.transposeMap(names);
            this.templateNames.put(type, names);
            this.templateIndices.put(type, indices);
        }
        this.loadTemplateStackConfiguration();
    }

    public void logConsole() {
        for (TemplateStack stack : this.templateStacks) {
            stack.logConsole();
        }
    }

    private void loadTemplateStackConfiguration() {
        ConfigurationSection allStacksConfig = this.plugin.getConfig().getConfigurationSection("template_stacks");
        final int defaultExpirationDays = this.plugin.getConfig().getInt("defaults.expiration_days", 90);
        final int defaultPunishments = this.plugin.getConfig().getInt("defaults.punishments", 1);

        if (allStacksConfig == null) {
            Log.error("invalid config - template_stacks not found");
            return;
        }
        Log.info("Loading TemplateStack Configuration");

        List<String> stackNames = new ArrayList<>(allStacksConfig.getKeys(false));
        for (String currentStackName : stackNames) {
            ConfigurationSection templateStackConfig = allStacksConfig.getConfigurationSection(currentStackName);
            if (templateStackConfig == null || !templateStackConfig.getBoolean("enabled", true)) {
                Log.debug("Skipping disabled TemplateStack " + currentStackName);
                continue;
            }

            if (!templateStackConfig.contains("templates")) {
                Log.warning("TemplateStack " + currentStackName + " has no stacks defined - ignoring");
                continue;
            }

            int expirationDays = templateStackConfig.getInt("expiration_days", defaultExpirationDays);
            TemplateStack stack = new TemplateStack(currentStackName, expirationDays);

            ConfigurationSection templatesConfig = templateStackConfig.getConfigurationSection("ladder");
            stack.setBanTemplate(this.getTemplate(TemplateType.BAN, templatesConfig, defaultPunishments, currentStackName));
            stack.setMuteTemplate(this.getTemplate(TemplateType.MUTE, templatesConfig, defaultPunishments, currentStackName));
            stack.setKickTemplate(this.getTemplate(TemplateType.KICK, templatesConfig, defaultPunishments, currentStackName));
            stack.setWarnTemplate(this.getTemplate(TemplateType.WARN, templatesConfig, defaultPunishments, currentStackName));

            Stream.of(stack.getBanTemplate(), stack.getMuteTemplate(), stack.getKickTemplate(), stack.getWarnTemplate())
                    .filter(Objects::nonNull)
                    .findFirst()
                    .ifPresent(template -> {
                        if(template.getPunishments() > -1) {
                            Log.debug("Top-most template " + template.getType() + ":" + template.getTemplate() + " in " + stack.getName() + " will use infinite number of punishments");
                            template.setPunishments(-1);
                        }
                    });

            if (stack.hasAtLeastOneTemplate()) {
                this.templateStacks.add(stack);
            } else {
                Log.debug("TemplateStack " + currentStackName + " has no assigned templates - ignoring");
            }
        }
        this.valid = true;

    }

    private @Nullable Template getTemplate(TemplateType type, ConfigurationSection section, int defaultPunishments, String stackName) {
        if (section == null || !section.contains(type.toString()) || !section.getBoolean(type + ".enabled", true)) {
            return null;
        }

        String templateName = section.getString(type + ".template");
        if (templateName == null) {
            Log.warning("StackTemplate " + stackName + "." + type + " appears to be enabled but is lacking a template. ignoring");
            return null;
        }

        int punishments = section.getInt(type + ".punishments", defaultPunishments);
        Template template = new Template(templateName, punishments, type);

        if (!this.templateIndices.containsKey(type)) {
            Log.warning("No LiteBan Templates found of type " + type + ". Skipping " + stackName + "." + templateName);
            return null;
        }
        if (!(this.templateIndices.get(type).containsKey(templateName))) {
            Log.warning("No LiteBans Template named " + templateName + " found in " + type + "-templates - ignoring");
            return null;
        }
        template.setIndex(this.templateIndices.get(type).get(templateName));
        return template;
    }

    public Map<Integer, String> getIndexMap(FileConfiguration litebansTemplateConfig, TemplateType type) {
        String templateKey = type.toString() + "-templates";
        Map<Integer, String> templateIndexMap = new LinkedHashMap<>();
        ConfigurationSection section = litebansTemplateConfig.getConfigurationSection(templateKey);
        if (section != null) {
            List<String> sectionKeys = new ArrayList<>(section.getKeys(false));
            for (int i = 0; i < sectionKeys.size(); i++) {
                templateIndexMap.put(i, sectionKeys.get(i));
            }
        }
        Log.debug(templateKey + ": " + templateIndexMap.size() + " LiteBan templates loaded");
        return templateIndexMap;
    }

    private Map<String, Integer> transposeMap(Map<Integer, String> input) {
        return input.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}
