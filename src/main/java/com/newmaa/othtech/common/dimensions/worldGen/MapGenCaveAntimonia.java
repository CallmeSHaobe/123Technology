package com.newmaa.othtech.common.dimensions.worldGen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.newmaa.othtech.common.blocks.antimonia.antimoniaBlocks;

import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta;

public class MapGenCaveAntimonia extends MapGenBaseMeta {

    public static final int BREAK_THROUGH_CHANCE = 50;

    public MapGenCaveAntimonia() {}

    protected void generateLargeCaveNode(long par1, int par3, int par4, Block[] blockIdArray, byte[] metaArray,
        double par6, double par8, double par10) {
        this.generateCaveNode(
            par1,
            par3,
            par4,
            blockIdArray,
            metaArray,
            par6,
            par8,
            par10,
            1.0F + this.rand.nextFloat() * 6.0F,
            0.0F,
            0.0F,
            -1,
            -1,
            0.5);
    }

    protected void generateCaveNode(long par1, int par3, int par4, Block[] blockIdArray, byte[] metaArray, double par6,
        double par8, double par10, float par12, float par13, float par14, int par15, int par16, double par17) {
        double d4 = (double) (par3 * 16 + 8);
        double d5 = (double) (par4 * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        Random random = new Random(par1);
        if (par16 <= 0) {
            int j1 = this.range * 16 - 16;
            par16 = j1 - random.nextInt(j1 / 4);
        }

        boolean flag = false;
        if (par15 == -1) {
            par15 = par16 / 2;
            flag = true;
        }

        int k1 = random.nextInt(par16 / 2) + par16 / 4;

        for (boolean flag2 = random.nextInt(6) == 0; par15 < par16; ++par15) {
            double d6 = 1.5 + (double) (MathHelper.sin((float) par15 * 3.1415927F / (float) par16) * par12 * 1.0F);
            double d7 = d6 * par17;
            float f5 = MathHelper.cos(par14);
            float f6 = MathHelper.sin(par14);
            par6 += (double) (MathHelper.cos(par13) * f5);
            par8 += (double) f6;
            par10 += (double) (MathHelper.sin(par13) * f5);
            if (flag2) {
                par14 *= 0.92F;
            } else {
                par14 *= 0.7F;
            }

            par14 += f4 * 0.1F;
            par13 += f3 * 0.1F;
            f4 *= 0.9F;
            f3 *= 0.75F;
            f4 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f3 += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (!flag && par15 == k1 && par12 > 1.0F && par16 > 0) {
                this.generateCaveNode(
                    random.nextLong(),
                    par3,
                    par4,
                    blockIdArray,
                    metaArray,
                    par6,
                    par8,
                    par10,
                    random.nextFloat() * 0.5F + 0.5F,
                    par13 - 1.5707964F,
                    par14 / 3.0F,
                    par15,
                    par16,
                    1.0);
                this.generateCaveNode(
                    random.nextLong(),
                    par3,
                    par4,
                    blockIdArray,
                    metaArray,
                    par6,
                    par8,
                    par10,
                    random.nextFloat() * 0.5F + 0.5F,
                    par13 + 1.5707964F,
                    par14 / 3.0F,
                    par15,
                    par16,
                    1.0);
                return;
            }

            if (flag || random.nextInt(4) != 0) {
                double d8 = par6 - d4;
                double d9 = par10 - d5;
                double d10 = (double) (par16 - par15);
                double d11 = (double) (par12 + 2.0F + 16.0F);
                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }

                if (par6 >= d4 - 16.0 - d6 * 2.0 && par10 >= d5 - 16.0 - d6 * 2.0
                    && par6 <= d4 + 16.0 + d6 * 2.0
                    && par10 <= d5 + 16.0 + d6 * 2.0) {
                    int l1 = MathHelper.floor_double(par6 - d6) - par3 * 16 - 1;
                    int i2 = MathHelper.floor_double(par6 + d6) - par3 * 16 + 1;
                    int j2 = MathHelper.floor_double(par8 - d7) - 1;
                    int k2 = MathHelper.floor_double(par8 + d7) + 1;
                    int l2 = MathHelper.floor_double(par10 - d6) - par4 * 16 - 1;
                    int i3 = MathHelper.floor_double(par10 + d6) - par4 * 16 + 1;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    if (i2 > 16) {
                        i2 = 16;
                    }

                    if (j2 < 1) {
                        j2 = 1;
                    }

                    if (k2 > 120) {
                        k2 = 120;
                    }

                    if (l2 < 0) {
                        l2 = 0;
                    }

                    if (i3 > 16) {
                        i3 = 16;
                    }

                    int localY;
                    for (localY = l1; localY < i2; ++localY) {
                        for (int l3 = l2; l3 < i3; ++l3) {
                            for (int i4 = k2 + 1; i4 >= j2 - 1; --i4) {
                                if (i4 >= 0 && i4 < 128
                                    && i4 != j2 - 1
                                    && localY != l1
                                    && localY != i2 - 1
                                    && l3 != l2
                                    && l3 != i3 - 1) {
                                    i4 = j2;
                                }
                            }
                        }
                    }

                    for (localY = j2; localY < k2; ++localY) {
                        double yfactor = ((double) localY + 0.5 - par8) / d7;
                        double yfactorSq = yfactor * yfactor;

                        for (int localX = l1; localX < i2; ++localX) {
                            double zfactor = ((double) (localX + par3 * 16) + 0.5 - par6) / d6;
                            double zfactorSq = zfactor * zfactor;

                            for (int localZ = l2; localZ < i3; ++localZ) {
                                double xfactor = ((double) (localZ + par4 * 16) + 0.5 - par10) / d6;
                                double xfactorSq = xfactor * xfactor;
                                if (xfactorSq + zfactorSq < 1.0) {
                                    int coords = (localX * 16 + localZ) * 256 + localY;
                                    if (yfactor > -0.7 && xfactorSq + yfactorSq + zfactorSq < 1.0) {
                                        if (blockIdArray[coords] == antimoniaBlocks.antimoniaBlockGrass
                                            && random.nextInt(50) == 0
                                            || blockIdArray[coords] == antimoniaBlocks.antimoniaBlockDirt
                                            || blockIdArray[coords] == antimoniaBlocks.antimoniaBlockStone) {
                                            blockIdArray[coords] = Blocks.air;
                                        }

                                        if (localY < 10) {
                                            blockIdArray[coords] = Blocks.lava;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (flag) {
                        break;
                    }
                }
            }
        }

    }

    protected void recursiveGenerate(World par1World, int par2, int par3, int par4, int par5, Block[] blockIdArray,
        byte[] metaArray) {
        int var7 = this.rand.nextInt(this.rand.nextInt(this.rand.nextInt(40) + 1) + 1);
        if (this.rand.nextInt(15) != 0) {
            var7 = 0;
        }

        for (int var8 = 0; var8 < var7; ++var8) {
            double var9 = (double) (par2 * 16 + this.rand.nextInt(16));
            double var10 = (double) this.rand.nextInt(this.rand.nextInt(120) + 8);
            double var11 = (double) (par3 * 16 + this.rand.nextInt(16));
            int var12 = 1;
            if (this.rand.nextInt(4) == 0) {
                this.generateLargeCaveNode(
                    this.rand.nextLong(),
                    par4,
                    par5,
                    blockIdArray,
                    metaArray,
                    var9,
                    var10,
                    var11);
                var12 += this.rand.nextInt(4);
            }

            for (int var13 = 0; var13 < var12; ++var13) {
                float var14 = this.rand.nextFloat() * 6.2831855F;
                float var15 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float var16 = this.rand.nextFloat() * 2.0F + this.rand.nextFloat();
                if (this.rand.nextInt(10) == 0) {
                    var16 *= this.rand.nextFloat() * this.rand.nextFloat() * 3.0F + 1.0F;
                }

                this.generateCaveNode(
                    this.rand.nextLong(),
                    par4,
                    par5,
                    blockIdArray,
                    metaArray,
                    var9,
                    var10,
                    var11,
                    var16,
                    var14,
                    var15,
                    0,
                    0,
                    1.0);
            }
        }

    }
}
