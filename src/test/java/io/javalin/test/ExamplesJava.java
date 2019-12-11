package io.javalin.test;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

public class ExamplesJava {

    @Test
    public void get_404() {
        JavalinTest.test((app, http) -> {
            final Response resp = http.get("/");
            assertThat(resp.code(), equalTo(404));
        });
    }

    @Test
    public void get() {
        JavalinTest.test((app, client) -> {
            app.get("/", ctx -> ctx.result("javalin"));

            final Response resp = client.get("/");
            assertThat(resp.code(), equalTo(200));
            assertThat(resp.body().string(), equalTo("javalin"));
        });
    }

    @Test
    public void post_withJsonBody() {
        JavalinTest.test((app, client) -> {
            app.post("/", ctx -> ctx.result(ctx.body()));

            final Map<String, String> requestJson = Collections.singletonMap("name", "javalin");
            final Response resp = client.post("/", Collections.emptyMap(), Collections.emptyMap(), requestJson);
            assertThat(resp.code(), equalTo(200));
            assertThat(resp.body().string(), equalTo("{\"name\":\"javalin\"}"));
        });
    }

    @Test
    public void custom_http_client() {
        JavalinTest.test((app, client) -> {
            app.post("/", ctx -> {
                final String result = ctx.header("FOO") + "-" + ctx.body();
                ctx.result(result);
            });

            final Request request = client.request("/")
                    .post(RequestBody.create("bar", MediaType.get("text/plain")))
                    .addHeader("FOO", "foo")
                    .build();

            final Response resp = client.execute(request);
            assertThat(resp.code(), equalTo(200));
            assertThat(resp.body().string(), equalTo("foo-bar"));
        });
    }
}
