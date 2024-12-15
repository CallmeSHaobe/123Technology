package com.newmaa.othtech.common.beeyonds;

import com.newmaa.othtech.OTHTechnology;
import forestry.api.apiculture.*;
import forestry.api.core.EnumTemperature;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFlowers;
import forestry.apiculture.genetics.Bee;
import forestry.apiculture.genetics.IBeeDefinition;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import gregtech.api.GregTechAPI;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTModHandler;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import static com.newmaa.othtech.common.beeyonds.OTHBeeDefinitionReference.*;
import static forestry.api.apiculture.EnumBeeChromosome.*;
import static forestry.api.apiculture.EnumBeeChromosome.FLOWER_PROVIDER;
import static forestry.api.core.EnumHumidity.DAMP;
import static gregtech.api.enums.Mods.*;
import static gregtech.loaders.misc.GTBeeDefinition.MACHINIST;
import static gregtech.loaders.misc.GTBeeDefinition.WALRUS;
import static gtPlusPlus.xmod.forestry.bees.registry.GTPP_BeeDefinition.DRAGONBLOOD;


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
        IBeeMutationCustom tMutation = dis
            .registerMutation(getSpecies(EXTRABEES, "chad"), WALRUS.getSpecies(), 2);
        tMutation.requireResource(GregTechAPI.sBlockMachines, 23520); // Mega 9in1
    }),

    GATE(OTHBranchDefinition.OTHBYDS, "Gate", true, new Color(0x3808A0), new Color(0x3808A0), beeSpecies -> {
        beeSpecies.addProduct(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateCrystalDust", 1), 0.810f);
        beeSpecies.addProduct(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateFramePart", 1), 0.810f);
        beeSpecies.addSpecialty(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateShieldingFoil", 1, 0), 0.123f);
        beeSpecies.addSpecialty(GTModHandler.getModItem(NewHorizonsCoreMod.ID, "item.StargateChevron", 1), 0.123f);
        beeSpecies.setHumidity(DAMP);
        beeSpecies.setTemperature(EnumTemperature.NORMAL);
    }, template -> {
        AlleleHelper.instance.set(template, FLOWERING, EnumAllele.Flowering.SLOWER);
        AlleleHelper.instance.set(template, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
        AlleleHelper.instance.set(template, EFFECT, getEffect(EXTRABEES, "teleport"));
        AlleleHelper.instance.set(template, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
    }, dis -> {
        IBeeMutationCustom tMutation = dis
            .registerMutation(WEIWEI, MACHINIST.getSpecies(), 1);
        tMutation.requireResource(GregTechAPI.sBlockMachines, 23524); // MAX1048576
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
