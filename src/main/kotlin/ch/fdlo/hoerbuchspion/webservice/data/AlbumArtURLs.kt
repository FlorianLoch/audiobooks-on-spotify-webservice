package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class AlbumArtURLs
private constructor() { // We need a default constructor because of JPA
    @Column(name = "largeAlbumArtURL")
    val large = ""
    @Column(name = "mediumAlbumArtURL")
    val medium = ""
    @Column(name = "smallAlbumArtURL")
    val small = ""
}
