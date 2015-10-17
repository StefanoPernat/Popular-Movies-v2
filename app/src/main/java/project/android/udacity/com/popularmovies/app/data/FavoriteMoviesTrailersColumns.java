package project.android.udacity.com.popularmovies.app.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by stefanopernat on 17/10/15.
 *
 * Trailers database table columns
 */
public interface FavoriteMoviesTrailersColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @References(table = MovieDatabase.FAVORITES, column = FavoriteMoviesColumns._ID) @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String KEY = "key";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String NAME = "name";
}
