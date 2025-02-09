package com.newmaa.othtech.common.beeyonds;

import static com.newmaa.othtech.common.OTHItemList.itemEnqingM;
import static com.newmaa.othtech.common.beeyonds.OTHBeeDefinitionReference.*;
import static forestry.api.apiculture.EnumBeeChromosome.*;
import static forestry.api.apiculture.EnumBeeChromosome.FLOWER_PROVIDER;
import static forestry.api.core.EnumHumidity.DAMP;
import static gregtech.api.enums.Mods.*;
import static gregtech.loaders.misc.GTBeeDefinition.*;

import java.awt.Color;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.item.ItemLoader;

import binnie.extrabees.genetics.effect.ExtraBeesEffect;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeEffect;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeMutationCustom;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.IBeeDefinition;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTModHandler;
import gregtech.loaders.misc.GTBeeDefinition;
import ic2.core.IC2;

@NotNull
class OTHBeeDefinitionReference {

    protected static final byte FORESTRY = 0;
    protected static final byte EXTRABEES = 1;
    protected static final byte GENDUSTRY = 2;
    protected static final byte MAGICBEES = 3;
    protected static final byte GREGTECH = 4;

    private OTHBeeDefinitionReference() {}
}

@NotNull
public enum OTHBeeDefinition implements IBeeDefinition {

    WEIWEI(OTHBranchDefinition.OTHBYDS, "Weiwei", true, new Color(0xE21818), new Color(0xE21818), beeSpecies -> {
        beeSpecies.addProduct(new ItemStack(Blocks.dirt, 1), 0.114514f);
        beeSpecies.addSpecialty(GTModHandler.getModItem(OTHTechnology.MODID, "itemZhangww", 1, 0), 0.05f);
        beeSpecies.setHumidity(DAMP);
        beeSpecies.setTemperature(EnumTemperature.NORMAL);
    }, template -> {
        AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
        AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
        AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
        AlleleHelper.instance.set(template, FLOWER_PROVIDER, getFlowers(EXTRABEES, "book"));
    }, dis -> {
        IBeeMutationCustom tMutation = dis.registerMutation(getSpecies(EXTRABEES, "chad"), WALRUS.getSpecies(), 2);
        tMutation.requireResource(GregTechAPI.sBlockMachines, 23520); // Mega 9in1
    }),

    GATE(OTHBranchDefinition.OTHBYDS, "Gate", true, new Color(0x3808A0), new Color(0x3808A0), beeSpecies -> {
        beeSpecies.addProduct(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateCrystalDust", 1), 0.00810f);
        // beeSpecies.addProduct(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateFramePart", 1), 0.00810f);
        // beeSpecies
        // .addSpecialty(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateShieldingFoil", 1, 0), 0.00123f);
        // beeSpecies.addSpecialty(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateChevron", 1), 0.00123f);
        beeSpecies.setHumidity(DAMP);
        beeSpecies.setTemperature(EnumTemperature.NORMAL);
    }, template -> {
        AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
        AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
        AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
        AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
    }, dis -> {
        IBeeMutationCustom tMutation = dis.registerMutation(WEIWEI, MACHINIST.getSpecies(), 1);
        tMutation.requireResource(GregTechAPI.sBlockMachines, 23524); // MAX1048576
    }),
    HYP(OTHBranchDefinition.OTHBYDS, "Hypogen", true, new Color(0xD31010), new Color(0xB01E0B), beeSpecies -> {
        beeSpecies.addSpecialty(OTHBeeyonds.combs.getStackForType(combTypes.HYPOGEN), 0.02F);
        beeSpecies.setHumidity(DAMP);
        beeSpecies.setTemperature(EnumTemperature.NORMAL);
    }, template -> {
        AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
        AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
        AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
        AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
    }, dis -> {
        IBeeMutationCustom tMutation = dis.registerMutation(INFINITY.getSpecies(), DRAGONESSENCE.getSpecies(), 1);
        tMutation.requireResource(Block.getBlockFromName(GoodGenerator.ID + ":componentAssemblylineCasing"), 10); // UIV
                                                                                                                  // COM
                                                                                                                  // CASING
    }),
    GLAS(OTHBranchDefinition.OTHBYDS, "Glass", true, new Color(0xFFFFFFFF), new Color(0xFFFFFFFF), beeSpecies -> {
        beeSpecies.addProduct(new ItemStack(Blocks.glass, 4), 0.9F);
        beeSpecies.addProduct(new ItemStack(Blocks.glass_pane, 12), 0.9F);
        beeSpecies.addSpecialty(OTHBeeyonds.combs.getStackForType(combTypes.NORMALGLASS), 0.3F);
        beeSpecies.setHumidity(DAMP);
        beeSpecies.setTemperature(EnumTemperature.NORMAL);
    }, template -> {
        AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
        AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
        AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
        AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
    }, dis -> {
        IBeeMutationCustom tMutation = dis.registerMutation(CLAY.getSpecies(), getSpecies(FORESTRY, "Rural"), 90);
        tMutation.requireResource(Block.getBlockFromName(TinkerConstruct.ID + ":GlassBlock"), 0); // Tic Glass
    }),
    CHG(OTHBranchDefinition.OTHBYDS, "ChromaticGlass", true, new Color(0xFFEE1414, true), new Color(0xFF1422BE, true),
        beeSpecies -> {
            beeSpecies.addSpecialty(OTHBeeyonds.combs.getStackForType(combTypes.CHROMATICGLASS), 1F);
            beeSpecies.setHumidity(DAMP);
            beeSpecies.setTemperature(EnumTemperature.NORMAL);
        }, template -> {
            AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
            AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
            AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
        }, dis -> {
            IBeeMutationCustom tMutation = dis.registerMutation(GLAS, INFINITY.getSpecies(), 1);
            tMutation.requireResource(Block.getBlockFromName(GTPlusPlus.ID + ":blockBlockChromaticGlass"), 0); // block
                                                                                                               // of
                                                                                                               // Chromatic
                                                                                                               // Glass
        }),
    JIUCAI(OTHBranchDefinition.OTHBYDS, "Jiucai", true, new Color(0xFFD5742A, true), new Color(0x38FF1038, true),
        beeSpecies -> {
            beeSpecies.addSpecialty(new ItemStack(ItemLoader.itemLeekBox, 16), 0.3F);
            beeSpecies.setHumidity(DAMP);
            beeSpecies.setTemperature(EnumTemperature.NORMAL);
        }, template -> {
            AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.FASTEST);
            AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(template, EFFECT, AlleleEffect.effectCreeper);
            AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.WHEAT);
        }, dis -> {
            IBeeMutationCustom tMutation = dis.registerMutation(COAL.getSpecies(), SALTY.getSpecies(), 12);
            tMutation.requireResource(GregTechAPI.sBlockMachines, 1174); // Lightning Rod
        }),
    ENQING(OTHBranchDefinition.OTHBYDS, "Enqing", true, new Color(0xFFFF0000, true), new Color(0xECFFCD07, true),
        beeSpecies -> {
            beeSpecies.addProduct(new ItemStack(Items.bread, 1), 1F);
            beeSpecies.addSpecialty(itemEnqingM.get(1), 0.1F);
            beeSpecies.addSpecialty(GTModHandler.getModItem(IC2.MODID, "blockNuke", 1, 0), 0.001F);
            beeSpecies.setHumidity(DAMP);
            beeSpecies.setTemperature(EnumTemperature.NORMAL);
        }, template -> {
            AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.MAXIMUM);
            AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(template, EFFECT, AlleleEffect.effectRadioactive);
            AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.NETHER);
        }, dis -> {
            IBeeMutationCustom tMutation = dis.registerMutation(JIUCAI.getSpecies(), WEIWEI.getSpecies(), 1);
            tMutation.requireResource(GregTechAPI.sBlockMachines, 23520 + 17); // Enqing Factory
        }),
    IRONOTH(OTHBranchDefinition.OTHBYDS, "Iron", true, new Color(0xD0FFFFFF, true), new Color(0x96FFFFFF, true),
        beeSpecies -> {
            beeSpecies.addProduct(new ItemStack(Blocks.iron_block, 16), 1F);
            beeSpecies.setHumidity(DAMP);
            beeSpecies.setTemperature(EnumTemperature.NORMAL);
        }, template -> {
            AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.MAXIMUM);
            AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(template, EFFECT, AlleleEffect.effectNone);
            AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
        }, dis -> {
            IBeeMutationCustom tMutation = dis
                .registerMutation(GTBeeDefinition.IRON.getSpecies(), CLAY.getSpecies(), 100);
            tMutation.requireResource(Blocks.iron_ore, 0); // Enqing Factory
        }),
    SHENGXI(OTHBranchDefinition.OTHBYDS, "shengxi", true, new Color(0xFFEE14BB, true), new Color(0xFF7C18D2, true),
        beeSpecies -> {
            beeSpecies.addSpecialty(GTModHandler.getModItem(Forestry.ID, "royalJelly", 1, 0), 1F);
            beeSpecies.setHumidity(DAMP);
            beeSpecies.setTemperature(EnumTemperature.NORMAL);
        }, template -> {
            AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
            AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
            AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
            binnie.genetics.genetics.AlleleHelper.instance.set(
                template,
                EnumBeeChromosome.EFFECT,
                binnie.genetics.genetics.AlleleHelper.getAllele(ExtraBeesEffect.BIRTHDAY.getUID()));
        }, dis -> {
            IBeeMutationCustom tMutation = dis.registerMutation(WEIWEI, GATE, 100);
            tMutation.requireResource(Blocks.dirt, 0);// block of dirt
        });

    private final OTHBranchDefinition branch;
    private final OTHAlleleBeeSpecies species;
    private final Consumer<OTHAlleleBeeSpecies> mSpeciesProperties;
    private final Consumer<IAllele[]> mAlleles;
    private final Consumer<OTHBeeDefinition> mMutations;
    private IAllele[] template;
    private IBeeGenome genome;

    OTHBeeDefinition(OTHBranchDefinition branch, String binomial, boolean dominant, Color primary, Color secondary,
        Consumer<OTHAlleleBeeSpecies> aSpeciesProperties, Consumer<IAllele[]> aAlleles,
        Consumer<OTHBeeDefinition> aMutations) {
        this.mAlleles = aAlleles;
        this.mMutations = aMutations;
        this.mSpeciesProperties = aSpeciesProperties;
        String lowercaseName = this.toString()
            .toLowerCase(Locale.ENGLISH);
        String species = WordUtils.capitalize(lowercaseName);

        String uid = "gregtech.bee.species" + species;
        String description = "for.description." + lowercaseName;
        String name = "for.bees.species." + lowercaseName;
        GTLanguageManager.addStringLocalization("for.bees.species." + lowercaseName, species);

        String authority = GTLanguageManager.getTranslation("for.bees.authority." + lowercaseName);
        if (authority.equals("for.bees.authority." + lowercaseName)) {
            authority = "GTNH";
        }
        this.branch = branch;
        this.species = new OTHAlleleBeeSpecies(
            uid,
            dominant,
            name,
            authority,
            description,
            branch.getBranch(),
            binomial,
            primary,
            secondary);
    }

    public static void initBees() {
        for (OTHBeeDefinition bee : values()) {
            bee.init();
        }
        for (OTHBeeDefinition bee : values()) {
            bee.registerMutations();
        }
    }

    static IAlleleBeeEffect getEffect(byte modid, String name) {
        String s = switch (modid) {
            case EXTRABEES -> "extrabees.effect." + name;
            case GENDUSTRY -> "gendustry.effect." + name;
            case MAGICBEES -> "magicbees.effect" + name;
            case GREGTECH -> "gregtech.effect" + name;
            default -> "forestry.effect" + name;
        };
        return (IAlleleBeeEffect) AlleleManager.alleleRegistry.getAllele(s);
    }

    static IAlleleFlowers getFlowers(byte modid, String name) {
        String s = switch (modid) {
            case EXTRABEES -> "extrabees.flower." + name;
            case GENDUSTRY -> "gendustry.flower." + name;
            case MAGICBEES -> "magicbees.flower" + name;
            case GREGTECH -> "gregtech.flower" + name;
            default -> "forestry.flowers" + name;
        };
        return (IAlleleFlowers) AlleleManager.alleleRegistry.getAllele(s);
    }

    private static IAlleleBeeSpecies getSpecies(byte modid, String name) {
        String s = switch (modid) {
            case EXTRABEES -> "extrabees.species." + name;
            case GENDUSTRY -> "gendustry.bee." + name;
            case MAGICBEES -> "magicbees.species" + name;
            case GREGTECH -> "gregtech.species" + name;
            default -> "forestry.species" + name;
        };
        return (IAlleleBeeSpecies) AlleleManager.alleleRegistry.getAllele(s);
    }

    private void setSpeciesProperties(OTHAlleleBeeSpecies beeSpecies) {
        this.mSpeciesProperties.accept(beeSpecies);
    }

    private void setAlleles(IAllele[] template) {
        this.mAlleles.accept(template);
    }

    private void registerMutations() {
        this.mMutations.accept(this);
    }

    private void init() {
        setSpeciesProperties(species);

        template = branch.getTemplate();
        AlleleHelper.instance.set(template, SPECIES, species);
        setAlleles(template);

        genome = BeeManager.beeRoot.templateAsGenome(template);

        BeeManager.beeRoot.registerTemplate(template);
    }

    private IBeeMutationCustom registerMutation(IAlleleBeeSpecies parent1, IAlleleBeeSpecies parent2, int chance) {
        return registerMutation(parent1, parent2, chance, 1f);
    }

    private IBeeMutationCustom registerMutation(OTHBeeDefinition parent1, IAlleleBeeSpecies parent2, int chance) {
        return registerMutation(parent1, parent2, chance, 1f);
    }

    private IBeeMutationCustom registerMutation(IAlleleBeeSpecies parent1, OTHBeeDefinition parent2, int chance) {
        return registerMutation(parent1, parent2, chance, 1f);
    }

    private IBeeMutationCustom registerMutation(OTHBeeDefinition parent1, OTHBeeDefinition parent2, int chance) {
        return registerMutation(parent1, parent2, chance, 1f);
    }

    /**
     * Diese neue Funtion erlaubt Mutationsraten unter 1%. Setze dazu die Mutationsrate als Bruch mit chance /
     * chanceDivider This new function allows Mutation percentages under 1%. Set them as a fraction with chance /
     * chanceDivider
     */
    private IBeeMutationCustom registerMutation(IAlleleBeeSpecies parent1, IAlleleBeeSpecies parent2, int chance,
        float chanceDivider) {
        return new OTHBeeMutation(parent1, parent2, this.getTemplate(), chance, chanceDivider);
    }

    private IBeeMutationCustom registerMutation(OTHBeeDefinition parent1, IAlleleBeeSpecies parent2, int chance,
        float chanceDivider) {
        return registerMutation(parent1.species, parent2, chance, chanceDivider);
    }

    private IBeeMutationCustom registerMutation(IAlleleBeeSpecies parent1, OTHBeeDefinition parent2, int chance,
        float chanceDivider) {
        return registerMutation(parent1, parent2.species, chance, chanceDivider);
    }

    private IBeeMutationCustom registerMutation(OTHBeeDefinition parent1, OTHBeeDefinition parent2, int chance,
        float chanceDivider) {
        return registerMutation(parent1.species, parent2, chance, chanceDivider);
    }

    @Override
    public final IAllele[] getTemplate() {
        return Arrays.copyOf(template, template.length);
    }

    @Override
    public final IBeeGenome getGenome() {
        return genome;
    }

    @Override
    public final IBee getIndividual() {
        return new Bee(genome);
    }

    @Override
    public final ItemStack getMemberStack(EnumBeeType beeType) {
        return BeeManager.beeRoot.getMemberStack(getIndividual(), beeType.ordinal());
    }

    public OTHAlleleBeeSpecies getSpecies() {
        return species;
    }
}
