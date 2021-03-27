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
    val preview = "" // URL to preview clip
    // TODO: Check why 'val' and 'var' are mixed
    var popularity = 0
    var label = ""
    var copyright = ""

    @Enumerated(EnumType.STRING)
    var assumedLanguage = Language.UNKNOWN

    enum class Language {
        DE, EN, FR, ES, IT, UNKNOWN;

        companion object {
            fun fromISO_639_1(lang: String): Language {
                return when (lang) {
                    "de" -> DE
                    "en" -> EN
                    "fr" -> FR
                    "es" -> ES
                    "it" -> IT
                    else -> UNKNOWN
                }
            }
        }
    }
}