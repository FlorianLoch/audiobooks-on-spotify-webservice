package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.db.QueryBuilder
import io.jooby.Jooby
import io.jooby.JoobyTest
import io.jooby.StatusCode
import io.jooby.require
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import javax.persistence.EntityManager
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*


@JoobyTest(App::class)
class IntegrationTest {

    companion object {
        val client = OkHttpClient()

        @BeforeAll
        @JvmStatic
        fun setup(app: Jooby) {
            val em = app.require(EntityManager::class)

            println("Going to run initialization queries. Check Hibernate's log.")

            em.transaction.begin()

            this::class.java.classLoader.getResourceAsStream("queries.sql").bufferedReader().useLines {
                it.forEach {
                    em.createNativeQuery(it).executeUpdate()
                }
            }

            em.transaction.commit()
        }
    }

    @Test
    fun shouldProvideASingleAlbumByItsId(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums/990023ace67a")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldReturnAnErrorWhenAskingForAlbumWithInvalidId(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums/990023a")
                .header("Accept", "application/json")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"message\":\"Identifier '990023a' cannot be resolved to an album.\",\"statusCode\":400,\"reason\":\"Bad Request\"}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.BAD_REQUEST.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideASingleArtistByItsId(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/artists/23129390abdc")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideAlbumsViaRESTAPIInDescendingOrderByPopularity(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"1992-08-03\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image2_lg.png\",\"medium\":\"http://albumart.de/image2_md.png\",\"small\":\"http://albumart.de/image2_sm.png\"},\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"EN\"},{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideArtistsViaRESTAPI(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/artists")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun searchShouldNotBeCaseSensitive(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?s=fANCY aLBUM")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), startsWith("{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\""))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun emptySearchQueryResultsInWildcardSearch(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?s=")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), startsWith("{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{"))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun omittedSearchQueryResultsInWildcardSearch(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), startsWith("{\"total\":2,\"offset\":0,\"limit\":50,\"items\":[{"))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun searchTermIsWrappedWithWildcards(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?s=cy Al")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), startsWith("{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\""))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun emptyListShouldBeReturnedWhenTermDoesNotMatch(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?s=Some Album we do not know about")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":0,\"offset\":0,\"limit\":50,\"items\":[]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldOnlyReturnUnabridgedAlbumsWhenAskedTo(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?unabridged_only=true")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldBeCapableOfFilteringByLanguageValidLanguage(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?language=de")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldBeCapableOfFilteringByLanguageInvalidLanguage(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?language=de,en")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), containsString("&apos;de,en&apos; is not an ISO 639-1 identifier or does not denote a supported language."))
            assertEquals(StatusCode.BAD_REQUEST.value(), rsp.code)
        }
    }

    @Test
    fun shouldBeCapableOfFilteringByLanguageSeveralLanguages(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?language=de&language=en&language=it")
                .build()
        ).execute().use { rsp ->
            assertThat(rsp.body!!.string(), startsWith("{\"total\":2,\"offset\":0,\"limit\":50,\"items\":"))
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }


    @Test
    fun apiShouldSupportPagination(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?limit=1")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":2,\"offset\":0,\"limit\":1,\"items\":[{\"id\":\"2847a676de8b\",\"name\":\"Super Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"1992-08-03\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image2_lg.png\",\"medium\":\"http://albumart.de/image2_md.png\",\"small\":\"http://albumart.de/image2_sm.png\"},\"albumType\":\"COMPILATION\",\"storyType\":\"ABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 2\",\"assumedLanguage\":\"EN\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?limit=1&offset=1")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"total\":2,\"offset\":1,\"limit\":1,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtURLs\":{\"large\":\"http://albumart.de/image1_lg.png\",\"medium\":\"http://albumart.de/image1_md.png\",\"small\":\"http://albumart.de/image1_sm.png\"},\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":60,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?limit=${QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST + 1}")
                .build()
        ).execute().use { rsp ->
            assertTrue(
                rsp.body!!.string()
                    .startsWith("{\"total\":2,\"offset\":0,\"limit\":${QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST},\"items\":[{")
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }

        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/albums?offset=2")
                .build()
        ).execute().use { rsp ->
            assertEquals("{\"total\":2,\"offset\":2,\"limit\":50,\"items\":[]}", rsp.body!!.string())
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }

    @Test
    fun shouldProvideCrawlStats(serverPort: Int) {
        client.newCall(
            Request.Builder()
                .url("http://localhost:$serverPort/api/stats")
                .build()
        ).execute().use { rsp ->
            assertEquals(
                "{\"PLAYLISTS_CONSIDERED_COUNT\":\"3\",\"PROFILES_CONSIDERED_COUNT\":\"2\",\"ARTISTS_CONSIDERED_COUNT\":\"1\",\"ALBUMS_FOUND_COUNT\":\"0\"}",
                rsp.body!!.string()
            )
            assertEquals(StatusCode.OK.value(), rsp.code)
        }
    }
}
