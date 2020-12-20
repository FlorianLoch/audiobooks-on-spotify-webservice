package ch.fdlo.hoerbuchspion.webservice.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ARTIST")
public class Artist {

  @Id
  private String id;
  private String name;
  @Embedded // effectively a OneToOne relation
  private ArtistDetails artistDetails;

  // Required by JPA
  private Artist() {
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setArtistDetails(ArtistDetails artistDetails) {
    this.artistDetails = artistDetails;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Artist) {
      return this.id.equals(((Artist) obj).id);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return "ARTIST: " + this.name + " (" + this.id + ")";
  }
}
