package com.newmaa.othtech.common.dimensions;

import static com.newmaa.othtech.Config.tier_Antimonia;
import static com.newmaa.othtech.Config.tier_Ross123b;

import com.newmaa.othtech.common.dimensions.antimonia.WorldProviderAntimonia;
import com.newmaa.othtech.common.dimensions.ross123b.WorldProviderRoss123b;
import com.newmaa.othtech.common.dimensions.sky.SkyProviderAntimonia;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class RegisterDimensions {

    public static Planet planetAntimonia;
    public static Planet planetRoss123b;
    public static SolarSystem antimoniaSystem;
    public static Star starAntimonia;
    public static final int ID_DIM = 12321;

    public void init() {
        antimoniaSystem = (new SolarSystem("antimoniaSystem", "milkyWay")).setMapPosition(new Vector3(-0.5, 1.5, 0.0));
        GalaxyRegistry.registerSolarSystem(antimoniaSystem);
        (starAntimonia = (Star) (new Star("starAntimonia")).setParentSolarSystem(antimoniaSystem)
            .setTierRequired(-1)).setBodyIcon(SkyProviderAntimonia.AntimoniaStar);
        antimoniaSystem.setMainStar(starAntimonia);
        (planetAntimonia = (new Planet("Antimonia")).setParentSolarSystem(antimoniaSystem))
            .setRingColorRGB(0.1F, 0.9F, 0.6F);
        planetAntimonia.setPhaseShift(18.86F);
        planetAntimonia.setTierRequired(tier_Antimonia);
        planetAntimonia.setBodyIcon(SkyProviderAntimonia.AntimoniaPlanet);
        planetAntimonia.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5F, 0.5F));
        planetAntimonia.setRelativeOrbitTime(1.8619934F);
        planetAntimonia.setDimensionInfo(ID_DIM, WorldProviderAntimonia.class);
        GalaxyRegistry.registerPlanet(planetAntimonia);
        GalacticraftRegistry.registerTeleportType(WorldProviderAntimonia.class, new WorldProviderAntimonia());

        (planetRoss123b = (new Planet("Ross123b")).setParentSolarSystem(antimoniaSystem))
            .setRingColorRGB(0.1F, 0.9F, 0.6F);
        planetRoss123b.setPhaseShift(18.86F);
        planetRoss123b.setTierRequired(tier_Ross123b);
        planetRoss123b.setBodyIcon(SkyProviderAntimonia.Ross123bPlanet);
        planetRoss123b.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(1.5F, 1.5F));
        planetRoss123b.setRelativeOrbitTime(2.8619934F);
        planetRoss123b.setDimensionInfo(ID_DIM + 1, WorldProviderRoss123b.class);
        GalaxyRegistry.registerPlanet(planetRoss123b);
        GalacticraftRegistry.registerTeleportType(WorldProviderRoss123b.class, new WorldProviderRoss123b());

    }
}
