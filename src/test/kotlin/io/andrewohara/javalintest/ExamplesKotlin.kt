package io.andrewohara.javalintest

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class ExamplesKotlin {


    @Test
    fun `get with no matching routes`() {
        val context = JavalinTest()
        context.run { _, http ->
            val resp = http.get("/")
            Assert.assertThat(resp.status, CoreMatchers.equalTo(404))
        }
    }

    @Test
    fun `get`() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result("javalin") }

            val resp = client.get("/")
            Assert.assertThat(resp.status, CoreMatchers.equalTo(200))
            Assert.assertThat(resp.body, CoreMatchers.equalTo("javalin"))
        }
    }
}
