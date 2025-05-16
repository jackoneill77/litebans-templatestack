package xyz.jackoneill.litebans.templatestack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import xyz.jackoneill.litebans.templatestack.TemplateStackPlugin;
import xyz.jackoneill.litebans.templatestack.model.Punishment;
import xyz.jackoneill.litebans.templatestack.model.Template;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;
import xyz.jackoneill.litebans.templatestack.util.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandAlias("%alias")
public class TemplateStackCommand extends BaseCommand {

    private final TemplateStackPlugin plugin;

    public TemplateStackCommand(TemplateStackPlugin plugin) {
        this.plugin = plugin;
    }

    @HelpCommand
    @CommandPermission("litebans.templatestack")
    @Description("Lists all available commands")
    public static void onHelp(CommandSender sender, @Name("subcommand") CommandHelp help) {
        Chat.msg(sender, "&e&lTemplateStack Help");
        help.showHelp();
    }

    @Subcommand("list")
    @CommandPermission("litebans.templatestack")
    @Description("Lists all loaded TemplateStacks")
    public void onList(CommandSender sender) {

        if (this.plugin.getLiteBansManager().getConfig().getTemplateStacks().isEmpty()) {
            Chat.msg(sender, "&4&lNo Template Stacks Available");
            return;
        }

        String stackList = this.plugin.getLiteBansManager().getConfig().getTemplateStacks().stream().map(TemplateStack::getName).collect(Collectors.joining(", "));
        Chat.msg(sender, "", "&e&lAvailable TemplateStacks", "---------------------------", stackList, "");
    }

    @Subcommand("info")
    @CommandPermission("litebans.templatestack")
    @Description("Shows Details of a TemplateStack")
    @CommandCompletion("@stacks")
    public void onInfo(CommandSender sender, @Name("template") String templateStack) {
        Chat.msg(sender, "", "&e&lTemplateStack Info for &6" + templateStack,
                "---------------------------");
        TemplateStack stack = this.plugin.getLiteBansManager().getConfig().getStackByName(templateStack);
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
    public void onPlayerInfo(CommandSender sender, @Name("player") OfflinePlayer player, @Name("template") String templateStack) {
        Chat.msg(sender, "\n&e&lPlayer Check for &6&l" + player.getName()
                + "&e&l on Ladder &6&l" + templateStack, "--------------------------------");
        TemplateStack stack = this.plugin.getLiteBansManager().getConfig().getStackByName(templateStack);

        if (stack != null) {

            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

                Map<Template, List<Punishment>> punishments = stack.getAllPunishments(player);

                List<String> messages = new ArrayList<>();

                for (Map.Entry<Template, List<Punishment>> entry : punishments.entrySet()) {
                    messages.add("&e" + entry.getKey().getType().toString() + ": " + entry.getKey().getTemplate() + " &o(" + entry.getValue().size() + " punishments)");
                    for (Punishment p : entry.getValue()) {
                        messages.add("» " + p.getTimeString() + ": " + p.getReason() + " &b(by " + p.getBannedBy() + ")");
                    }
                }
                messages.add("");

                Template nextPunishment = stack.getNextPunishment(punishments);
                if (nextPunishment != null) {
                    messages.add("Next Punishment: &e" + nextPunishment.getType().toString() + ": " + nextPunishment.getTemplate());
                } else {
                    messages.add("Next Punishment: &4Error - No Template found");
                }
                messages.add("");

                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    Chat.msg(sender, messages.toArray(new String[0]));
                });
            });
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
        if (this.plugin.getLiteBansManager().getConfig().isValid()) {
            Chat.msg(sender, "&aConfiguration successfully reloaded");
        } else {
            Chat.msg(sender, "&cConfiguration invalid. Check logs, fix and reload again.");
        }
    }
}
