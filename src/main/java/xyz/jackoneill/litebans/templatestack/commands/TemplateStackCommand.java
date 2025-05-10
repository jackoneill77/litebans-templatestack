package xyz.jackoneill.litebans.templatestack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import xyz.jackoneill.litebans.templatestack.TemplateStackPlugin;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;

@CommandAlias("%alias")
public class TemplateStackCommand extends BaseCommand {

    private final TemplateStackPlugin plugin;

    public TemplateStackCommand(TemplateStackPlugin plugin) {
        this.plugin = plugin;
    }

    @HelpCommand
    @CommandPermission("litebans.templatestack")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        sender.sendMessage("&e&lTemplateStack Help");
        help.showHelp();
    }

    @Subcommand("info")
    @CommandPermission("litebans.templatestack.info")
    @Description("Shows Details of available Template Stacks")
    @CommandCompletion("@stacks")
    public void onInfo(CommandSender sender, String templateStack) {
        TemplateStack stack = this.plugin.getLiteBansConfig().getStackByName(templateStack);
        if (stack != null) {
            sender.sendMessage(stack.getInfo().toArray(new String[0]));
        } else {
            sender.sendMessage("TemplateStack " + templateStack + " does not exist");
        }
    }

    @Subcommand("reload")
    @CommandPermission("litebans.templatestack.reload")
    @Description("Reloads configuration files from disk.")
    public void onReload(CommandSender sender) {
        this.plugin.reloadPlugin();

        if (this.plugin.getLiteBansConfig().isValid()) {
            sender.sendMessage("Configuration successfully reloaded");
        } else {
            sender.sendMessage("Configuration invalid. Check logs, fix and reload again.");
        }
    }
}
