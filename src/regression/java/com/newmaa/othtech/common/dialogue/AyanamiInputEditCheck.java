package com.newmaa.othtech.common.dialogue;

import com.newmaa.othtech.common.dialogue.AyanamiInputEdit.Edit;

/** Exercises the editor's UTF-16 boundaries without opening Minecraft or loading ModularUI2. */
public final class AyanamiInputEditCheck {

    private static final String FACE = "\uD83D\uDE00";

    private AyanamiInputEditCheck() {}

    public static void main(String[] args) {
        run();
        System.out.println(
            "Ayanami input editor: stale cursors, repeat, selections, Unicode, IME/paste and length checks passed");
    }

    static void run() {
        Edit result = AyanamiInputEdit.delete("", 13, 13, true, false);
        expect(result, "", 0); // Reproduce the exact cleared-value/cursor pair from the crash report.
        expect(AyanamiInputEdit.delete("", 0, 90, false, false), "", 0);
        result = AyanamiInputEdit.insert("", 100, -1, "日" + FACE + "bye", 20);
        for (int i = 0; i < 12; i++) {
            result = AyanamiInputEdit.delete(result.text, result.cursor, result.cursor, true, false);
            if (result.text.endsWith("\uD83D")) throw new AssertionError("Backspace left an unpaired surrogate");
        }
        expect(result, "", 0);
        expect(AyanamiInputEdit.delete("甲" + FACE + "乙", 3, 3, true, false), "甲乙", 1);
        expect(AyanamiInputEdit.delete("甲" + FACE + "乙", 2, 2, false, false), "甲乙", 1);
        expect(AyanamiInputEdit.delete("hello world", 11, 11, true, true), "hello ", 6);
        expect(AyanamiInputEdit.delete("a" + FACE + "b", 3, 1, true, false), "ab", 1);
        expect(AyanamiInputEdit.insert("a" + FACE + "b", 3, 1, "新\r\n行" + FACE, 7), "a新 行" + FACE + "b", 6);
        expect(AyanamiInputEdit.insert("abcd", 4, 2, "中" + FACE + "x", 5), "ab中" + FACE, 5);
        if (AyanamiInputEdit.insert("abcd", 4, 2, "\u00a7\uD83D\u0000", 9) != null
            || AyanamiInputEdit.insert("ab", 2, 2, FACE, 3) != null) {
            throw new AssertionError("Rejected or unfit paste must preserve the previous text and selection");
        }
        if (AyanamiInputEdit.boundary(FACE, 1) != 0 || AyanamiInputEdit.move(FACE, 0, false, false) != 2) {
            throw new AssertionError("Caret must never land inside a Unicode surrogate pair");
        }
    }

    private static void expect(Edit result, String text, int cursor) {
        if (result == null || !text.equals(result.text) || result.cursor != cursor) {
            throw new AssertionError("Expected text " + text + " and caret " + cursor);
        }
    }
}
