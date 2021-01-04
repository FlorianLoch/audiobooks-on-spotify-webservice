package ch.fdlo.hoerbuchspion.webservice.data

import com.fasterxml.jackson.annotation.JsonUnwrapped
import javax.persistence.*

@Entity(name = "Album")
@Table(name = "ALBUM")
class Album
private constructor() {  // We need a default constructor because of JPA
    @Id
    val id: String? = null
    val name: String? = null

    @ManyToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    val artist: Artist? = null
    val releaseDate: String? = null
    val albumArtUrl: String? = null

    @Enumerated(EnumType.STRING)
    val albumType: AlbumType? = null

    @Enumerated(EnumType.STRING)
    val storyType: StoryType? = null

    @Embedded // effectively a OneToOne relation
    @JsonUnwrapped
    var albumDetails: AlbumDetails? = null

    override fun equals(other: Any?): Boolean {
        return if (other is Album) {
            id == other.id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ALBUM: $name ($id), $releaseDate, $storyType"
    }

    enum class StoryType(private val str: String) {
        ABRIDGED("abridged"), UNABRIDGED("unabridged"), UNKNOWN("unknown");

        override fun toString(): String {
            return str
        }

        // TODO: Clean this up
        companion object {
            fun analyze(albumName: String): StoryType {
                var s = albumName
                s = s.toLowerCase()

                // As the check below is a subset we need to check for the full sequence first
                if (s.contains("unabridged") || s.contains("ungekürzt")) {
                    return UNABRIDGED
                }
                return if (s.contains("abridged") || s.contains("gekürzt")) {
                    ABRIDGED
                } else UNKNOWN
            }
        }
    }

    enum class AlbumType {
        ALBUM, SINGLE, COMPILATION, APPEARS_ON, UNKNOWN
    }
}