package dev.slne.unstuck.bukkit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class MessageManager {

    public static final TextColor PRIMARY = TextColor.fromHexString("#6fa8dc");
    public static final TextColor SECONDARY = TextColor.fromHexString("#5b5b5b");

    public static final TextColor INFO = TextColor.fromHexString("#5bd2f8");
    public static final TextColor SUCCESS = TextColor.fromHexString("#65ff64");
    public static final TextColor WARNING = TextColor.fromHexString("#f9c353");
    public static final TextColor ERROR = TextColor.fromHexString("#ee3d51");
    public static final TextColor IMPORTANT = NamedTextColor.RED;

    public static final TextColor VARIABLE_KEY = MessageManager.INFO;
    public static final TextColor VARIABLE_VALUE = MessageManager.WARNING;
    public static final TextColor SPACER = NamedTextColor.GRAY;

    public static final Component getPrefix() {
        return Component.text().append(Component.text(">>", NamedTextColor.DARK_GRAY)).append(Component.space())
                .append(Component.text("Unstuck", MessageManager.PRIMARY)).append(Component.space())
                .append(Component.text("|", NamedTextColor.DARK_GRAY)).append(Component.space()).build();
    }

}
