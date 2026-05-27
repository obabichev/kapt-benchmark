plugins {
    kotlin("jvm")
    kotlin("kapt")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // compileOnly: annotation classes available at compile time but not on runtime classpath.
    // kapt: registers the same artifact on the annotation-processor path.
    compileOnly(project(":synthetic-processor"))
    kapt(project(":synthetic-processor"))
}

val sampleSize: String = (findProperty("sampleSize") ?: "small") as String
val classCount: Int = when (sampleSize) {
    "small" -> 100
    "medium" -> 500
    "large" -> 2000
    else -> error("Unknown sampleSize: $sampleSize (use small, medium, or large)")
}

val generatorSeed: String = (findProperty("generatorSeed") ?: "42") as String

val generatedSrcDir = layout.buildDirectory.dir("generated/source/research-kapt/main")

val generateSources by tasks.registering(JavaExec::class) {
    classpath = project(":source-generator").the<SourceSetContainer>()["main"].runtimeClasspath
    mainClass.set("research.kapt.gen.MainKt")
    outputs.dir(generatedSrcDir)
    inputs.property("classCount", classCount)
    inputs.property("mode", "synthetic")
    inputs.property("seed", generatorSeed)
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(
            "--count", classCount.toString(),
            "--mode", "synthetic",
            "--seed", generatorSeed,
            "--out-dir", generatedSrcDir.get().asFile.absolutePath
        )
    })
}

sourceSets["main"].kotlin.srcDir(generatedSrcDir)

afterEvaluate {
    tasks.named("compileKotlin") { dependsOn(generateSources) }
    tasks.named("kaptGenerateStubsKotlin") { dependsOn(generateSources) }
}
