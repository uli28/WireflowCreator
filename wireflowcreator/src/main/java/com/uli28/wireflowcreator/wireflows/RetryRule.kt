package com.uli28.wireflowcreator.wireflows

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

// https://github.com/cortinico/rules4android
class RetryRule : TestRule {
    override fun apply(statement: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val retryCount = description
                    .annotations
                    .filterIsInstance<RetryOnFailure>()
                    .firstOrNull()
                    ?.retryCount ?: 0

                var failureCause: Throwable? = null

                repeat(retryCount + 1) { _ ->
                    runCatching { statement.evaluate() }
                        .onSuccess { return }
                        .onFailure { failureCause = it }
                }

                println("Test ${description.methodName} - Giving up after ${retryCount + 1} attemps")
                failureCause?.printStackTrace()
            }
        }
    }
}
