package ch.fdlo.hoerbuchspion.webservice.data;

import javax.persistence.Embeddable;

@Embeddable
public class ArtistDetails {
  private String artistImage = "";
  private int popularity;

  // Required by JPA
  private ArtistDetails() {}

  public void setArtistImage(String artistImage) {
    this.artistImage = artistImage;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }
}
