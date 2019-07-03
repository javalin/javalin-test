# Javalin Testing Client

Utility to write Acceptance Tests for the [Javalin](https://github.com/tipsy/javalin) Web Framework.

This tool is used in your tests to launch a short-lived Javalin server, with which you can add your request handlers and run your tests.  An http client wrapper is included, but you are free to use whatever client you like.  

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Install

javalin-test is available via Jitpack.  Click the badge for instructions on adding it to your project.

[![](https://jitpack.io/v/com.gitlab.aohara/javalin-test.svg)](https://jitpack.io/#com.gitlab.aohara/javalin-test)

Javalin 3 or greater is required.

## Usage

With Kotlin:
```kotlin
class KotlinTest {
    @Test
    fun get() {
        JavalinTest.test { app, client ->
            app.get("/") { it.result("javalin") }

            val resp = client.get("/")
            Assert.assertThat(resp.status, CoreMatchers.equalTo(200))
            Assert.assertThat(resp.body, CoreMatchers.equalTo("javalin"))
        }
    }
}

```

With Java:
```java
public class JavaTest {
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

```

See more examples in [Kotlin](https://gitlab.com/aohara/javalin-test/blob/master/src/test/kotlin/io/andrewohara/javalintest/ExamplesKotlin.kt) and [Java](https://gitlab.com/aohara/javalin-test/blob/master/src/test/java/io/andrewohara/javalintest/ExamplesJava.java).

