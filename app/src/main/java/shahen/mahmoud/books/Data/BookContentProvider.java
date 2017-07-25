package shahen.mahmoud.books.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by harty on 7/21/2017.
 */

public class BookContentProvider extends ContentProvider {

    private BookOpenHelper bookOpenHelper;
    public static final short BOOKS = 100;
    public static final short BOOKS_WITH_ID = 101;
    public static final UriMatcher uriMatcher = buildUriMatcher();
    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        uriMatcher.addURI(BookContract.AUTHORITY, BookContract.PATH_BOOKS + "/id", BOOKS_WITH_ID);

        return uriMatcher;
    }
    @Override
    public boolean onCreate() {
        bookOpenHelper = new BookOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        SQLiteDatabase db = bookOpenHelper.getReadableDatabase();
        Cursor retCursor = null;
        switch (uriMatcher.match(uri)) {

            case BOOKS: {
                retCursor =  db.query(BookContract.BookTable.TABLE_NAME,
                        strings, s, strings1, null, null, s1);
                break;
            }
            case  BOOKS_WITH_ID: {
                retCursor = db.rawQuery("select* from " + BookContract.BookTable.TABLE_NAME + " WHERE " +
                        BookContract.BookTable.COLUMN_NAME_ID + " = " + "\""+ s +"\"", null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        if(retCursor.isAfterLast())
            return null;

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = bookOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)){
            case BOOKS:{
                long newRowID;
                newRowID = db.insert(BookContract.BookTable.TABLE_NAME, null, contentValues);

                if(newRowID > 0)
                {
                    returnUri = ContentUris.withAppendedId(BookContract.BookTable.CONTENT_URI, newRowID);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = bookOpenHelper.getWritableDatabase();
        int count = db.delete(BookContract.BookTable.TABLE_NAME, BookContract.BookTable.COLUMN_NAME_ID +"="+ "\""+s+"\"", strings);

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
