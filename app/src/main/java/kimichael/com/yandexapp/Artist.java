package kimichael.com.yandexapp;

import android.graphics.Bitmap;

public class Artist {
    private int id;
    private String name;
    private String[] genre;
    private int tracks, albums;
    private String link;
    private String description;
    private Bitmap coverSmall, coverBig;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenre() {
        return genre;
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getCoverSmall() {
        return coverSmall;
    }

    public Bitmap getCoverBig() {
        return coverBig;
    }
}
