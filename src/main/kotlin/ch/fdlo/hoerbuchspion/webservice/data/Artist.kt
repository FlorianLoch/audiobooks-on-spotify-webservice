package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Embedded
import com.fasterxml.jackson.annotation.JsonUnwrapped
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "Artist")
@Table(name = "ARTIST")
class Artist
private constructor() {  // We need a default constructor because of JPA
    @Id
    val id: String? = null
    val name: String? = null

    @Embedded // TODO: It's there same here - no point in embedding, just add them in-place.
    @JsonUnwrapped
    var artistDetails: ArtistDetails? = null

    override fun equals(other: Any?): Boolean {
        return if (other is Artist) {
            id == other.id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ARTIST: $name ($id)"
    }
}