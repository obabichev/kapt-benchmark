package research.kapt.gen

import java.nio.file.Path

fun main(argv: Array<String>) {
    val args = argv.toList().chunked(2).associate { it[0] to it[1] }
    val count = (args["--count"] ?: error("--count required")).toInt()
    val mode = Mode.valueOf((args["--mode"] ?: error("--mode required")).uppercase())
    val seed = (args["--seed"] ?: "42").toLong()
    val outDir = Path.of(args["--out-dir"] ?: error("--out-dir required"))
    Generator.generate(count, mode, seed, outDir)
    println("Generated $count files in $outDir (mode=$mode, seed=$seed)")
}
