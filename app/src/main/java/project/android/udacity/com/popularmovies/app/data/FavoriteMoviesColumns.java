package project.android.udacity.com.popularmovies.app.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by stefanopernat on 11/10/15.
 */
public interface FavoriteMoviesColumns {

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String LANGUAGE = "language";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String PLOT = "plot";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE = "release_date";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String POPULARITY = "popularity";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String VOTE_AVERAGE = "vote_average";

    @DataType(DataType.Type.TEXT)
    public static final String POSTER = "poster_path";

    @DataType(DataType.Type.TEXT)
    public static final String BACKDROP = "backdrop_path";
}
