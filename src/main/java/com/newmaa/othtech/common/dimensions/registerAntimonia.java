package com.newmaa.othtech.common.dimensions;

import com.newmaa.othtech.common.dimensions.sky.skyProviderAntimonia;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;

public class registerAntimonia {

    public static Planet planetAntimonia;
    public static SolarSystem antimoniaSystem;
    public static Star starAntimonia;
    public static final int ID_DIM = 12321;

    public void init() {
        antimoniaSystem = (new SolarSystem("antimoniaSystem", "milkyWay")).setMapPosition(new Vector3(-1, 2.3, 0.0));
        GalaxyRegistry.registerSolarSystem(antimoniaSystem);
        (starAntimonia = (Star) (new Star("starAntimonia")).setParentSolarSystem(antimoniaSystem)
            .setTierRequired(-1)).setBodyIcon(skyProviderAntimonia.AntimoniaStar);
        antimoniaSystem.setMainStar(starAntimonia);
        (planetAntimonia = (new Planet("Antimonia")).setParentSolarSystem(antimoniaSystem))
            .setRingColorRGB(0.1F, 0.9F, 0.6F);
        planetAntimonia.setPhaseShift(18.86F);
        planetAntimonia.setTierRequired(250);
        planetAntimonia.setBodyIcon(skyProviderAntimonia.AntimoniaPlanet);
        planetAntimonia.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(0.5F, 0.5F));
        planetAntimonia.setRelativeOrbitTime(1.8619934F);
        planetAntimonia.setDimensionInfo(ID_DIM, worldProviderAntimonia.class);
        GalaxyRegistry.registerPlanet(planetAntimonia);
        GalacticraftRegistry.registerTeleportType(worldProviderAntimonia.class, new worldProviderAntimonia());

    }
}
