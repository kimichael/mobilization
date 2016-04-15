package kimichael.com.yandexapp;

import android.graphics.Bitmap;

public class Artist {
    private int id;
    private String name;
    private String[] genres;
    private int tracks, albums;
    private String link;
    private String description;
    private Bitmap coverSmall, coverBig;
    private String linkCoverBig;

    public String getLinkCoverBig() {return linkCoverBig;}
    public int getId() {return id;}
    public String getName() {
        return name;
    }
    public String[] getGenres() {return genres;}
    public int getTracks() {return tracks;}
    public int getAlbums() {
        return albums;
    }
    public String getLink() {return link;}
    public String getDescription() {return description;}
    public Bitmap getCoverSmall() {
        return coverSmall;
    }
    public Bitmap getCoverBig() {
        return coverBig;
    }

    public void setLinkCoverBig(String linkCoverBig) {this.linkCoverBig = linkCoverBig;}
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setGenres(String[] genres) {
        this.genres = genres;
    }
    public void setTracks(int tracks) {
        this.tracks = tracks;
    }
    public void setAlbums(int albums) {
        this.albums = albums;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setCoverSmall(Bitmap coverSmall) {
        this.coverSmall = coverSmall;
    }
    public void setCoverBig(Bitmap coverBig) {
        this.coverBig = coverBig;
    }
}
