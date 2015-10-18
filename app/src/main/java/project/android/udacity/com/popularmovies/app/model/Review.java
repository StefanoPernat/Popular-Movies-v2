package project.android.udacity.com.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stefanopernat on 17/10/15.
 */
public class Review implements Parcelable{
    private String mId;
    private long mMovieId;
    private String mAuthor;
    private String mContent;

    public Review(){}

    public Review(String id, long movieId, String author, String content){
        this.mId = id;
        this.mMovieId = movieId;
        this.mAuthor = author;
        this.mContent = content;
    }

    public Review(Parcel parcel){
        this.mId = parcel.readString();
        this.mMovieId = parcel.readLong();
        this.mAuthor = parcel.readString();
        this.mContent = parcel.readString();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public long getMovieId() {
        return mMovieId;
    }

    public void setMovieId(long movieId) {
        mMovieId = movieId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    @Override
    public String toString() {
        return
                "Id: " + this.mId + "\n" +
                "movieId: " + this.mMovieId + "\n" +
                "Author: " + this.mAuthor + "\n" +
                "Content: " + this.mContent + "\n";
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeLong(this.mMovieId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
