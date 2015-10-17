package project.android.udacity.com.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stefanopernat on 16/10/15.
 *
 * The Trailer class
 */
public class Trailer implements Parcelable {

    public final String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";

    private long mMovieId;
    private String mKey;
    private String mName;

    public Trailer(){

    }

    public Trailer(long movieId, String key, String name){
        this.mMovieId = movieId;
        this.mKey = key;
        this.mName = name;
    }

    public Trailer(Parcel parcel){
        this.mMovieId = parcel.readLong();
        this.mKey = parcel.readString();
        this.mName = parcel.readString();
    }

    public long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(long movieId) {
        mMovieId = movieId;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return
                "movieId: "+ mMovieId + "\n" +
                "key: " + mKey + "\n" +
                "name: "+ mName + "\n";
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mMovieId);
        dest.writeString(this.mKey);
        dest.writeString(this.mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>(){
        @Override
        public Trailer createFromParcel(Parcel source) {
            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
