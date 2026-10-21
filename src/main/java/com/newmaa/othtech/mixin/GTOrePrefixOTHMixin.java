package com.newmaa.othtech.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;

@Mixin(value = OrePrefixes.class, remap = false)
public abstract class GTOrePrefixOTHMixin {

    @Inject(method = "doGenerateItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void oth$enableMaterialsOre(Materials material, CallbackInfoReturnable<Boolean> cir) {
        if (material == Materials.HydrochloricAcid) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }
        }
        if (material == Materials.Chlorine) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }
        }
        if (material == Materials.Air) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.Stone) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.GraniteBlack) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.GraniteRed) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.Basalt) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }

        if (material == Materials.Steel) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.Bronze) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.Brass) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
        if (material == Materials.Charcoal) {
            OrePrefixes prefix = (OrePrefixes) (Object) this;

            if (prefix == OrePrefixes.ore || prefix == OrePrefixes.rawOre
                || prefix == OrePrefixes.crushed
                || prefix == OrePrefixes.crushedPurified
                || prefix == OrePrefixes.crushedCentrifuged
                || prefix == OrePrefixes.dustImpure
                || prefix == OrePrefixes.dustPure
                || prefix == OrePrefixes.dustRefined
                || prefix == OrePrefixes.dust) {

                cir.setReturnValue(true);
            }

        }
    }
}
