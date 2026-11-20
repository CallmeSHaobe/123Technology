package com.newmaa.othtech.recipe;

import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsBA0;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.Nxer.TwistSpaceTechnology.common.GTCMItemList;
import com.brandon3055.draconicevolution.common.ModItems;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusUpgrade;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import goodgenerator.items.GGMaterial;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.block.ModBlocks;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import tectech.thing.CustomItemList;
import thaumcraft.common.config.ConfigBlocks;
import thaumicenergistics.api.ThEApi;

/**
 * NEI-only material previews for the Void Nexus upgrade tree.
 *
 * <p>
 * These recipes do not perform purchases or consume materials. The runtime upgrade purchase path remains
 * {@code OTEVoidNexusMatrix.tryPurchaseUpgrade(int)}.
 */
public final class VoidNexusUpgradePreviewRecipes {

    private VoidNexusUpgradePreviewRecipes() {}

    public static void register() {
        add(1, "无（免费升级）");
        add(
            2,
            "盖亚水晶×1；增生之种×3；盖亚之魂×256；无尽催化剂×8；UHV湿件主机×16；超致密宇宙中子态素板×4",
            block(vazkii.botania.common.block.ModBlocks.pylon, 2, 1),
            item(vazkii.botania.common.item.ModItems.overgrowthSeed, 0, 3),
            item(vazkii.botania.common.item.ModItems.manaResource, 5, 256),
            avaritiaResource(5, 8),
            ItemList.Circuit_Wetwaremainframe.get(16),
            plateSuperdense(Materials.CosmicNeutronium, 4));
        add(
            3,
            "水晶缠绕宇宙中子态素法杖×1；中子板×72；UHV电动活塞×16；源质供应器×4；法杖充能基座×1",
            infinityWand(),
            plate(Materials.Neutronium, 72),
            ItemList.Electric_Piston_UHV.get(16),
            exactAmount(
                4,
                ThEApi.instance()
                    .blocks().EssentiaProvider.getStack()),
            block(ConfigBlocks.blockStoneDevice, 5, 1));
        add(
            4,
            "增容符文×4；快反符文×4；促进符文×4；快反符文×4；血腥灵宝块×4；无尽锭×2；宗师魔法能源吸收器×1",
            block(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 1, 4),
            block(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 6, 4),
            block(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 5, 4),
            block(WayofTime.alchemicalWizardry.ModBlocks.bloodRune, 6, 4),
            modItem("dreamcraft", "BloodyIchorium", 4, 0),
            avaritiaResource(6, 2),
            ItemList.MagicEnergyAbsorber_EV.get(1));
        add(
            5,
            "无尽锭×16；永恒奇点×1；飞米功率IC×16；UHV力场发生器×16",
            avaritiaResource(6, 16),
            eternalSingularity(1),
            ItemList.Circuit_Chip_FPIC.get(16),
            ItemList.Field_Generator_UHV.get(16));
        add(
            6,
            "粗制超立方体×1；超时空金属粉×64；UEV能源仓×16；生物处理器主机×16",
            ItemList.Tesseract.get(1),
            Materials.TranscendentMetal.getDust(64),
            ItemList.Hatch_Energy_UEV.get(16),
            ItemList.Circuit_Biomainframe.get(16));
        add(
            7,
            "月球泥土×256；盖亚之魂块×64；灵魂之铁×16；魂金奇点×1；灵魂信标×1",
            block(GCBlocks.blockMoon, 3, 256),
            ore(OrePrefixes.block, Materials.GaiaSpirit, 64),
            modItem("RandomThings", "ingredient", 16, 4),
            modItem("universalsingularities", "universal.enderIO.singularity", 1, 6),
            modItem("ThaumicHorizons", "soulBeacon", 1, 0));
        add(
            8,
            "史诗存在创造者Ⅱ×1；UEV机械臂×2；研究站×16；觉醒核心×1",
            ItemList.MassFabricatorUEV.get(1),
            ItemList.Robot_Arm_UEV.get(2),
            CustomItemList.Machine_Multi_Research.get(16),
            item(ModItems.awakenedCore, 0, 1));
        add(
            9,
            "生物活性晶圆×32；海珀珍锭×64；生物细胞×128",
            ItemList.Circuit_Wafer_Bioware.get(32),
            GTModHandler.getModItem("miscutils", "itemIngotHypogen", 64),
            exactAmount(128, ItemList.Circuit_Chip_Biocell.get(1)));
        add(
            10,
            "超级煲×1；寰宇肉丸×64；治愈之斧×1",
            GTModHandler.getModItem("Avaritia", "Ultimate_Stew", 1),
            GTModHandler.getModItem("Avaritia", "Cosmic_Meatballs", 64),
            GTModHandler.getModItem("ExtraUtilities", "defoliageAxe", 1));
        add(
            11,
            "原初恒坚钢板×8；UIV光学主机×1；时空锭×16；量子反常×2；未知粒子×128",
            plate(Materials.ProtoHalkonite, 8),
            ItemList.Circuit_OpticalMainframe.get(1),
            ingot(Materials.SpaceTime, 16),
            modItem("miscutils", "MU-metaitem.01", 2, 32105),
            modItem("miscutils", "particleBase", 128, 24));
        add(12, "引力透镜×1；恒星燃料×1", GTCMItemList.GravitationalLens.get(1), avaritiaResource(8, 1));
        add(
            13,
            "充能超立方体×64；永恒线圈×64；UIV力场发生器×1",
            ItemList.EnergisedTesseract.get(64),
            block(GregTechAPI.sBlockCasings5, 13, 64),
            ItemList.Field_Generator_UIV.get(1));
        add(
            14,
            "超时空金属纳米蜂群×1；六相铜纳米蜂群×1；UIV超导导线×64",
            Materials.TranscendentMetal.getNanite(1),
            Materials.SixPhasedCopper.getNanite(1),
            ore(OrePrefixes.wireGt01, Materials.SuperconductorUIV, 64));
        add(15, "时空锭×512", ingot(Materials.SpaceTime, 512));
        add(
            16,
            "咒源玄晶锭×8；调律源金锭×8；UMV能源仓×4；UMV力场发生器×1",
            ingot(Materials.Hexanite, 8),
            exactAmount(8, GGMaterial.shirabon.get(OrePrefixes.ingot, 1)),
            ItemList.Hatch_Energy_UMV.get(4),
            ItemList.Field_Generator_UMV.get(1));
        addMAXPath(17, "超维校准矩阵×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123", ItemList.Transdimensional_Alignment_Matrix.get(1));
        addMAXPath(18, "无尽时空能量边界方块×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123", new ItemStack(sBlockCasingsBA0, 1, 12));
        addMAXPath(19, "圣灵奇点×128；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123", compoundSingularity(1, 128));
        addMAXPath(
            20,
            "无尽锭×128；寰宇支配之剑×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123",
            avaritiaResource(6, 128),
            GTModHandler.getModItem("Avaritia", "Infinity_Sword", 1));
        addMAXPath(
            21,
            "时空连续体撕裂方块×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123",
            new ItemStack(ModBlocks.blockCasings5Misc, 1, 10));
        addMAXPath(
            22,
            "亚稳态黑洞遏制场×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123",
            ItemList.Machine_Multi_BlackHoleCompressor.get(1));
        addMAXPath(
            23,
            "强化空间结构方块×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123",
            CustomItemList.EOH_Reinforced_Spatial_Casing.get(1));
        addMAXPath(
            24,
            "感知超频机械方块×1；白矮星物质锭×123；黑矮星物质锭×123；具象化灵能锭×123",
            new ItemStack(GregTechAPI.sBlockCasingsFoundry, 1, 5));
        add(25, "永恒锭×123；熔融磁流体约束恒星物质锭×123", ingot(Materials.Eternity, 123), ingot(Materials.MHDCSM, 123));
        add(
            26,
            "星门核心水晶×1；磁物质纳米蜂群×1；引力子碎片×1",
            GTModHandler.getModItem("SGCraft", "sgCoreCrystal", 1),
            Materials.MagMatter.getNanite(1),
            Materials.GravitonShard.getGems(1));
        add(
            27,
            "星阵×1；磁物质纳米蜂群×1；引力子碎片×1",
            CustomItemList.astralArrayFabricator.get(1),
            Materials.MagMatter.getNanite(1),
            Materials.GravitonShard.getGems(1));
        add(
            28,
            "宇宙坍缩者机械方块×1；UXV部件装配线外壳×1；超致密具象化灵能板×4；星门辐射防护板×1",
            new ItemStack(GregTechAPI.sBlockCasingsFoundry, 1, 4),
            block(Loaders.componentAssemblylineCasing, 12, 1),
            manifested(OrePrefixes.plateSuperdense, 4),
            GTModHandler.getModItem("dreamcraft", "StargateShieldingFoil", 1));
        OTHTechnology.LOG.info("[VoidNexus] Registered 28 upgrade material previews in NEI.");
    }

    private static void addMAXPath(int id, String materialText, ItemStack... special) {
        List<ItemStack> inputs = new ArrayList<>();
        addValid(inputs, id, materialText, special);
        addValid(inputs, id, materialText, ingot(Materials.WhiteDwarfMatter, 123));
        addValid(inputs, id, materialText, ingot(Materials.BlackDwarfMatter, 123));
        addValid(inputs, id, materialText, manifested(OrePrefixes.ingot, 123));
        add(id, materialText, inputs.toArray(new ItemStack[0]));
    }

    private static void addFluid(int id, String materialText, FluidStack fluid) {
        add(id, materialText, fluid, new ItemStack[0]);
    }

    private static void add(int id, String materialText, ItemStack... requestedInputs) {
        add(id, materialText, null, requestedInputs);
    }

    private static void add(int id, String materialText, FluidStack fluid, ItemStack... requestedInputs) {
        ItemStack controller = OTHItemList.VoidNexusMatrix.get(1);
        if (GTUtility.isStackInvalid(controller)) {
            OTHTechnology.LOG.warn("[VoidNexus] Upgrade preview pool skipped: controller item is unavailable.");
            return;
        }
        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(controller);
        addValid(inputs, id, materialText, requestedInputs);
        int expectedMaterialCount = declaredMaterialCount(materialText);
        int actualMaterialCount = inputs.size() - 1 + (fluid == null ? 0 : 1);
        if (actualMaterialCount < expectedMaterialCount) {
            OTHTechnology.LOG.warn(
                "[VoidNexus] Upgrade U{} preview material resolution is incomplete: {} of {} entries resolved. {}",
                id,
                actualMaterialCount,
                expectedMaterialCount,
                materialText);
        }
        if (inputs.size() > 16) {
            OTHTechnology.LOG.warn(
                "[VoidNexus] Upgrade U{} preview has {} item stacks; only the first 16 can be displayed.",
                id,
                inputs.size());
            inputs = new ArrayList<>(inputs.subList(0, 16));
        }
        try {
            var builder = GTValues.RA.stdBuilder()
                .itemInputsUnsafe(inputs.toArray(new ItemStack[0]))
                .duration(1)
                .eut(1)
                .specialValue(id)
                .fake()
                .setNEIDesc(
                    StatCollector.translateToLocal(
                        "tc.research_name." + VoidNexusUpgrade.byId(id)
                            .getResearchKey()));
            if (fluid != null) builder.fluidInputs(fluid);
            builder.addTo(Recipemaps.VOID_NEXUS_UPGRADES);
        } catch (Throwable throwable) {
            OTHTechnology.LOG.error("[VoidNexus] Failed to register upgrade U" + id + " NEI preview.", throwable);
        }
    }

    private static void addValid(List<ItemStack> target, int id, String materialText, ItemStack... stacks) {
        if (stacks == null) return;
        for (ItemStack stack : stacks) {
            if (!GTUtility.isStackInvalid(stack) && stack.stackSize > 0) {
                target.add(stack);
            } else {
                OTHTechnology.LOG.warn(
                    "[VoidNexus] Upgrade U{} preview could not resolve an item stack. Materials: {}",
                    id,
                    materialText);
            }
        }
    }

    private static int declaredMaterialCount(String materialText) {
        return materialText.startsWith("无（") ? 0 : materialText.split("；").length;
    }

    private static ItemStack block(Block block, int meta, int amount) {
        return block == null ? null : exactAmount(amount, new ItemStack(block, 1, meta));
    }

    private static ItemStack item(Item item, int meta, int amount) {
        return item == null ? null : exactAmount(amount, new ItemStack(item, 1, meta));
    }

    private static ItemStack modItem(String modId, String registryName, int amount, int meta) {
        return exactAmount(amount, GTModHandler.getModItem(modId, registryName, 1, meta));
    }

    private static ItemStack avaritiaResource(int meta, int amount) {
        return modItem("Avaritia", "Resource", amount, meta);
    }

    private static ItemStack eternalSingularity(int amount) {
        return modItem("eternalsingularity", "eternal_singularity", amount, 0);
    }

    private static ItemStack compoundSingularity(int meta, int amount) {
        return modItem("eternalsingularity", "combined_singularity", amount, meta);
    }

    private static ItemStack infinityWand() {
        ItemStack wand = GTModHandler.getModItem("Thaumcraft", "WandCasting", 1, 9000);
        if (GTUtility.isStackInvalid(wand)) return null;
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("cap", "matrix");
        tag.setString("rod", "infinity");
        wand.setTagCompound(tag);
        return wand;
    }

    private static ItemStack ingot(Materials material, int amount) {
        return ore(OrePrefixes.ingot, material, amount);
    }

    private static ItemStack plate(Materials material, int amount) {
        return ore(OrePrefixes.plate, material, amount);
    }

    private static ItemStack plateSuperdense(Materials material, int amount) {
        return ore(OrePrefixes.plateSuperdense, material, amount);
    }

    private static ItemStack ore(OrePrefixes prefix, Materials material, int amount) {
        return exactAmount(amount, GTOreDictUnificator.get(prefix, material, 1));
    }

    private static ItemStack manifested(OrePrefixes prefix, int amount) {
        return OTHMaterials.ManifestedPsionics == null ? null
            : exactAmount(amount, GTOreDictUnificator.get(prefix, OTHMaterials.ManifestedPsionics, 1));
    }

    private static ItemStack exactAmount(int amount, ItemStack stack) {
        return GTUtility.isStackInvalid(stack) ? null : GTUtility.copyAmountUnsafe(amount, stack);
    }
}
