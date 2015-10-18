package project.android.udacity.com.popularmovies.app.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by stefanopernat on 11/10/15.
 */

@ContentProvider(
        authority = MovieProvider.AUTHORITY,
        database = MovieDatabase.class,
        packageName = "project.android.udacity.com.popularmovies.app.generated.data")
public final class MovieProvider {
    public static final String AUTHORITY =
            "project.android.udacity.com.popularmovies.app.data.provider.MovieProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Paths {
        String FAVORITES = "favorites";
        String REVIEWS = "reviews";
        String TRAILERS = "trailers";
        String MOVIE_REVIEWS = "movie_reviews";
        String MOVIE_TRAILERS = "movie_trailers";
    }

    private static Uri buildUri (String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();

        for (String path : paths){
            builder.appendPath(path);
        }

        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.FAVORITES)
    public static class Favorites{
        @ContentUri(
                path = Paths.FAVORITES,
                type = "vnd.android.cursor.dir/favorite",
                defaultSort = FavoriteMoviesColumns.VOTE_AVERAGE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Paths.FAVORITES);

        @InexactContentUri(
                name = "FAVORITE_ID",
                path = Paths.FAVORITES + "/#",
                type = "vnd.android.cursor.item/favorite",
                whereColumn = FavoriteMoviesColumns._ID,
                pathSegment = 1)
        public static Uri withId (long id){
            return buildUri(Paths.FAVORITES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = MovieDatabase.REVIEWS)
    public static class Reviews {
        @ContentUri(
                path = Paths.REVIEWS,
                type = "vnd.android.cursor.dir/review",
                defaultSort = FavoriteMoviesReviewsColumns.MOVIE_ID + " DESC")
        public static final  Uri CONTENT_URI = buildUri(Paths.REVIEWS);

        @InexactContentUri(
                name = "REVIEW_ID",
                path = Paths.REVIEWS + "/#",
                type = "vnd.android.cursor.item/review",
                whereColumn = FavoriteMoviesReviewsColumns._ID,
                pathSegment = 1)
        public static  Uri withId(String id){ return buildUri(Paths.REVIEWS, id); }

        @InexactContentUri(
                name = "REVIEWS_PER_MOVIE",
                path = Paths.REVIEWS + "/" + Paths.MOVIE_REVIEWS + "/#",
                type = "vnd.android.cursor.dir/favorite",
                whereColumn = FavoriteMoviesReviewsColumns.MOVIE_ID,
                pathSegment = 2)
        public static Uri withId(long movieId){ return buildUri(Paths.REVIEWS, Paths.MOVIE_REVIEWS, String.valueOf(movieId));}
    }

    @TableEndpoint(table = MovieDatabase.TRAILERS)
    public static class Trailers {
        @ContentUri(
                path = Paths.TRAILERS,
                type = "vnd.android.cursor.dir/trailer",
                defaultSort = FavoriteMoviesTrailersColumns.MOVIE_ID + " DESC")
        public static final  Uri CONTENT_URI = buildUri(Paths.TRAILERS);

        @InexactContentUri(
                name = "TRAILER_ID",
                path = Paths.TRAILERS + "/#",
                type = "vnd.android.cursor.item/trailer",
                whereColumn = FavoriteMoviesTrailersColumns._ID,
                pathSegment = 1)
        public static  Uri withTrailerId(long id){ return buildUri(Paths.TRAILERS, String.valueOf(id)); }

        @InexactContentUri(
                name = "TRAILER_PER_MOVIE",
                path = Paths.TRAILERS + "/" + Paths.MOVIE_TRAILERS + "/#",
                type = "vnd.android.cursor.dir/favorite",
                whereColumn = FavoriteMoviesTrailersColumns.MOVIE_ID,
                pathSegment = 2)
        public static Uri withId(long movieId){ return buildUri(Paths.TRAILERS, Paths.MOVIE_TRAILERS, String.valueOf(movieId));}


    }
}
