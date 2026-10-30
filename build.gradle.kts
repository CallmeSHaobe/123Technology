
plugins {
    id("com.github.ElytraServers.elytra-conventions") version "v1.1.1"
    id("com.gtnewhorizons.gtnhconvention")
}

// GTNH 约定只把 mixins.<modid>.json 写进 MixinConfigs；作物 mixin 单独一份 required:false 的配置，
// 必须显式追加，否则 MixinCropLoader 不会被加载。核心 GT mixin 仍留在主配置里保持 required:true。
tasks.named<Jar>("jar") {
    doFirst {
        manifest.attributes["MixinConfigs"] = "mixins.othtech.json,mixins.othtech.cropsnh.json"
    }
}
