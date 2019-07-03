package io.andrewohara.javalintest

import com.mashape.unirest.http.HttpMethod
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.request.HttpRequestWithBody
import io.javalin.Javalin
import io.javalin.core.util.Header
import org.apache.http.impl.client.HttpClients
import java.util.function.BiConsumer

class JavalinTest {

    private val app = Javalin.create().apply {
        delete("/x-test-cookie-cleaner") { ctx -> ctx.cookieMap().keys.forEach { ctx.removeCookie(it) } }
        config.showJavalinBanner = false
    }

    fun run(consumer: (Javalin, Client) -> Unit) {
        app.start(0)
        val client = Client(app.port())

        consumer(app, client)
        client.call(HttpMethod.DELETE, "/x-test-cookie-cleaner")
        app.stop()
    }

    fun run(consumer: BiConsumer<Javalin, Client>) {
        run { app, client -> consumer.accept(app, client) }
    }

    companion object {
        fun test(consumer: (Javalin, Client) -> Unit) {
            JavalinTest().run(consumer)
        }

        @JvmStatic
        fun test(consumer: BiConsumer<Javalin, Client>) {
            JavalinTest().run(consumer)
        }
    }

    class Client(port: Int) {

        private val origin = "http://localhost:$port"

        fun enableUnirestRedirects() = Unirest.setHttpClient(HttpClients.custom().build())
        fun disableUnirestRedirects() = Unirest.setHttpClient(HttpClients.custom().disableRedirectHandling().build())

        // Unirest

        fun get(path: String) = Unirest.get(origin + path).asString()
        fun getBody(path: String) = Unirest.get(origin + path).asString().body
        fun post(path: String) = Unirest.post(origin + path)
        fun call(method: HttpMethod, pathname: String) = HttpRequestWithBody(method, origin + pathname).asString()
        fun htmlGet(path: String) = Unirest.get(origin + path).header(Header.ACCEPT, "text/html").asString()
        fun jsonGet(path: String) = Unirest.get(origin + path).header(Header.ACCEPT, "application/json").asString()
        fun sse(path: String) = Unirest.get(origin + path).header("Accept", "text/event-stream").header("Connection", "keep-alive").header("Cache-Control", "no-cache").asStringAsync()
    }
}


