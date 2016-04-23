package kimichael.com.yandexapp;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Artist implements Parcelable {
    public int id;
    public String name;
    public String[] genres;
    public int tracks, albums;
    public String link;
    public String description;
    public String linkCoverBig, linkCoverSmall;
    public boolean isShowedAlready;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeStringArray(this.genres);
        dest.writeInt(this.tracks);
        dest.writeInt(this.albums);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.linkCoverBig);
        dest.writeString(this.linkCoverSmall);
        dest.writeByte(isShowedAlready ? (byte) 1 : (byte) 0);
    }

    public Artist() {
    }

    protected Artist(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.genres = in.createStringArray();
        this.tracks = in.readInt();
        this.albums = in.readInt();
        this.link = in.readString();
        this.description = in.readString();
        this.linkCoverBig = in.readString();
        this.linkCoverSmall = in.readString();
        this.isShowedAlready = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}