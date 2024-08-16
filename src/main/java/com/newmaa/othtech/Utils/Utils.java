package com.newmaa.othtech.Utils;

import gregtech.api.enums.HeatingCoilLevel;

public final class Utils {

    public static int getCoilTier(HeatingCoilLevel coilLevel) {
        return coilLevel.getTier() + 1;
    }

    public static int multiBuildPiece(int... buildPieces) {
        int out = 0x80000000;
        for (int v : buildPieces) {
            out &= (v & 0x80000000) | 0x7fffffff;
            if (v != -1) out += v;
        }
        return out < 0 ? -1 : out;
    }

    ;
}
