package io.javalin.test;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ExamplesJava {

    private static final String JSON_BODY = "{ \"name\": \"javalin\" }";

    @Test
    public void get_404() {
        final JavalinTest context = new JavalinTest();
        context.run((app, http) -> {
            final Response resp = http.get("/");
            Assert.assertThat(resp.code(), CoreMatchers.equalTo(404));
        });
    }

    @Test
    public void get() {
        JavalinTest.test((app, client) -> {
            app.get("/", ctx -> ctx.result("javalin"));

            final Response resp = client.get("/");
            Assert.assertThat(resp.code(), CoreMatchers.equalTo(200));
            Assert.assertThat(resp.body().string(), CoreMatchers.equalTo("javalin"));
        });
    }

    @Test
    public void post_withJsonBody() {
        JavalinTest.test((app, client) -> {
            app.post("/", ctx -> ctx.result(ctx.body()));

            final Response resp = client.postJson("/", JSON_BODY);
            Assert.assertThat(resp.code(), CoreMatchers.equalTo(200));
            Assert.assertThat(resp.body().string(), CoreMatchers.equalTo(JSON_BODY));
        });
    }

    @Test
    public void request_custom() {
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
            Assert.assertThat(resp.code(), CoreMatchers.equalTo(200));
            Assert.assertThat(resp.body().string(), CoreMatchers.equalTo("foo-bar"));
        });
    }
}
