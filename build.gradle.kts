
plugins {
    id("com.github.ElytraServers.elytra-conventions") version "v1.1.1"
    id("com.gtnewhorizons.gtnhconvention")
}

// CropsNH 的作物 mixin（MixinCropLoader）不能写进 MixinConfigs：CropsNH 是普通 mod，jar 要等到
// LoaderState.CONSTRUCTING 才进 LaunchClassLoader，而清单里的配置在启动期就 prepare，会因读不到
// CropLoader 而把 "类不存在" 缓存下来，导致 mixin 永久失效（NEI 里也就没有新品种作物）。
// 该配置改由 utils.LateMixins（@LateMixin/ILateMixinLoader）在 CONSTRUCTING 之后排队，
// 见 mixins.123technology.late.json。清单里只保留核心 GT mixin 的配置。
tasks.named<Jar>("jar") {
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.123technology.json"
    }
}
