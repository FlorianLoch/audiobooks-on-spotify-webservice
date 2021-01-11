package ch.fdlo.hoerbuchspion.webservice

import ch.fdlo.hoerbuchspion.webservice.data.*
import ch.fdlo.hoerbuchspion.webservice.db.QueryBuilder
import ch.fdlo.hoerbuchspion.webservice.db.queryCrawlStats
import io.jooby.*
import io.jooby.hibernate.HibernateModule
import io.jooby.hikari.HikariModule
import io.jooby.json.JacksonModule
import io.jooby.whoops.WhoopsModule
import io.jooby.annotations.QueryParam
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import java.lang.IllegalArgumentException
import java.lang.Long
import javax.persistence.EntityManager
import kotlin.math.min

@OpenAPIDefinition(
    info = Info(
        title = "Audiobooks-On-Spotify",
        description = "Providing information on audiobooks available on Spotify to make it easier for you to find good books to listen to.",
    )
)
class App : Kooby({
    // The implicit route "/swagger/" provides OpenAPI documentation
    install(OpenAPIModule())

    install(JacksonModule())

    install(HikariModule())

    decorator(CorsHandler(Cors.from(config)))

    // We need to explicitly state the package, otherwise Hibernate won't be initialized correctly (only when running tests)
    install(HibernateModule().scan("ch.fdlo.hoerbuchspion.webservice.data"))

    install(WhoopsModule())

    get("/albums", ::getAlbums)

    get("/artists", ::getArtists)

    get("/stats", ::getStats)
})

@Operation(
    summary = "Provides albums/audiobooks matching given filters. Information is wrapped inside a PaginationWrapper object.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "400",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\n" +
                            "  \"message\": \"Error description\",\n" +
                            "  \"status\": 400,\n" +
                            "  \"reason\": \"Some details on the error.\"\n" +
                            "}"
                )]
            )]
        ),
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"990023ace67a\",\"name\":\"Fancy Album\",\"artist\":{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90},\"releaseDate\":\"2020-08-01\",\"albumArtUrl\":\"http://albumart.de/image1.png\",\"albumType\":\"ALBUM\",\"storyType\":\"UNABRIDGED\",\"totalTracks\":10,\"totalDurationMs\":9078934,\"allTracksNotExplicit\":true,\"allTracksPlayable\":true,\"previewURL\":\"http://previewurl.de/sample.mp3\",\"popularity\":80,\"label\":\"Some Fancy Label\",\"copyright\":\"Copyright owner 1\",\"assumedLanguage\":\"DE\"}]}"
                )]
            )]
        )],
    parameters = [
        Parameter(
            name = "offset",
            description = "Required for pagination. Position from which to start returning records.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "0",
                type = "integer",
                minimum = "0"
            )
        ),
        Parameter(
            name = "limit",
            description = "Required for pagination. Number of records to return starting from 'offset'.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString(),
                type = "integer",
                minimum = QueryBuilder.MIN_ROWS_REQUIRED_TO_BE_REQUESTED.toString(),
                maximum = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString()
            )
        ),
        Parameter(
            name = "s",
            description = "Search term records need to match.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "%",
                type = "string"
            )
        ),
        Parameter(
            name = "unabridged_only",
            description = "Flag whether to provide only unabridged albums/audiobooks.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "false",
                type = "boolean"
            )
        ),
        Parameter(
            name = "language",
            description = "Only provide albums/audiobooks matching the given language. Parameter can be set multiple times.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                implementation = AlbumDetails.Language::class
            )
        )]
)
fun getAlbums(ctx: Context): PaginationWrapper<Album> {
    val offset = ctx.query("offset").longValue(0)
    val limit = ctx.query("limit").longValue(QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST)
    val searchTerm = ctx.query("s").value("%")
    val unabridgedOnly = ctx.query("unabridged_only").booleanValue(false)
    val languages = ctx.query("language").map {
        val langStr = it.value().toLowerCase()
        val lang = AlbumDetails.Language.fromISO_639_1(langStr)

        if (lang == AlbumDetails.Language.UNKNOWN) {
            throw IllegalArgumentException("'$langStr' is not an ISO 639-1 identifier or does not denote a supported language.")
        }

        lang
    }.toSet()
    val em = ctx.require(EntityManager::class.java)

    return QueryBuilder.fetchAlbums(em, offset, limit, searchTerm, unabridgedOnly, languages)
}

@Operation(
    summary = "Provides artists/authors matching given filters. Information is wrapped inside a PaginationWrapper object.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "default",
            content = [Content(
                    mediaType = "application/json",
                    examples = [ExampleObject(
                        value = "{\"total\":1,\"offset\":0,\"limit\":50,\"items\":[{\"id\":\"23129390abdc\",\"name\":\"Some Super Fancy Artist\",\"artistImage\":\"http://artist.com/image.png\",\"popularity\":90}]}"
                    )]
                )]
        )],
    parameters = [
        Parameter(
            name = "offset",
            description = "Required for pagination. Position from which to start returning records.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "0",
                type = "integer",
                minimum = "0"
            )
        ),
        Parameter(
            name = "limit",
            description = "Required for pagination. Number of records to return starting from 'offset'.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString(),
                type = "integer",
                minimum = QueryBuilder.MIN_ROWS_REQUIRED_TO_BE_REQUESTED.toString(),
                maximum = QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST.toString()
            )
        ),
        Parameter(
            name = "s",
            description = "Search term records need to match.",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(
                defaultValue = "%",
                type = "string"
            )
        )]
)
fun getArtists(ctx: Context): PaginationWrapper<Artist> {
    val offset = ctx.query("offset").longValue(0)
    val limit = ctx.query("limit").longValue(QueryBuilder.DEFAULT_ROW_LIMIT_PER_REQUEST)
    val searchTerm = ctx.query("s").value("%")
    val em = ctx.require(EntityManager::class.java)

    return QueryBuilder.fetchArtists(em, offset, limit, searchTerm)
}

@Operation(
    summary = "Provides some statistical values about the last run of the crawler.",
    method = "GET",
    responses = [
        ApiResponse(
            responseCode = "default",
            content = [Content(
                mediaType = "application/json",
                examples = [ExampleObject(
                    value = "{\"PLAYLISTS_CONSIDERED_COUNT\":\"3\",\"PROFILES_CONSIDERED_COUNT\":\"2\",\"ARTISTS_CONSIDERED_COUNT\":\"1\",\"ALBUMS_FOUND_COUNT\":\"0\"}"
                )]
            )]
        )]
)
fun getStats(ctx: Context): MutableMap<CrawlStatsKV.KVKey, String> {
    val em = ctx.require(EntityManager::class.java)

    return queryCrawlStats(em)
}

fun main(args: Array<String>) {
    runApp(args, App::class)
}
