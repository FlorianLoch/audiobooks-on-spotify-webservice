package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.*

@Entity
@Table(name = "CRAWL_STATS_KV")
class CrawlStatsKV
private constructor() {  // We need a default constructor because of JPA
    @Id
    @Enumerated(EnumType.STRING)
    val key: KVKey? = null
    val value: String? = null

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return if (other is CrawlStatsKV) {
            key == other.key
        } else false
    }

    enum class KVKey {
        PLAYLISTS_CONSIDERED_COUNT, PROFILES_CONSIDERED_COUNT, ARTISTS_CONSIDERED_COUNT, ALBUMS_FOUND_COUNT, ALBUM_DETAILS_FETCHED_COUNT, ARTIST_DETAILS_FETCHED_COUNT, DURATION_LAST_RUN_MS, TOTAL_API_REQUESTS_COUNT, LAST_RUN_FINISHED_AT
    }
}