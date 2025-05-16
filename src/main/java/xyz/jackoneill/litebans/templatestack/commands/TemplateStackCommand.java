package xyz.jackoneill.litebans.templatestack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        Chat.msg(sender, ChatColor.YELLOW + "" + ChatColor.BOLD + "TemplateStack Help");
        help.showHelp();
    }

    @Subcommand("list")
    @CommandPermission("litebans.templatestack")
    @Description("Lists all loaded TemplateStacks")
    public void onList(CommandSender sender) {

        if (this.plugin.getLiteBansManager().getConfig().getTemplateStacks().isEmpty()) {
            Chat.msg(sender, ChatColor.RED + "No valid TemplateStacks available");
            return;
        }

        String stackList = this.plugin.getLiteBansManager().getConfig().getTemplateStacks().stream().map(TemplateStack::getName).collect(Collectors.joining(", "));
        Chat.msg(sender,
                "",
                ChatColor.YELLOW + "" + ChatColor.BOLD + "Available TemplateStacks",
                "---------------------------",
                stackList,
                "");
    }

    @Subcommand("info")
    @CommandPermission("litebans.templatestack")
    @Description("Shows Details of a TemplateStack")
    @CommandCompletion("@stacks")
    public void onInfo(CommandSender sender, @Name("template") String templateStack) {

        TemplateStack stack = this.plugin.getLiteBansManager().getConfig().getStackByName(templateStack);
        if (stack != null) {
            Chat.msg(sender, "",
                    ChatColor.YELLOW + "" + ChatColor.BOLD + "TemplateStack Info for " + ChatColor.GOLD + templateStack,
                    "---------------------------");
            Chat.msg(sender, stack.getInfo().toArray(new String[0]));
        } else {
            Chat.msg(sender, ChatColor.RED + "TemplateStack " + templateStack + " does not exist");
        }
        Chat.msg(sender, "");
    }

    @Subcommand("check")
    @CommandPermission("litebans.templatestack.punish")
    @Description("Checks where the player in the current punishment ladder is")
    @CommandCompletion("@offlineplayers @stacks")
    public void onPlayerInfo(CommandSender sender, @Name("player") OfflinePlayer player, @Name("template") String templateStack) {

        TemplateStack stack = this.plugin.getLiteBansManager().getConfig().getStackByName(templateStack);
        if (stack != null) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {

                Map<Template, List<Punishment>> punishments = stack.getAllPunishments(player);
                List<String> messages = new ArrayList<>();
                messages.add("");
                messages.add(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Check for "
                        + ChatColor.GOLD + ChatColor.BOLD + player.getName()
                        + ChatColor.YELLOW + ChatColor.BOLD + " on Ladder "
                        + ChatColor.GOLD + ChatColor.BOLD + templateStack);
                messages.add("--------------------------------");

                for (Map.Entry<Template, List<Punishment>> entry : punishments.entrySet()) {
                    messages.add(ChatColor.YELLOW + entry.getKey().getType().toString() + ": " + entry.getKey().getTemplate() + ChatColor.ITALIC + " (" + entry.getValue().size() + " punishments)");
                    for (Punishment p : entry.getValue()) {
                        messages.add("» " + p.getTimeString() + ": " + p.getReason() + " &b(by " + p.getBannedBy() + ")");
                    }
                }
                messages.add("");

                Template nextPunishment = stack.getNextPunishment(punishments);
                if (nextPunishment != null) {
                    messages.add("Next Punishment: " + ChatColor.YELLOW + nextPunishment.getType().toString() + ": " + nextPunishment.getTemplate());
                } else {
                    messages.add(ChatColor.RED + "Next Punishment: Error - No Template found");
                }
                messages.add("");

                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    Chat.msg(sender, messages.toArray(new String[0]));
                });
            });
        } else {
            Chat.msg(sender, ChatColor.RED + "TemplateStack " + ChatColor.YELLOW + templateStack + ChatColor.RED + " does not exist");
        }
        Chat.msg(sender, "");
    }

    @Subcommand("reload")
    @CommandPermission("litebans.templatestack.reload")
    @Description("Reloads configuration files from disk.")
    public void onReload(CommandSender sender) {
        this.plugin.reloadPlugin();
        if (this.plugin.getLiteBansManager().getConfig().isValid()) {

            Chat.msg(sender, ChatColor.GREEN + "Configuration successfully reloaded");
        } else {
            Chat.msg(sender, ChatColor.RED + "Configuration invalid. Check logs, fix and reload again.");
        }
    }
}
