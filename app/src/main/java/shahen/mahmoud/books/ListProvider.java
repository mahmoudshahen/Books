package shahen.mahmoud.books;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import shahen.mahmoud.books.Data.BookContract;
import shahen.mahmoud.books.models.Book;

/**
 * Created by harty on 7/22/2017.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Cursor cursor;

    public ListProvider(Context context) {
        this.context = context;
        getData();
    }

    public void getData() {
        cursor = context.getContentResolver().query(BookContract.BookTable.CONTENT_URI,
                new String[]{BookContract.BookTable.COLUMN_NAME_NAME}
                , null, null, null);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (cursor == null)
            return 0;
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.widget_item);
        if (cursor == null)
            return remoteView;
        cursor.moveToPosition(i);
        Log.v("weg", cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_NAME)));
        remoteView.setTextViewText(R.id.tv_widget, cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_NAME)));

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
