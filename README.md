[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Javalin Test - An Acceptance Testing tool for the Javalin Web Framework

javalin-test  is a tool that will launch a short-lived javalin server in your tests.  From here, you can register your request handlers, and use the built-in http client to test your request handlers.


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

            client.get("/").use { resp ->
                Assert.assertThat(resp.code, CoreMatchers.equalTo(200))
                Assert.assertThat(resp.body?.string(), CoreMatchers.equalTo("javalin"))
            }
        }
    }
}

```

With Java:
```java
public class JavaTest {
   @Test
   public void get() {
       JavalinTest.test((app, client) -> {
           app.get("/", ctx -> ctx.result("javalin"));
           
           final Response resp = client.get("/");
           Assert.assertThat(resp.code(), CoreMatchers.equalTo(200));
           Assert.assertThat(resp.body().string(), CoreMatchers.equalTo("javalin"));
       });
   }
}

```

See more examples in [Kotlin](https://github.com/javalin/javalin-test/blob/master/src/test/kotlin/io/andrewohara/javalintest/ExamplesKotlin.kt) and [Java](https://github.com/javalin/javalin-test/blob/master/src/test/java/io/andrewohara/javalintest/ExamplesJava.java).

