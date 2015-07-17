// This test was adapted from compiler/testData/codegen/boxWithStdlib/callableReference/function/.
package foo

class A {
    fun foo(result: String):String = result
}

fun box(): String {
    val x = A::foo
    var r = x(A(), "OK")

    return r
}
