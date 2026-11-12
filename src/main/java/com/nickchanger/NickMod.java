package com.nickchanger;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

/** Client-side nickname: /nick <name> */
public class NickMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("nickchanger");
    public static NickConfig config;

    @Override
    public void onInitializeClient() {
        config = NickConfig.load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("nick")
                .then(ClientCommandManager.literal("off").executes(ctx -> {
                    config.enabled = false;
                    config.save();
                    say(ctx.getSource(), Text.literal("Nick OFF - your real name is shown again.").formatted(Formatting.YELLOW));
                    return 1;
                }))
                .then(ClientCommandManager.literal("on").executes(ctx -> {
                    config.enabled = true;
                    config.save();
                    say(ctx.getSource(), Text.literal("Nick ON.").formatted(Formatting.GREEN));
                    return 1;
                }))
                .then(ClientCommandManager.literal("reset").executes(ctx -> {
                    config.nick = "";
                    config.save();
                    say(ctx.getSource(), Text.literal("Nick removed.").formatted(Formatting.YELLOW));
                    return 1;
                }))
                .then(sectionCmd("tab", "Tab list", c -> c.tabList, (c, v) -> c.tabList = v))
                .then(sectionCmd("scoreboard", "Side scoreboard", c -> c.scoreboard, (c, v) -> c.scoreboard = v))
                .then(sectionCmd("nametag", "Name tags", c -> c.nametag, (c, v) -> c.nametag = v))
                .then(sectionCmd("chat", "Chat", c -> c.chat, (c, v) -> c.chat = v))
                .then(ClientCommandManager.argument("name", StringArgumentType.greedyString()).executes(ctx -> {
                    String name = StringArgumentType.getString(ctx, "name").trim();
                    if (name.isEmpty() || name.length() > 48) {
                        say(ctx.getSource(), Text.literal("Name must be 1-48 characters.").formatted(Formatting.RED));
                        return 0;
                    }
                    config.nick = name;
                    config.enabled = true;
                    config.save();
                    MutableText msg = Text.literal("Nick set to: ").formatted(Formatting.GREEN)
                            .append(NameReplacer.preview(name));
                    say(ctx.getSource(), msg);
                    return 1;
                }))
                .executes(ctx -> {
                    FabricClientCommandSource src = ctx.getSource();
                    String real = NameReplacer.realName();
                    MutableText line = Text.literal("Nick: ").formatted(Formatting.GOLD);
                    if (config.nick == null || config.nick.isBlank()) {
                        line.append(Text.literal("(none)").formatted(Formatting.GRAY));
                    } else {
                        line.append(NameReplacer.preview(config.nick));
                    }
                    line.append(Text.literal(config.enabled ? "  [ON]" : "  [OFF]")
                            .formatted(config.enabled ? Formatting.GREEN : Formatting.RED));
                    say(src, line);
                    say(src, Text.literal("Real name: " + real + " | tab " + onOff(config.tabList)
                            + " | scoreboard " + onOff(config.scoreboard)
                            + " | nametag " + onOff(config.nametag)
                            + " | chat " + onOff(config.chat)).formatted(Formatting.GRAY));
                    say(src, Text.literal("/nick <name> (colours with &6, &l ...) | off | on | reset"
                            + " | tab | scoreboard | nametag | chat [true/false]").formatted(Formatting.GRAY));
                    return 1;
                })));
    }

    private static String onOff(boolean b) {
        return b ? "on" : "off";
    }

    private static void say(FabricClientCommandSource src, Text text) {
        src.sendFeedback(text);
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> sectionCmd(
            String literal, String label, Predicate<NickConfig> getter, BiConsumer<NickConfig, Boolean> setter) {
        return ClientCommandManager.literal(literal)
            .executes(ctx -> {
                boolean v = !getter.test(config);
                setter.accept(config, v);
                config.save();
                say(ctx.getSource(), Text.literal(label + ": " + (v ? "ON" : "OFF")));
                return 1;
            })
            .then(ClientCommandManager.argument("on", BoolArgumentType.bool()).executes(ctx -> {
                boolean v = BoolArgumentType.getBool(ctx, "on");
                setter.accept(config, v);
                config.save();
                say(ctx.getSource(), Text.literal(label + ": " + (v ? "ON" : "OFF")));
                return 1;
            }));
    }
}
