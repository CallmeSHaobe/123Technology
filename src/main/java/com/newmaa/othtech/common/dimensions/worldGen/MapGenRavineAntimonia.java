package com.newmaa.othtech.common.dimensions.worldGen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.MapGenBase;

public class MapGenRavineAntimonia extends MapGenBase {

    private float[] field_75046_d = new float[1024];

    public MapGenRavineAntimonia() {}

    protected void func_151540_a(long seed, int x, int z, Block[] block, double p_151540_6_, double p_151540_8_,
        double p_151540_10_, float p_151540_12_, float p_151540_13_, float p_151540_14_, int p_151540_15_,
        int p_151540_16_, double p_151540_17_) {
        Random rand = new Random(seed);
        double d4 = (double) (x * 16 + 8);
        double d5 = (double) (z * 16 + 8);
        float f3 = 0.0F;
        float f4 = 0.0F;
        if (p_151540_16_ <= 0) {
            int j1 = this.range * 16 - 16;
            p_151540_16_ = j1 - rand.nextInt(j1 / 4);
        }

        boolean flag1 = false;
        if (p_151540_15_ == -1) {
            p_151540_15_ = p_151540_16_ / 2;
            flag1 = true;
        }

        float f5 = 1.0F;

        for (int k1 = 0; k1 < 256; ++k1) {
            if (k1 == 0 || rand.nextInt(3) == 0) {
                f5 = 1.0F + rand.nextFloat() * rand.nextFloat() * 1.0F;
            }

            this.field_75046_d[k1] = f5 * f5;
        }

        for (; p_151540_15_ < p_151540_16_; ++p_151540_15_) {
            double d6 = 1.5
                + (double) (MathHelper.sin((float) p_151540_15_ * 3.1415927F / (float) p_151540_16_) * p_151540_12_
                    * 1.0F);
            double d7 = d6 * p_151540_17_;
            d6 *= (double) rand.nextFloat() * 0.25 + 0.75;
            d7 *= (double) rand.nextFloat() * 0.25 + 0.75;
            float f6 = MathHelper.cos(p_151540_14_);
            float f7 = MathHelper.sin(p_151540_14_);
            p_151540_6_ += (double) (MathHelper.cos(p_151540_13_) * f6);
            p_151540_8_ += (double) f7;
            p_151540_10_ += (double) (MathHelper.sin(p_151540_13_) * f6);
            p_151540_14_ *= 0.7F;
            p_151540_14_ += f4 * 0.05F;
            p_151540_13_ += f3 * 0.05F;
            f4 *= 0.8F;
            f3 *= 0.5F;
            f4 += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 2.0F;
            f3 += (rand.nextFloat() - rand.nextFloat()) * rand.nextFloat() * 4.0F;
            if (flag1 || rand.nextInt(4) != 0) {
                double d8 = p_151540_6_ - d4;
                double d9 = p_151540_10_ - d5;
                double d10 = (double) (p_151540_16_ - p_151540_15_);
                double d11 = (double) (p_151540_12_ + 2.0F + 16.0F);
                if (d8 * d8 + d9 * d9 - d10 * d10 > d11 * d11) {
                    return;
                }

                if (p_151540_6_ >= d4 - 16.0 - d6 * 2.0 && p_151540_10_ >= d5 - 16.0 - d6 * 2.0
                    && p_151540_6_ <= d4 + 16.0 + d6 * 2.0
                    && p_151540_10_ <= d5 + 16.0 + d6 * 2.0) {
                    int i4 = MathHelper.floor_double(p_151540_6_ - d6) - x * 16 - 1;
                    int l1 = MathHelper.floor_double(p_151540_6_ + d6) - x * 16 + 1;
                    int j2 = MathHelper.floor_double(p_151540_8_ - d7) - 1;
                    int i5 = MathHelper.floor_double(p_151540_8_ + d7) + 1;
                    int k2 = MathHelper.floor_double(p_151540_10_ - d6) - z * 16 - 1;
                    int j3 = MathHelper.floor_double(p_151540_10_ + d6) - z * 16 + 1;
                    if (i4 < 0) {
                        i4 = 0;
                    }

                    if (l1 > 16) {
                        l1 = 16;
                    }

                    if (j2 < 1) {
                        j2 = 1;
                    }

                    if (i5 > 248) {
                        i5 = 248;
                    }

                    if (k2 < 0) {
                        k2 = 0;
                    }

                    if (j3 > 16) {
                        j3 = 16;
                    }

                    boolean flag2 = false;

                    int k3;
                    int j4;
                    for (k3 = i4; !flag2 && k3 < l1; ++k3) {
                        for (int l2 = k2; !flag2 && l2 < j3; ++l2) {
                            for (int i6 = i5 + 1; !flag2 && i6 >= j2 - 1; --i6) {
                                j4 = (k3 * 16 + l2) * 256 + i6;
                                if (i6 >= 0 && i6 < 256) {
                                    if (this.isOceanBlock(block, j4, k3, i6, l2, x, z)) {
                                        flag2 = true;
                                    }

                                    if (i6 != j2 - 1 && k3 != i4 && k3 != l1 - 1 && l2 != k2 && l2 != j3 - 1) {
                                        i6 = j2;
                                    }
                                }
                            }
                        }
                    }

                    if (!flag2) {
                        for (k3 = i4; k3 < l1; ++k3) {
                            double d12 = ((double) (k3 + x * 16) + 0.5 - p_151540_6_) / d6;

                            for (j4 = k2; j4 < j3; ++j4) {
                                double d13 = ((double) (j4 + z * 16) + 0.5 - p_151540_10_) / d6;
                                int k4 = (k3 * 16 + j4) * 256 + i5;
                                boolean flag3 = false;
                                if (d12 * d12 + d13 * d13 < 1.0) {
                                    for (int l3 = i5 - 1; l3 >= j2; --l3) {
                                        double d14 = ((double) l3 + 0.5 - p_151540_8_) / d7;
                                        if ((d12 * d12 + d13 * d13) * (double) this.field_75046_d[l3] + d14 * d14 / 6.0
                                            < 1.0) {
                                            if (this.isTopBlock(block, k4, k3, j4, x, z)) {
                                                flag3 = true;
                                            }

                                            this.digBlock(block, k4, k3, l3, j4, x, z, flag3);
                                        }

                                        --k4;
                                    }
                                }
                            }
                        }

                        if (flag1) {
                            break;
                        }
                    }
                }
            }
        }

    }

    protected void func_151538_a(World worldIn, int p_151538_2_, int p_151538_3_, int p_151538_4_, int p_151538_5_,
        Block[] p_151538_6_) {
        if (this.rand.nextInt(50) == 0) {
            double d0 = (double) (p_151538_2_ * 16 + this.rand.nextInt(16));
            double d2 = (double) (this.rand.nextInt(this.rand.nextInt(40) + 8) + 20);
            double d3 = (double) (p_151538_3_ * 16 + this.rand.nextInt(16));
            boolean b0 = true;

            for (int i1 = 0; i1 < 1; ++i1) {
                float f = this.rand.nextFloat() * 6.2831855F;
                float f2 = (this.rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f3 = (this.rand.nextFloat() * 2.0F + this.rand.nextFloat()) * 2.0F;
                this.func_151540_a(
                    this.rand.nextLong(),
                    p_151538_4_,
                    p_151538_5_,
                    p_151538_6_,
                    d0,
                    d2,
                    d3,
                    f3,
                    f,
                    f2,
                    0,
                    0,
                    3.0);
            }
        }

    }

    protected boolean isOceanBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ) {
        return data[index] == Blocks.water || data[index] == Blocks.flowing_water;
    }

    private boolean isTopBlock(Block[] data, int index, int x, int z, int chunkX, int chunkZ) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        return data[index] == biome.topBlock;
    }

    protected void digBlock(Block[] data, int index, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
        BiomeGenBase biome = this.worldObj.getBiomeGenForCoords(x + chunkX * 16, z + chunkZ * 16);
        Block top = biome.topBlock;
        Block filler = biome.fillerBlock;
        Block block = data[index];
        if (block == Blocks.stone || block == filler || block == top) {
            data[index] = null;
            if (foundTop && data[index - 1] == filler) {
                data[index - 1] = top;
            }
        }

    }
}
