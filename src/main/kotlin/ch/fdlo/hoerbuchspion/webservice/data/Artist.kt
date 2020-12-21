package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Embedded
import ch.fdlo.hoerbuchspion.webservice.data.ArtistDetails
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

    @Embedded // effectively a OneToOne relation
    @JsonUnwrapped
    var artistDetails: ArtistDetails? = null

    override fun equals(obj: Any?): Boolean {
        return if (obj is Artist) {
            id == obj.id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ARTIST: $name ($id)"
    }
}