package shahen.mahmoud.books.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by harty on 7/21/2017.
 */

public class BookOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "books.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookContract.BookTable.TABLE_NAME + " ( " +
            BookContract.BookTable.COLUMN_NAME_ID + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_IMAGE + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_NAME + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_DESCRIPTION + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_PUBLISHED_DATE + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_ACSTOKENLINK + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_WEBREADERLINK + TEXT_TYPE + COMA_SEP +
            BookContract.BookTable.COLUMN_NAME_IS_AVAILABLE + INTEGER_TYPE + " );";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXIST " + BookContract.BookTable.TABLE_NAME;

    public BookOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
