package com.nickchanger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Swaps your real username for the fake one inside any Text (keeping colours and click/hover styles). */
public final class NameReplacer {
    private record Seg(String text, Style style) {}

    private static String cachedReal;
    private static Pattern cachedPattern;
    private static String cachedNickRaw;
    private static List<Seg> cachedSegs = new ArrayList<>();

    private NameReplacer() {}

    public static Text replaceText(Text in) {
        if (in == null) return null;
        MutableText r = process(in);
        return r == null ? in : r;
    }

    public static MutableText replaceMutable(MutableText in) {
        if (in == null) return null;
        MutableText r = process(in);
        return r == null ? in : r;
    }

    /** The fake name as coloured text (used for the preview in chat). */
    public static MutableText preview(String raw) {
        MutableText out = Text.empty();
        for (Seg s : segments(raw)) out.append(Text.literal(s.text()).setStyle(s.style()));
        return out;
    }

    /** Returns null when there is nothing to replace. */
    private static MutableText process(Text in) {
        NickConfig cfg = NickMod.config;
        if (cfg == null || !cfg.active()) return null;
        String real = realName();
        if (real == null || real.isEmpty()) return null;
        String plain = in.getString();
        if (!plain.toLowerCase(Locale.ROOT).contains(real.toLowerCase(Locale.ROOT))) return null;

        Pattern p = pattern(real);
        List<Seg> segs = segments(cfg.nick);
        MutableText out = Text.empty();
        in.visit((style, str) -> {
            Matcher m = p.matcher(str);
            int last = 0;
            while (m.find()) {
                if (m.start() > last) out.append(Text.literal(str.substring(last, m.start())).setStyle(style));
                for (Seg s : segs) out.append(Text.literal(s.text()).setStyle(s.style().withParent(style)));
                last = m.end();
            }
            if (last < str.length()) out.append(Text.literal(str.substring(last)).setStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return out;
    }

    public static String realName() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.getSession() == null) return null;
        return mc.getSession().getUsername();
    }

    private static Pattern pattern(String real) {
        if (!real.equals(cachedReal) || cachedPattern == null) {
            cachedPattern = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(real) + "(?![A-Za-z0-9_])",
                    Pattern.CASE_INSENSITIVE);
            cachedReal = real;
        }
        return cachedPattern;
    }

    private static List<Seg> segments(String raw) {
        if (raw == null) raw = "";
        if (raw.equals(cachedNickRaw)) return cachedSegs;
        List<Seg> list = new ArrayList<>();
        Style style = Style.EMPTY;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if ((ch == '&' || ch == '\u00a7') && i + 1 < raw.length()) {
                Formatting f = Formatting.byCode(Character.toLowerCase(raw.charAt(i + 1)));
                if (f != null) {
                    if (sb.length() > 0) {
                        list.add(new Seg(sb.toString(), style));
                        sb.setLength(0);
                    }
                    style = f == Formatting.RESET ? Style.EMPTY : style.withFormatting(f);
                    i++;
                    continue;
                }
            }
            sb.append(ch);
        }
        if (sb.length() > 0) list.add(new Seg(sb.toString(), style));
        cachedNickRaw = raw;
        cachedSegs = list;
        return list;
    }
}
