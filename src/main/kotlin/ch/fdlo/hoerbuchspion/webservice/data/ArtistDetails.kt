package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Embeddable

@Embeddable
class ArtistDetails
private constructor() {  // We need a default constructor because of JPA
    var artistImage = ""
    var popularity = 0
}