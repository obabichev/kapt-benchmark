package research.kapt.gen

internal enum class Shape { DATA_CLASS, ANNOTATED_MEMBERS, GENERIC_CLASS, NESTED_TYPE, INHERITANCE }

internal fun shapeFor(index: Int): Shape = Shape.entries[index % Shape.entries.size]

internal fun renderShape(name: String, shape: Shape, mode: Mode): String {
    val classAnno = if (mode == Mode.SYNTHETIC) "@SyntheticAnno\n" else ""
    val ctorAnno = if (mode == Mode.DAGGER) "@Inject constructor" else "constructor"
    return when (shape) {
        Shape.DATA_CLASS ->
            "${classAnno}data class ${name}_Data $ctorAnno(val a: Int, val b: String, val c: Long)\n"
        Shape.ANNOTATED_MEMBERS -> {
            val memberAnno = if (mode == Mode.SYNTHETIC) "@field:SyntheticAnno " else ""
            """
            |${classAnno}class ${name}_Members $ctorAnno() {
            |    ${memberAnno}var x: Int = 0
            |    ${memberAnno}var y: String = ""
            |}
            |""".trimMargin()
        }
        Shape.GENERIC_CLASS ->
            "${classAnno}class ${name}_Generic<T : Comparable<T>, U : Number> $ctorAnno(val t: T, val u: U)\n"
        Shape.NESTED_TYPE ->
            """
            |${classAnno}class ${name}_Nested $ctorAnno(val v: Int) {
            |    class Inner(val w: String)
            |    inner class InnerRef(val parent: ${name}_Nested)
            |}
            |""".trimMargin()
        Shape.INHERITANCE ->
            """
            |${classAnno}open class ${name}_Base $ctorAnno(open val id: Int)
            |${classAnno}class ${name}_Derived $ctorAnno(override val id: Int, val extra: String) : ${name}_Base(id)
            |""".trimMargin()
    }
}
