package ch.fdlo.hoerbuchspion.webservice.data

import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
class AlbumDetails
private constructor() { // We need a default constructor because of JPA
    val totalTracks = 0
    val totalDurationMs: Long = 0
    val allTracksNotExplicit = true
    val allTracksPlayable = true
    val previewURL = ""
    var popularity = 0
    var label = ""
    var copyright = ""

    @Enumerated(EnumType.STRING)
    var assumedLanguage = Language.UNKNOWN

    enum class Language {
        DE, EN, FR, ES, IT, UNKNOWN
    }
}