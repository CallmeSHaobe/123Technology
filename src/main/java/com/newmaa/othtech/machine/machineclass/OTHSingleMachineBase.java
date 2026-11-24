package com.newmaa.othtech.machine.machineclass;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.machine.gui.OTHSingleMachineGui;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEBasicMachineWithRecipe;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;

/**
 * Base for OTH's recipe-driven, tiered single-block machines. Processing, sided I/O, power and GUI are managed by GT5U.
 */
public abstract class OTHSingleMachineBase extends MTEBasicMachineWithRecipe {

    private int machineEUtMultiplier = 1;

    /** Registers a machine prototype; register its crafting recipe separately. */
    protected OTHSingleMachineBase(int id, String name, String regionalName, int tier, String[] description,
        RecipeMap<?> recipes, int inputSlots, int outputSlots, boolean fluidInput, boolean fluidOutput,
        int tankCapacity, SoundResource sound, SpecialEffects effect, ITexture... overlays) {
        super(
            id,
            name,
            regionalName,
            tier,
            description,
            recipes,
            inputSlots,
            outputSlots,
            fluidInput,
            fluidOutput,
            tankCapacity,
            sound,
            effect,
            overlays,
            null);
    }

    /** Registers a machine with GT5U's tier-scaled tank capacity, or no tank for item-only recipes. */
    protected OTHSingleMachineBase(int id, String name, String regionalName, int tier, String[] description,
        RecipeMap<?> recipes, int inputSlots, int outputSlots, boolean fluidInput, boolean fluidOutput,
        SoundResource sound, SpecialEffects effect, ITexture... overlays) {
        this(
            id,
            name,
            regionalName,
            tier,
            description,
            recipes,
            inputSlots,
            outputSlots,
            fluidInput,
            fluidOutput,
            fluidInput || fluidOutput ? getCapacityForTier(tier) : 0,
            sound,
            effect,
            overlays);
    }

    /** Copies machine configuration, not inventory or progress, into a new placed instance. */
    protected OTHSingleMachineBase(OTHSingleMachineBase prototype) {
        super(
            prototype.mName,
            prototype.mTier,
            prototype.mDescriptionArray,
            prototype.mRecipes,
            prototype.mInputSlotCount,
            prototype.mOutputItems.length,
            prototype.hasInputFluidSlot,
            prototype.hasOutputFluidSlot,
            prototype.mTankCapacity,
            prototype.mAmperage,
            prototype.mTextures,
            prototype.mSoundResource,
            prototype.mSpecialEffect);
        setProgressBarTexture(prototype.progressBarTexture);
        setMachineEUtMultiplier(prototype.machineEUtMultiplier);
        setRecipeCatalystPriority(prototype.recipeCatalystPriority);
        setDeprecated(prototype.isDeprecated());
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager sync, UISettings settings) {
        return new OTHSingleMachineGui(this, getUIProperties()).build(data, sync, settings);
    }

    /** Implement as {@code return new YourMachine(this);} using the copy constructor above. */
    @Override
    public abstract IMetaTileEntity newMetaEntity(IGregTechTileEntity tile);

    @Override
    public OTHSingleMachineBase setMachineEUtMultiplier(int multiplier) {
        super.setMachineEUtMultiplier(multiplier);
        machineEUtMultiplier = multiplier;
        return this;
    }

    /**
     * Loads OTH overlays from {@code textures/blocks/basicmachines/<folder>/} in GT5U's order:
     * side, front, top, bottom, with active before inactive on each face.
     */
    protected static ITexture[] othOverlays(String folder) {
        String root = "basicmachines/" + folder + "/OVERLAY_";
        return new ITexture[] { overlay(root + "SIDE_ACTIVE"), overlay(root + "SIDE"), overlay(root + "FRONT_ACTIVE"),
            overlay(root + "FRONT"), overlay(root + "TOP_ACTIVE"), overlay(root + "TOP"),
            overlay(root + "BOTTOM_ACTIVE"), overlay(root + "BOTTOM") };
    }

    private static ITexture overlay(String path) {
        return TextureFactory.of(
            TextureFactory.of(Textures.BlockIcons.customOptional(OTHTechnology.MODID, path)),
            TextureFactory.builder()
                .addIcon(Textures.BlockIcons.customOptional(OTHTechnology.MODID, path + "_GLOW"))
                .glow()
                .build());
    }
}
