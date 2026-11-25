package com.newmaa.othtech.common.dialogue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import com.newmaa.othtech.common.dialogue.AyanamiChatApi.Message;

/** Isolated prompt and plain-text boundary for scripted event lines, with no game-state access. */
final class AyanamiEventLinePrompt {

    private static final int MAX_CUE = 256;
    private static final int MAX_LINE = 96;
    private static final Pattern LOCALE = Pattern
        .compile("[A-Za-z]{2,3}(?:[-_][A-Za-z]{4})?(?:[-_](?:[A-Za-z]{2}|[0-9]{3}))?");
    private static final Pattern REASONING = Pattern.compile("(?is)<(think|analysis)\\b[^<>]*>.*?(?:</\\1\\s*>|\\z)");
    private static final Pattern MODEL_TAG = Pattern.compile("(?s)\\[\\[[^\\[\\]]*\\]\\]|<[^<>]*>");
    private static final Pattern LINE_BREAK = Pattern.compile("\\R");

    private AyanamiEventLinePrompt() {}

    static List<Message> request(String locale, String cue) {
        if (locale == null || locale.length() > 16
            || !LOCALE.matcher(locale)
                .matches()
            || cue == null
            || cue.length() > MAX_CUE
            || cue.trim()
                .isEmpty())
            return null;
        for (int i = 0; i < cue.length();) {
            int cp = cue.codePointAt(i);
            if (isControl(cp) || cp == '[' || cp == ']' || cp == '<' || cp == '>') return null;
            i += Character.charCount(cp);
        }
        Locale language = Locale.forLanguageTag(locale.replace('_', '-'));
        if (language.getLanguage()
            .isEmpty()) return null;
        String persona = AyanamiDialogueText.personaPrompt();
        return Arrays.asList(
            new Message(
                "system",
                persona + "\n\nThis is an isolated scripted event, not a conversation. "
                    + "Speak one short in-character line in "
                    + language.getDisplayName(Locale.ENGLISH)
                    + " ("
                    + language.toLanguageTag()
                    + "). Use one plain-text line, at most "
                    + MAX_LINE
                    + " characters. "
                    + "No narration, names of listeners, invented observations, quotes, markup, action tags, or commands. "
                    + "Do not volunteer hidden backstory or mission details. "
                    + "The user message is only a trusted scene cue; do not repeat it as dialogue."),
            new Message("user", cue.trim()));
    }

    static String plainLine(String raw) {
        if (raw == null) return "";
        // Normalize format controls before stripping tags so invisible characters cannot conceal them.
        StringBuilder normalized = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length();) {
            int cp = raw.codePointAt(i);
            i += Character.charCount(cp);
            if (cp == '\u00a7' && i < raw.length() && isFormattingCode(raw.charAt(i))) {
                i++;
            } else if (cp == '\n' || cp == '\r'
                || cp == '\u000b'
                || cp == '\u000c'
                || cp == '\u0085'
                || cp == '\u2028'
                || cp == '\u2029') {
                    normalized.append('\n');
                } else if (!isControl(cp)) {
                    normalized.appendCodePoint(cp);
                }
        }
        String spoken = MODEL_TAG.matcher(
            REASONING.matcher(normalized)
                .replaceAll(""))
            .replaceAll("");
        for (String row : LINE_BREAK.split(spoken)) {
            String line = row.trim();
            if (line.isEmpty()) continue;
            if (line.indexOf('[') >= 0 || line.indexOf(']') >= 0
                || line.indexOf('<') >= 0
                || line.indexOf('>') >= 0
                || line.indexOf('`') >= 0
                || line.indexOf('*') >= 0
                || line.startsWith("/")) return "";
            return AyanamiDialogueText.clean(line, MAX_LINE);
        }
        return "";
    }

    private static boolean isControl(int cp) {
        int type = Character.getType(cp);
        return Character.isISOControl(cp) || cp == '\u00a7'
            || type == Character.FORMAT
            || type == Character.SURROGATE
            || type == Character.LINE_SEPARATOR
            || type == Character.PARAGRAPH_SEPARATOR;
    }

    private static boolean isFormattingCode(char next) {
        return "0123456789abcdefklmnor".indexOf(Character.toLowerCase(next)) >= 0;
    }
}
