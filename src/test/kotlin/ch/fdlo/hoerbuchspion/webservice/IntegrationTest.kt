package ch.fdlo.hoerbuchspion.webservice

import io.jooby.JoobyTest
import io.jooby.StatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

// TODO: Add a tag to explicitly include running this test

@JoobyTest(App::class)
class IntegrationTest {

    companion object {
        val client = OkHttpClient()
    }

    @Test
    fun shouldSayHi(serverPort: Int) {
        val req = Request.Builder()
            .url("http://localhost:$serverPort/albums")
            .build()

        client.newCall(req).execute().use { rsp ->
            assertEquals("{\"total\":0,\"offset\":0,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }
}
