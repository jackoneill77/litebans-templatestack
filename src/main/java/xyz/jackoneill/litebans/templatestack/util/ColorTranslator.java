package xyz.jackoneill.litebans.templatestack.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTranslator {

    @Deprecated
    public static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final Pattern HEX_PATTERN = Pattern.compile("(&#[0-9a-fA-F]{6})");

    /**
     * @param text The string of text to apply color/effects to
     * @return Returns a string of text with color/effects applied
     */
    public static String translateColorCodes(@NotNull String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1).substring(1);
            matcher.appendReplacement(sb, "" + ChatColor.of(hex));
        }
        matcher.appendTail(sb);

        String hexColored = sb.toString();

        return ChatColor.translateAlternateColorCodes('&', hexColored);
    }

    /**
     * @param text The text with color codes that you want to turn into a TextComponent
     * @return the TextComponent with hex colors and regular colors
     */
    public static TextComponent translateColorCodesToTextComponent(@NotNull String text) {
        String colored = translateColorCodes(text);

        TextComponent base = new TextComponent();
        BaseComponent[] converted = TextComponent.fromLegacyText(colored);

        for (BaseComponent comp : converted) {
            base.addExtra(comp);
        }

        return base;
    }
}
