package ch.fdlo.hoerbuchspion.webservice.data;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ALBUM")
public class Album {
  @Id
  private String id;
  private String name;
  @ManyToOne(cascade = CascadeType.MERGE)
  private Artist artist;
  private String releaseDate;
  private String albumArtUrl;
  private AlbumType albumType;
  private StoryType storyType;
  @Embedded // effectively a OneToOne relation
  private AlbumDetails albumDetails;

  // Required by JPA
  private Album() {
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Artist getArtist() {
    return artist;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public String getAlbumArtUrl() {
    return albumArtUrl;
  }

  public AlbumType getAlbumType() {
    return albumType;
  }

  public StoryType getStoryType() {
    return storyType;
  }

  public AlbumDetails getAlbumDetails() {
    return albumDetails;
  }

  public void setAlbumDetails(AlbumDetails albumDetails) {
    this.albumDetails = albumDetails;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Album) {
      return this.id.equals(((Album) obj).id);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }

  @Override
  public String toString() {
    return "ALBUM: " + this.name + " (" + this.id + "), " + this.releaseDate + ", " + this.storyType;
  }

  public enum StoryType {
    ABRIDGED("abridged"), UNABRIDGED("unabridged"), UNKNOWN("unknown");

    private String name;

    private StoryType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return this.name;
    }

    public static StoryType analyze(String albumName) {
      albumName = albumName.toLowerCase();

      // As the check below is a subset we need to check for the full sequence first
      if (albumName.contains("unabridged") || albumName.contains("ungekürzt")) {
        return UNABRIDGED;
      }

      if (albumName.contains("abridged") || albumName.contains("gekürzt")) {
        return ABRIDGED;
      }

      return UNKNOWN;
    }
  }

  public enum AlbumType {
    ALBUM, SINGLE, COMPILATION, APPEARS_ON, UNKNOWN;
  }
}
