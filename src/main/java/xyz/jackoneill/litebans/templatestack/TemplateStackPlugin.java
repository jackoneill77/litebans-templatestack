package xyz.jackoneill.litebans.templatestack;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitWorker;
import xyz.jackoneill.litebans.templatestack.commands.TemplateStackCommand;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class TemplateStackPlugin extends JavaPlugin {

    @Getter
    private static TemplateStackPlugin instance;

    @Getter
    private LiteBansManager liteBansManager;

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        Log.set(getLogger());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Log.setDebug(getConfig().getBoolean("debug", false));

        if (!Bukkit.getPluginManager().isPluginEnabled("LiteBans")) {
            Log.error("LiteBans not found on this server - this plugin requires it to function. LiteBans-TemplateStack will be deactivated.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        liteBansManager = new LiteBansManager(this);

        this.loadLiteBansConfigAndLogResults();
        this.registerCommands();

        Log.info("initialized");
    }

    @Override
    public void onDisable() {
        // Wait up to 5 seconds for our async tasks to complete
        for (int i = 0; i < 50; ++i) {
            List<BukkitWorker> workers = Bukkit.getScheduler().getActiveWorkers();
            boolean taskFound = false;

            for (BukkitWorker worker : workers) {
                if (worker.getOwner().equals(this)) {
                    taskFound = true;
                    break;
                }
            }

            if (!taskFound) break;

            try {
                Thread.sleep(100); // msec
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void loadLiteBansConfigAndLogResults() {
        this.liteBansManager.loadConfig();
        if (this.liteBansManager.getConfig().isValid()) {
            Log.info("Configuration is valid.");
            this.liteBansManager.getConfig().logConsole();
        } else {
            Log.error("Configuration appears to be invalid. Check preceding log messages for details.");
        }
    }

    public void reloadPlugin() {
        this.reloadConfig();
        Log.setDebug(getConfig().getBoolean("debug", false));
        this.loadLiteBansConfigAndLogResults();
        this.registerCommandCompletions();
    }

    private void registerCommands() {
        commandManager = new PaperCommandManager(this);
        commandManager.enableUnstableAPI("help");

        List<String> templateStackCommands = getConfig().getStringList("command_alias");
        if (templateStackCommands.isEmpty()) {
            templateStackCommands.add("templatestack");
        }

        StringBuilder templateStackCommandArgs = new StringBuilder();
        for (String command : templateStackCommands) templateStackCommandArgs.append(command).append("|");
        templateStackCommandArgs = new StringBuilder(templateStackCommandArgs.substring(0, templateStackCommandArgs.length() - 1));

        String commandAlias = templateStackCommandArgs.toString();
        Log.debug("Registering command at " + commandAlias);
        commandManager.getCommandReplacements().addReplacements("alias", commandAlias);
        commandManager.registerCommand(new TemplateStackCommand(this));
        this.registerCommandCompletions();
    }

    private void registerCommandCompletions() {
        if (this.liteBansManager.getConfig().isValid()) {
            List<String> stacks = this.liteBansManager.getConfig().getTemplateStacks().stream().map(TemplateStack::getName).toList();
            Log.debug("Loaded TemplateStacks = " + stacks);
            commandManager.getCommandCompletions().registerCompletion("stacks", c -> stacks);
        }

        commandManager.getCommandCompletions().registerAsyncCompletion("offlineplayers", c -> {
            List<String> names = new ArrayList<>();
            for (OfflinePlayer p : getServer().getOfflinePlayers()) {
                if (p.getName() != null) {
                    names.add(p.getName());
                }
            }
            return names;
        });
    }
}
