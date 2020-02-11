package io.javalin.test

import io.javalin.Javalin
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.function.Consumer

class ExamplesKotlin {

    @Test
    fun `get with no matching routes`() {
        JavalinTest.test { _, http ->
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
    fun post() {
        JavalinTest.test { app, client ->
            app.post("/") { }

            client.post("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
            }
        }
    }

    @Test
    fun `post with JSON body`() {
        JavalinTest.test { app, client ->
            app.post("/") { it.result(it.body()) }

            val requestJson = mapOf("name" to "javalin")
            client.post("/", json=requestJson).use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("{\"name\":\"javalin\"}"))
            }
        }
    }

    @Test
    fun put() {
        JavalinTest.test { app, client ->
            app.put("/") { }

            client.put("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
            }
        }
    }

    @Test
    fun `put with JSON body`() {
        JavalinTest.test { app, client ->
            app.put("/") { it.result(it.body()) }

            val requestJson = mapOf("name" to "javalin")
            client.put("/", json=requestJson).use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("{\"name\":\"javalin\"}"))
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

    @Test
    fun `provide custom Javalin instance`() {
        // create custom app with access manager that always throws 401
        val app = Javalin.create { config ->
            config.accessManager { _, ctx, _ -> ctx.status(401) }
        }
        app.get("/") { it.result("javalin") }

        JavalinTest.test(app) { client ->
            client.get("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(401))
            }
        }
    }

    @Test
    fun `send headers`() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result(it.header("FOO")!!)}

            client.get("/", headers=mapOf("FOO" to "bar")).use { resp ->
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("bar"))
            }
        }
    }

    @Test
    fun `don't follow redirects`() {
        Client.clientConfigurator = Consumer { it.followRedirects(false) }

        JavalinTest.test { app, client ->
            app.get("/") { it.redirect("http://foo.com") }

            client.get("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(302))
                Assert.assertThat(resp.headers["Location"], CoreMatchers.equalTo("http://foo.com"))
            }
        }
    }
}
