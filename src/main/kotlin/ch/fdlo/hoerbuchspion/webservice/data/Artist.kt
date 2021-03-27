package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.*

@Entity(name = "Artist")
@Table(name = "ARTIST")
class Artist
private constructor() {  // We need a default constructor because of JPA
    @Id
    val id: String? = null
    val name: String? = null

    @AttributeOverrides(
        AttributeOverride(name = "large", column = Column(name = "artistImageLarge")),
        AttributeOverride(name = "medium", column = Column(name = "artistImageMedium")),
        AttributeOverride(name = "small", column = Column(name = "artistImageSmall"))
    )
    val artistImage: ImageURLs? = null
    val popularity = 0
}