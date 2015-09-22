package project.android.udacity.com.popularmovies.app.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stefanopernat on 15/09/15.
 *
 * The movie class
 */
public class Movie implements Parcelable{
    private int mId;
    private String mLanguage;
    private String mPlot;
    private String mReleaseDate;
    private double mPopularity;
    private double mVoteAverage;
    private String mPoster;
    private String mBackDrop;
    private String mTitle;

    public Movie(){

    }

    public Movie(int id, String language, String plot, String releaseDate, double popularity, double voteAverage, String backdrop, String poster, String title){
        this.mId = id;
        this.mLanguage = language;
        this.mPlot = plot;
        this.mReleaseDate = releaseDate;
        this.mPopularity = popularity;
        this.mVoteAverage = voteAverage;
        this.mPoster = poster;
        this.mBackDrop = backdrop;
        this.mTitle = title;
    }

    private Movie(Parcel parcel){
        this.mId = parcel.readInt();
        this.mLanguage = parcel.readString();
        this.mPlot = parcel.readString();
        this.mReleaseDate = parcel.readString();
        this.mPopularity = parcel.readDouble();
        this.mVoteAverage = parcel.readDouble();
        this.mPoster = parcel.readString();
        this.mBackDrop = parcel.readString();
        this.mTitle = parcel.readString();

    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }

    public String getPlot() {
        return mPlot;
    }

    public void setPlot(String plot) {
        mPlot = plot;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(double popularity) {
        mPopularity = popularity;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        mVoteAverage = voteAverage;
    }

    public String getPoster() {
        return mPoster;
    }

    public void setPoster(String poster) {
        mPoster = poster;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBackDrop() {
        return mBackDrop;
    }

    public void setBackDrop(String backDrop) {
        mBackDrop = backDrop;
    }

    @Override
    public String toString() {
        return
                "id: " + mId + "\n" +
                "title: "+ mTitle + "\n" +
                "language: " + mLanguage + "\n" +
                "plot: " + mPlot + "\n" +
                "release date: " + mReleaseDate + "\n" +
                "popularity: " + mPopularity + "\n" +
                "vote average: " + mVoteAverage + "\n" +
                "poster: " + mPoster + "\n" +
                "back_drop: " + mBackDrop + "\n";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mLanguage);
        dest.writeString(mPlot);
        dest.writeString(mReleaseDate);
        dest.writeDouble(mPopularity);
        dest.writeDouble(mVoteAverage);
        dest.writeString(mPoster);
        dest.writeString(mBackDrop);
        dest.writeString(mTitle);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}