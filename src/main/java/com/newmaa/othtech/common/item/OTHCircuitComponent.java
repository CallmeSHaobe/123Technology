package com.newmaa.othtech.common.item;

import java.util.function.Supplier;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;

import gregtech.api.enums.ItemList;
import gregtech.common.tileentities.machines.multi.nanochip.util.CircuitComponent;

public enum OTHCircuitComponent {

    AF("OTHMiniActiveTransformer", 23547, "ote.tn.mat", () -> OTHItemList.AF.get(1)),
    UNIVERSAL_SOC("OTHUniversalSoC", 23548, "MetaItemOTH.22.name", () -> OTHItemList.socNorM.get(1)),
    PLASTIC_BOARD("OTHPlasticBoard", 23549, "oth.nac.board.plastic", () -> ItemList.Circuit_Board_Plastic.get(1)),
    ADVANCED_PLASTIC_BOARD("OTHAdvancedPlasticBoard", 23550, "oth.nac.board.advanced_plastic",
        () -> ItemList.Circuit_Board_Plastic_Advanced.get(1)),
    ADVANCED_BOARD("OTHAdvancedBoard", 23551, "oth.nac.board.advanced", () -> ItemList.Circuit_Board_Advanced.get(1));

    public final String nacName;
    public final int metaId;
    public final String nameKey;
    public final Supplier<ItemStack> realStack;

    OTHCircuitComponent(String nacName, int metaId, String nameKey, Supplier<ItemStack> realStack) {
        this.nacName = nacName;
        this.metaId = metaId;
        this.nameKey = nameKey;
        this.realStack = realStack;
    }

    public ItemStack getFakeStack(int amount) {
        return CircuitComponent.valueOf(nacName)
            .getFakeStack(amount);
    }
}
