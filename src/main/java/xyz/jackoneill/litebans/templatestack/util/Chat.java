package xyz.jackoneill.litebans.templatestack.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Chat {

    public static String color(String msg) {
        return ColorTranslator.translateColorCodes(msg);
    }

    public static void msg(Player player, String... messages) {
        Arrays.stream(messages).forEach((s) -> {
            player.sendMessage(color(s));
        });
    }

    public static void msg(CommandSender sender, String... messages) {
        Arrays.stream(messages).forEach((s) -> {
            sender.sendMessage(color(s));
        });
    }

}
