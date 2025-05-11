package xyz.jackoneill.litebans.templatestack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import xyz.jackoneill.litebans.templatestack.TemplateStackPlugin;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;
import xyz.jackoneill.litebans.templatestack.util.Chat;

@CommandAlias("%alias")
public class TemplateStackCommand extends BaseCommand {

    private final TemplateStackPlugin plugin;

    public TemplateStackCommand(TemplateStackPlugin plugin) {
        this.plugin = plugin;
    }

    @HelpCommand
    @CommandPermission("litebans.templatestack")
    public static void onHelp(CommandSender sender, CommandHelp help) {
        Chat.msg(sender, "&e&lTemplateStack Help");
        help.showHelp();
    }

    @Subcommand("info")
    @CommandPermission("litebans.templatestack.info")
    @Description("Shows Details of available Template Stacks")
    @CommandCompletion("@stacks")
    public void onInfo(CommandSender sender, String templateStack) {
        Chat.msg(sender, "", "TemplateStack Info for &e" + templateStack);
        TemplateStack stack = this.plugin.getLiteBansConfig().getStackByName(templateStack);
        if (stack != null) {
            Chat.msg(sender, stack.getInfo().toArray(new String[0]));
        } else {
            Chat.msg(sender, "&cTemplateStack " + templateStack + " does not exist");
        }
        Chat.msg(sender, "");
    }

    @Subcommand("check")
    @CommandPermission("litebans.templatestack.check")
    @Description("Checks where the player in the current punishment ladder is")
    @CommandCompletion("@offlineplayers @stacks")
    public void onPlayerInfo(CommandSender sender, OfflinePlayer player, String templateStack) {
        Chat.msg(sender, "\nPlayerCheck Info for &e" + player.getName() + "&f on &e" + templateStack);
        TemplateStack stack = this.plugin.getLiteBansConfig().getStackByName(templateStack);

        if (stack != null) {
            Chat.msg(sender, "TODO");
        } else {
            Chat.msg(sender, "&cTemplateStack &e" + templateStack + " does not exist");
        }
        Chat.msg(sender, "");
    }

    @Subcommand("reload")
    @CommandPermission("litebans.templatestack.reload")
    @Description("Reloads configuration files from disk.")
    public void onReload(CommandSender sender) {
        this.plugin.reloadPlugin();

        if (this.plugin.getLiteBansConfig().isValid()) {
            Chat.msg(sender, "&aConfiguration successfully reloaded");
        } else {
            Chat.msg(sender, "&cConfiguration invalid. Check logs, fix and reload again.");
        }
    }
}
