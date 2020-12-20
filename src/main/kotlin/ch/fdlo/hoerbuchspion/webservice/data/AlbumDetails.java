package ch.fdlo.hoerbuchspion.webservice.data;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class AlbumDetails {
  private int totalTracks;
  private long totalDurationMs;
  private boolean allTracksNotExplicit = true;
  private boolean allTracksPlayable = true;
  private String previewURL = "";
  private int popularity;
  private String label = "";
  private String copyright = "";
  @Enumerated(EnumType.STRING)
  private Language assumedLanguage = Language.UNKNOWN;

  // Required by JPA
  private AlbumDetails() {}

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setCopyright(String copyright) {
    this.copyright = copyright;
  }

  public void setAssumedLanguage(Language assumedLanguage) {
    this.assumedLanguage = assumedLanguage;
  }

  public enum Language {
    DE, EN, FR, ES, IT, UNKNOWN;
  }
}
