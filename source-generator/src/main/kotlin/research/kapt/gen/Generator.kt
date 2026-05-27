package research.kapt.gen

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.writeText

object Generator {

    fun generate(count: Int, mode: Mode, seed: Long, outDir: Path) {
        // seed is reserved for future randomization of shape mix; output is currently deterministic by index alone.
        Files.createDirectories(outDir)
        val pkg = "research.kapt.generated"
        val pkgDir = outDir.resolve(pkg.replace('.', '/'))
        Files.createDirectories(pkgDir)

        // Imports differ per mode; we always write the import header, even if some imports are unused.
        val header = buildString {
            appendLine("package $pkg")
            appendLine()
            when (mode) {
                Mode.SYNTHETIC -> appendLine("import research.kapt.SyntheticAnno")
                Mode.DAGGER -> {
                    appendLine("import javax.inject.Inject")
                    appendLine("import dagger.Module")
                    appendLine("import dagger.Provides")
                    appendLine("import dagger.Component")
                }
                Mode.MAPSTRUCT -> appendLine("import org.mapstruct.Mapper")
            }
            appendLine()
        }

        // Generate body files
        for (i in 0 until count) {
            val name = "Gen$i"
            val shape = shapeFor(i)
            val body = if (mode == Mode.MAPSTRUCT && i % 5 == 0 && i > 0) {
                renderMapstructMapper(i)
            } else {
                renderShape(name, shape, mode)
            }
            pkgDir.resolve("$name.kt").writeText(header + body)
        }

        // Mode-specific extras
        when (mode) {
            Mode.DAGGER -> {
                pkgDir.resolve("DaggerWiring.kt").writeText(
                    header + renderDaggerModulesAndComponent(count)
                )
            }
            Mode.MAPSTRUCT, Mode.SYNTHETIC -> { /* no extras */ }
        }
    }

    private fun renderMapstructMapper(i: Int): String {
        // Mappers reference the nearest preceding DATA_CLASS indices (where i % 5 == 0).
        // For i >= 10, the two preceding data classes are at i-5 and i-10.
        val src = if (i >= 5) i - 5 else 0
        val dst = if (i >= 10) i - 10 else 0
        return """
        |@Mapper
        |interface Gen${i}_Mapper {
        |    fun map(src: Gen${src}_Data): Gen${dst}_Data
        |}
        |""".trimMargin()
    }

    private fun renderDaggerModulesAndComponent(count: Int): String {
        val moduleProvides = (0 until count step 50).joinToString("\n") { i ->
            """
            |    @Provides
            |    fun provideGen${i}_Data(): Gen${i}_Data = Gen${i}_Data(0, "x", 0L)
            """.trimMargin()
        }
        return """
        |@Module
        |class GenModule {
        |$moduleProvides
        |}
        |
        |@Component(modules = [GenModule::class])
        |interface GenComponent
        |""".trimMargin()
    }
}
