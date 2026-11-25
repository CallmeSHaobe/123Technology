package com.newmaa.othtech.common.dialogue;

/** Single-line edit operations on bounded UTF-16 cursor indices, without a Minecraft or MUI dependency. */
final class AyanamiInputEdit {

    private AyanamiInputEdit() {}

    static int boundary(String text, int index) {
        int pos = Math.max(0, Math.min(index, text.length()));
        return pos > 0 && pos < text.length()
            && Character.isLowSurrogate(text.charAt(pos))
            && Character.isHighSurrogate(text.charAt(pos - 1)) ? pos - 1 : pos;
    }

    static int move(String text, int index, boolean backward, boolean word) {
        int pos = boundary(text, index);
        if (!word) return step(text, pos, backward);
        // Skip separators first, then the next word. Use code points for CJK and supplementary letters.
        while (backward ? pos > 0 : pos < text.length()) {
            int cp = backward ? text.codePointBefore(pos) : text.codePointAt(pos);
            if (isWord(cp)) break;
            pos = step(text, pos, backward);
        }
        while (backward ? pos > 0 : pos < text.length()) {
            int cp = backward ? text.codePointBefore(pos) : text.codePointAt(pos);
            if (!isWord(cp)) break;
            pos = step(text, pos, backward);
        }
        return pos;
    }

    static Edit delete(String text, int cursor, int anchor, boolean backward, boolean word) {
        int end = boundary(text, cursor);
        int start = boundary(text, anchor);
        if (end == start) start = move(text, end, backward, word);
        return replaceRange(text, start, end, "");
    }

    static Edit insert(String text, int cursor, int anchor, String raw, int limit) {
        int start = boundary(text, cursor);
        int end = boundary(text, anchor);
        String insertion = singleLine(raw, limit - text.length() + Math.abs(start - end));
        // Invalid clipboard/IME input must not delete the previous selection.
        return insertion.isEmpty() ? null : replaceRange(text, start, end, insertion);
    }

    private static int step(String text, int pos, boolean backward) {
        if (backward ? pos == 0 : pos == text.length()) return pos;
        return text.offsetByCodePoints(pos, backward ? -1 : 1);
    }

    private static boolean isWord(int cp) {
        int type = Character.getType(cp);
        return Character.isLetterOrDigit(cp) || cp == '_'
            || type == Character.NON_SPACING_MARK
            || type == Character.COMBINING_SPACING_MARK;
    }

    private static String singleLine(String raw, int maxLength) {
        if (raw == null || maxLength <= 0) return "";
        StringBuilder out = new StringBuilder(Math.min(raw.length(), maxLength));
        for (int i = 0; i < raw.length() && out.length() < maxLength;) {
            int cp = raw.codePointAt(i);
            int count = Character.charCount(cp);
            i += count;
            if (cp == '\u00a7' || Character.getType(cp) == Character.SURROGATE) continue;
            if (cp == '\r' || cp == '\n' || cp == '\t' || cp == '\u0085' || cp == '\u2028' || cp == '\u2029') {
                if (out.length() == 0 || out.charAt(out.length() - 1) != ' ') out.append(' ');
            } else if (!Character.isISOControl(cp)) {
                if (out.length() + count > maxLength) break;
                out.appendCodePoint(cp);
            }
        }
        return out.toString();
    }

    private static Edit replaceRange(String text, int first, int second, String insertion) {
        int from = Math.min(first, second);
        int to = Math.max(first, second);
        if (from == to && insertion.isEmpty()) return new Edit(text, from);
        return new Edit(text.substring(0, from) + insertion + text.substring(to), from + insertion.length());
    }

    static final class Edit {

        final String text;
        final int cursor;

        private Edit(String text, int cursor) {
            this.text = text;
            this.cursor = cursor;
        }
    }
}
