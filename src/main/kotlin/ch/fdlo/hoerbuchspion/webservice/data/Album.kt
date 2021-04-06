package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.*

@Entity(name = "Album")
@Table(name = "ALBUM")
class Album
private constructor() {  // We need a default constructor because of JPA
    @Id
    val id: String? = null
    val name: String? = null

    @ManyToMany(cascade = [CascadeType.MERGE])
    val artists: List<Artist>? = null
    val releaseDate: String? = null

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "large", column = Column(name = "albumArtLarge")),
        AttributeOverride(name = "medium", column = Column(name = "albumArtMedium")),
        AttributeOverride(name = "small", column = Column(name = "albumArtSmall"))
    )
    val albumArt: ImageURLs? = null

    @Enumerated(EnumType.STRING)
    val albumType: AlbumType? = null

    @Enumerated(EnumType.STRING)
    val storyType: StoryType? = null

    val totalTracks = 0
    val totalDurationMs: Long = 0
    val allTracksNotExplicit = true
    val allTracksPlayable = true
    val preview = "" // URL to preview clip
    val popularity = 0
    val label = ""
    val copyright = ""

    @Enumerated(EnumType.STRING)
    val assumedLanguage = Language.UNKNOWN

    enum class StoryType(private val str: String) {
        ABRIDGED("abridged"), UNABRIDGED("unabridged"), UNKNOWN("unknown");

        override fun toString(): String {
            return str
        }
    }

    enum class AlbumType {
        ALBUM, SINGLE, COMPILATION, APPEARS_ON, UNKNOWN
    }
}