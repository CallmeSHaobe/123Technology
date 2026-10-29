package com.newmaa.othtech.common.dimensions.biome;

import static gregtech.api.GregTechAPI.*;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import bartworks.API.BorosilicateGlass;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace;

/**
 * 锑星的装饰性矿脉。
 * <p>
 * 置换目标必须是 {@link Blocks#stone}：{@code ChunkProviderAntimonia#getStoneBlock()} 返回的就是原版石头，
 * 整颗星的基岩之上全是它（{@code enableBiomeGenBaseBlock() == false}，群系里那个
 * {@code antimoniaBlockStone} 并不参与地形生成）。 之前这里写成 {@code antimoniaBlockStone}，
 * 而 {@code WorldGenMinable} 只替换 {@code isReplaceableOreGen(...)} 为真的方块 —— 目标方块在锑星地层里一格都不存在，
 * 于是四条矿脉全部生成失败（GT 大矿脉不受影响，那是另一套逻辑）。
 */
public class BiomeDecOreAntimonia extends BiomeDecoratorSpace {

    private World world;
    private final WorldGenerator blockAntimony;
    private final WorldGenerator blockNiobium;
    private final WorldGenerator oreSalfur;
    private final WorldGenerator glassBoron;

    public BiomeDecOreAntimonia() {
        // (方块, metadata, 每簇方块数, 被替换的方块)
        this.blockAntimony = new WorldGenMinable(sBlockMetal1, 4, 50, Blocks.stone);
        this.blockNiobium = new WorldGenMinable(sBlockMetal5, 5, 40, Blocks.stone);
        this.oreSalfur = new WorldGenMinable(sBlockReinforced, 5, 20, Blocks.stone);
        this.glassBoron = new WorldGenMinable(BorosilicateGlass.getGlassBlock(), 15, Blocks.stone);
    }

    protected void decorate() {
        this.generateOre(5, this.blockAntimony, 1, 100);
        this.generateOre(10, this.blockNiobium, 1, 60);
        this.generateOre(12, this.oreSalfur, 1, 20);
        this.generateOre(10, this.glassBoron, 1, 90);
    }

    protected void setCurrentWorld(World world) {
        this.world = world;
    }

    protected World getCurrentWorld() {
        return this.world;
    }
}
