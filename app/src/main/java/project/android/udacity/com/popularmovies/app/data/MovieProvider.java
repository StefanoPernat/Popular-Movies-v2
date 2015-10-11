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
}
