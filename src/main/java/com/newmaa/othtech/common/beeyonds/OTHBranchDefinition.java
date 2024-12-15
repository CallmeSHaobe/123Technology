package com.newmaa.othtech.common.beeyonds;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IClassification;
import forestry.apiculture.genetics.alleles.AlleleEffect;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;

import java.util.Arrays;
import java.util.function.Consumer;

import static forestry.api.apiculture.EnumBeeChromosome.*;
import static forestry.api.apiculture.EnumBeeChromosome.SPEED;

public enum OTHBranchDefinition {
    OTHBYDS("Industrialis", alleles -> {
        AlleleHelper.instance.set(alleles, TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.UP_1);
        AlleleHelper.instance.set(alleles, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.BOTH_1);
        AlleleHelper.instance.set(alleles, NOCTURNAL, false);
        AlleleHelper.instance.set(alleles, FLOWER_PROVIDER, EnumAllele.Flowers.SNOW);
        AlleleHelper.instance.set(alleles, FLOWERING, EnumAllele.Flowering.FASTER);
        AlleleHelper.instance.set(alleles, LIFESPAN, EnumAllele.Lifespan.SHORT);
        AlleleHelper.instance.set(alleles, SPEED, EnumAllele.Speed.SLOW);
    });
    private static IAllele[] defaultTemplate;
    private final IClassification branch;
    private final Consumer<IAllele[]> mBranchProperties;

    OTHBranchDefinition(String scientific, Consumer<IAllele[]> aBranchProperties) {
        this.branch = BeeManager.beeFactory.createBranch(
            this.name()
                .toLowerCase(),
            scientific);
        this.mBranchProperties = aBranchProperties;
    }

    private static IAllele[] getDefaultTemplate() {
        if (defaultTemplate == null) {
            defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

            AlleleHelper.instance.set(defaultTemplate, SPEED, EnumAllele.Speed.SLOWEST);
            AlleleHelper.instance.set(defaultTemplate, LIFESPAN, EnumAllele.Lifespan.SHORTER);
            AlleleHelper.instance.set(defaultTemplate, FERTILITY, EnumAllele.Fertility.NORMAL);
            AlleleHelper.instance.set(defaultTemplate, TEMPERATURE_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(defaultTemplate, NOCTURNAL, false);
            AlleleHelper.instance.set(defaultTemplate, HUMIDITY_TOLERANCE, EnumAllele.Tolerance.NONE);
            AlleleHelper.instance.set(defaultTemplate, TOLERANT_FLYER, false);
            AlleleHelper.instance.set(defaultTemplate, CAVE_DWELLING, false);
            AlleleHelper.instance.set(defaultTemplate, FLOWER_PROVIDER, EnumAllele.Flowers.VANILLA);
            AlleleHelper.instance.set(defaultTemplate, FLOWERING, EnumAllele.Flowering.SLOWEST);
            AlleleHelper.instance.set(defaultTemplate, TERRITORY, EnumAllele.Territory.AVERAGE);
            AlleleHelper.instance.set(defaultTemplate, EFFECT, AlleleEffect.effectNone);
        }
        return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
    }

    void setBranchProperties(IAllele[] template) {
        this.mBranchProperties.accept(template);
    }

    public final IAllele[] getTemplate() {
        IAllele[] template = getDefaultTemplate();
        setBranchProperties(template);
        return template;
    }

    public final IClassification getBranch() {
        return branch;
    }
}
