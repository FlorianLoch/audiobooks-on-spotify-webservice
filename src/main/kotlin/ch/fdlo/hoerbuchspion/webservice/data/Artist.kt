package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.*

@Entity(name = "Artist")
@Table(name = "ARTIST")
class Artist {
    @Id val id: String?
    val name: String? = null

    @AttributeOverrides(
        AttributeOverride(name = "large", column = Column(name = "artistImageLarge")),
        AttributeOverride(name = "medium", column = Column(name = "artistImageMedium")),
        AttributeOverride(name = "small", column = Column(name = "artistImageSmall"))
    )
    val artistImage: ImageURLs? = null
    val popularity = 0

    constructor(id: String) {
        this.id = id
    }

    private constructor() {
        this.id = null
    }
}