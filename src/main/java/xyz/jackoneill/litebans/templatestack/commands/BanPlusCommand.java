package xyz.jackoneill.litebans.templatestack.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.jackoneill.litebans.templatestack.TemplateStackPlugin;
import xyz.jackoneill.litebans.templatestack.model.Template;
import xyz.jackoneill.litebans.templatestack.model.TemplateStack;
import xyz.jackoneill.litebans.templatestack.util.Chat;
import xyz.jackoneill.litebans.templatestack.util.Log;

import java.util.ArrayList;
import java.util.List;

@CommandPermission("litebans.templatestack.punish")
@CommandAlias("%banPlusAlias")
public class BanPlusCommand extends BaseCommand {

    private final TemplateStackPlugin plugin;

    public BanPlusCommand(TemplateStackPlugin plugin) {
        this.plugin = plugin;
    }

    @Default()
    @CommandPermission("litebans.templatestack.punish")
    @Description("Executes a punishment based on current position of templatestack ladder")
    @CommandCompletion("@offlineplayers @stacks")
    public void onExecute(CommandSender sender, @Name("player") OfflinePlayer player, @Name("template") String templateStack) {
        TemplateStack stack = this.plugin.getLiteBansManager().getConfig().getStackByName(templateStack);
        if (stack != null) {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                Template nextPunishment = stack.getNextPunishment(stack.getAllPunishments(player));
                Bukkit.getScheduler().runTask(this.plugin, () -> {
                    List<String> messages = new ArrayList<>();
                    if (nextPunishment != null) {
                        String commandToExecute = nextPunishment.getType().toString() + " " + player.getName() + " " + nextPunishment.getTemplate();
                        if (sender instanceof Player senderPlayer) {
                            senderPlayer.performCommand(commandToExecute);
                        } else {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandToExecute);
                        }
                        messages.add("&cSuccessfully " + nextPunishment.getType().getPastTense() + " &e" + player.getName() + "&f (" + nextPunishment.getTemplate() + ")");
                    } else {
                        Log.error("Could not find a suitable punishment template for " + player.getName() + " and Template " + stack.getName());
                        messages.add("&4Error: Could not find a suitable punishment template for &e" + player.getName() + " &4 and Template &e" + stack.getName());
                    }
                    Chat.msg(sender, messages.toArray(new String[0]));
                });
            });
        } else {
            Chat.msg(sender, "&cTemplateStack &e" + templateStack + " does not exist");
        }
        Chat.msg(sender, "");
    }
}
