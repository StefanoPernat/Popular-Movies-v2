package project.android.udacity.com.popularmovies.app.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by stefanopernat on 11/10/15.
 */

@Database(
        version = MovieDatabase.VERSION,
        packageName = "project.android.udacity.com.popularmovies.app.generated.data")
public class MovieDatabase {
    public static final int VERSION = 1;

    @Table(FavoriteMoviesColumns.class)
    public static final String FAVORITES = "favorites";
}
