package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.*

@Embeddable
class ArtistDetails
private constructor() {  // We need a default constructor because of JPA
    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "large", column = Column(name = "artistImageLarge")),
        AttributeOverride(name = "medium", column = Column(name = "artistImageMedium")),
        AttributeOverride(name = "small", column = Column(name = "artistImageSmall"))
    )
    val artistImage: ImageURLs? = null
    val popularity = 0
}