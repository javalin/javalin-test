package io.javalin.test

import io.javalin.Javalin
import io.javalin.plugin.json.JavalinJson
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaType
import java.net.URL

class JavalinTest {

    private val app = Javalin.create().apply {
        delete("/x-test-cookie-cleaner") { ctx -> ctx.cookieMap().keys.forEach { ctx.removeCookie(it) } }
        config.showJavalinBanner = false
    }

    fun run(consumer: (Javalin, Client) -> Unit) {
        app.start(0)
        val client = Client(app.port())

        consumer(app, client)
        client.delete("/x-test-cookie-cleaner")
        app.stop()
    }

    fun run(consumer: ThrowingBiConsumer<Javalin, Client>) {
        run { app, client -> consumer.acceptThrows(app, client) }
    }

    companion object {
        fun test(consumer: (Javalin, Client) -> Unit) {
            JavalinTest().run(consumer)
        }

        @JvmStatic
        fun test(consumer: ThrowingBiConsumer<Javalin, Client>) {
            JavalinTest().run(consumer)
        }
    }
}

class Client(private val port: Int) {

    private val client = OkHttpClient.Builder().build()

    fun getUrl(path: String, queryParams: Map<String, String> = emptyMap()): URL {
        return HttpUrl.Builder().apply {
            scheme("http")
            host("localhost")
            port(port)
            addPathSegments(path.trimStart('/'))
            queryParams.forEach { addQueryParameter(it.key, it.value) }
        }.build().toUrl()
    }

    @JvmOverloads
    fun request(path: String, queryParams: Map<String, String> = emptyMap()): Request.Builder {
        val url = getUrl(path, queryParams)
        return Request.Builder().url(url)
    }

    fun execute(request: Request): Response = client.newCall(request).execute()
    fun execute(request: Request.Builder): Response = execute(request.build())

    @JvmOverloads
    fun get(path: String, queryParams: Map<String, String> = emptyMap()) = execute(request(path, queryParams).get())
    fun delete(path: String) = execute(request(path).delete("".toRequestBody()))

    fun post(path: String, json: Any? = null): Response {
        val request = request(path).post(json.toBody())
        return execute(request)
    }

    fun put(path: String, json: Any? = null): Response {
        val request = request(path).put(json.toBody())
        return execute(request)
    }

    fun patch(path: String, json: Any? = null): Response {
        val request = request(path).patch(json.toBody())
        return execute(request)
    }

    companion object {
        private val JSON_TYPE = "application/json".toMediaType()
        private fun Any?.toBody(): RequestBody{
            return if (this == null) {
                RequestBody.create(null, ByteArray(0), 0, 0)
            } else {
                JavalinJson.toJson(this).toRequestBody(JSON_TYPE)
            }
        }
    }
}


