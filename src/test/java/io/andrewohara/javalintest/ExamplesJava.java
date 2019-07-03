package io.andrewohara.javalintest;

import com.mashape.unirest.http.HttpResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ExamplesJava {

    @Test
    public void get_404() {
        final JavalinTest context = new JavalinTest();
        context.run((app, http) -> {
            final HttpResponse<String> resp = http.get("/");
            Assert.assertThat(resp.getStatus(), CoreMatchers.equalTo(404));
        });
    }

    @Test
    public void get() {
        JavalinTest.test((app, http) -> {
            app.get("/", ctx -> ctx.result("javalin"));

            final HttpResponse<String> resp = http.get("/");
            Assert.assertThat(resp.getStatus(), CoreMatchers.equalTo(200));
            Assert.assertThat(resp.getBody(), CoreMatchers.equalTo("javalin"));
        });
    }
}
