package shahen.mahmoud.books.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by harty on 7/21/2017.
 */

public class BookContract {

    public static final String AUTHORITY = "shahen.mahmoud.books";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_BOOKS = "favourite";
    private BookContract() {}

    public static abstract class BookTable implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build();

        public static final Uri CONTENT_URI_ID = CONTENT_URI.buildUpon().appendPath("id").build();

        public static final String TABLE_NAME = "favourite";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_IMAGE = "imageUrl";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_PUBLISHED_DATE = "published";
        public static final String COLUMN_NAME_ACSTOKENLINK = "acsTokenLink";
        public static final String COLUMN_NAME_WEBREADERLINK = "webReaderLink";
        public static final String COLUMN_NAME_IS_AVAILABLE = "isAvailable";
    }
}
