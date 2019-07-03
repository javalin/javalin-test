package io.javalin.test

import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import okhttp3.RequestBody.Companion.toRequestBody

class ExamplesKotlin {

    @Test
    fun `get with no matching routes`() {
        val context = JavalinTest()
        context.run { _, http ->
            http.get("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(404))
            }
        }
    }

    @Test
    fun get() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result("javalin") }

            client.get("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("javalin"))
            }
        }
    }

    @Test
    fun `get with query string arguments`() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result(it.queryParam("foo") ?: "") }

            client.get("/", mapOf("foo" to "bar")).use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("bar"))
            }
        }
    }

    @Test
    fun `post with JSON body`() {
        JavalinTest.test { app, client ->
            app.post("/") { it.result(it.body()) }

            client.postJson("/", "{ \"name\": \"javalin\" }").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("{ \"name\": \"javalin\" }"))
            }
        }
    }

    @Test
    fun `put with JSON body`() {
        JavalinTest.test { app, client ->
            app.put("/") { it.result(it.body()) }

            client.putJson("/", "{ \"name\": \"javalin\" }").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("{ \"name\": \"javalin\" }"))
            }
        }
    }

    @Test
    fun `delete with path param`() {
        JavalinTest.test { app, client ->
            app.delete("/:id") { it.result(it.pathParam("id")) }

            client.delete("/123").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("123"))
            }
        }
    }

    @Test
    fun `custom request`() {
        JavalinTest.test { app, client ->
            app.post("/") {
                it.result("${it.header("FOO")}-${it.body()}")
            }

            val req = client.request("/")
                .post("bar".toRequestBody())
                .addHeader("FOO", "foo")

            client.execute(req).use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("foo-bar"))
            }
        }
    }

    @Test
    fun `custom http client`() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result("javalin") }

            val url = client.getUrl("/")

            val resp = url.openStream().reader().readText()
            Assert.assertThat(resp, CoreMatchers.equalTo("javalin"))
        }
    }
}