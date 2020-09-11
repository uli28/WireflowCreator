package com.uli28.wireflowcreator

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

//class MyRule(private val name: String) : TestRule {
//    override fun apply(base: Statement, description: Description?) = MyStatement(base)
//
//    inner class MyStatement(private val base: Statement) : Statement() {
//        @Throws(Throwable::class)
//        override fun evaluate() {
//            println("$name Before TestRule")
//            try {
//                base.evaluate()
//            } finally {
//                println("$name After TestRule")
//            }
//        }
//    }
//}
class MyRule (firstName: TestRule?) : TestRule {
    var test: String = "";
    override fun apply(statement: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                var enabled = description
                    .annotations
                    .filterIsInstance<LogTiming>()
                    .isNotEmpty()

                // Do something before the test.
                val startTime = System.currentTimeMillis()
                try {
                    // Execute the test.
                    statement.evaluate()
                } finally {
                    // Do something after the test.
                    val endTime = System.currentTimeMillis()
                    println("${description.methodName} took ${endTime - startTime} ms)")
                }
            }
        }
    }
}
