package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class ImageURLs
private constructor() { // We need a default constructor because of JPA
    val large = ""
    val medium = ""
    val small = ""
}
