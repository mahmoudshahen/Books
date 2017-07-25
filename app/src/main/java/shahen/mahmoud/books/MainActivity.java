package shahen.mahmoud.books;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import shahen.mahmoud.books.Data.BookContract;
import shahen.mahmoud.books.adapters.BookAdapter;
import shahen.mahmoud.books.models.Book;

public class MainActivity extends AppCompatActivity implements BookAdapter.ListItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    List<Book> booksList;
    RecyclerView booksRecycler;
    BookAdapter bookAdapter;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    final int LOADER_LOAD = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        booksRecycler = (RecyclerView) findViewById(R.id.rv_books);
        progressBar = (ProgressBar) findViewById(R.id.pb_load);
        booksRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        booksList = new ArrayList<>();
        setBookAdapter();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String state = sharedPreferences.getString(getString(R.string.key_list), getString(R.string.online));
        if (savedInstanceState != null) {
            booksList = (List<Book>) savedInstanceState.getSerializable("books");
            setBookAdapter();
        }
        else if (state.equals(getString(R.string.offline)))
            getLoaderManager().initLoader(LOADER_LOAD, null, this);
        else if (isNetworkAvailable(this))
            getBooks();
        else
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3424516490633842~1969192418");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        //ca-app-pub-3424516490633842/6823075609
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequestBuilder = new AdRequest.Builder().build();
        adView.loadAd(adRequestBuilder);

    }

    public void getBooks() {
        String URL = getString(R.string.BASE_URL)+"&"+getString(R.string.PRINT_TYPE)+"=books&"+
                    getString(R.string.KEY_API);
        Log.v("URL", (URL));
        progressBar.setVisibility(View.VISIBLE);
        Ion.with(this)
                .load(URL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        booksList.clear();
                        JsonArray jsonArray = result.get("items").getAsJsonArray();
                        for (int i=0 ; i<jsonArray.size() ; i++) {
                            Book book = new Book();
                            book.setId(jsonArray.get(i).getAsJsonObject().get("id").getAsString());
                            JsonObject volumeInfo = jsonArray.get(i).getAsJsonObject().get("volumeInfo").getAsJsonObject();
                            JsonObject accessInfo = jsonArray.get(i).getAsJsonObject().get("accessInfo").getAsJsonObject();
                            book.setTitle(volumeInfo.get("title").getAsString());
                            book.setPublishedDate(volumeInfo.get("publishedDate").toString().substring(1,
                                    volumeInfo.get("publishedDate").toString().length()-1));
                            if (volumeInfo.has("description"))
                                book.setDescription(volumeInfo.get("description").toString().substring(1,
                                        volumeInfo.get("description").toString().length()-1));
                            else
                                book.setDescription("No Description available");
                            book.setSmallThumbnail(volumeInfo.get("imageLinks").getAsJsonObject().get("smallThumbnail").getAsString()
                            .substring(0, volumeInfo.get("imageLinks").getAsJsonObject().get("smallThumbnail").getAsString().length()-1));
                            if (accessInfo.has("webReaderLink"))
                                book.setWebReaderLink(accessInfo.get("webReaderLink").getAsString());
                            else
                                book.setWebReaderLink("no reader link available");
                            book.setAvailable(accessInfo.get("pdf").getAsJsonObject().get("isAvailable").getAsBoolean());
                            if (accessInfo.get("pdf").getAsJsonObject().has("acsTokenLink"))
                                book.setAcsTokenLink(accessInfo.get("pdf").getAsJsonObject().get("acsTokenLink").getAsString());
                            else
                                book.setAcsTokenLink("no download link available");
                            booksList.add(book);
                            Log.v("books", book.getAvailable()+"");
                        }
                        bookAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.v("bookListSize", booksList.size()+"");
                    }
                });
    }
    public void setBookAdapter() {
        bookAdapter = new BookAdapter(booksList, MainActivity.this, MainActivity.this);
        booksRecycler.setAdapter(bookAdapter);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Toast.makeText(this, clickedItemIndex+"", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", booksList.get(clickedItemIndex));
        intent.putExtras(bundle);
        startActivity(intent);

    }
    public boolean isNetworkAvailable(final Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mi_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return true;
    }
    public void getBooksFromDataBase(Cursor cursor) {
      //  Cursor cursor = this.getContentResolver().query(BookContract.BookTable.CONTENT_URI, null, null, null, null);

        booksList.clear();
        if (cursor == null) {
            bookAdapter.notifyDataSetChanged();
            return;
        }
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setId(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_ID)));
            book.setSmallThumbnail(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_IMAGE)));
            book.setTitle(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_NAME)));
            book.setDescription(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_DESCRIPTION)));
            book.setPublishedDate(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_PUBLISHED_DATE)));
            book.setAcsTokenLink(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_ACSTOKENLINK)));
            book.setWebReaderLink(cursor.getString(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_WEBREADERLINK)));
            book.setIsAvailableInt(cursor.getInt(cursor.getColumnIndex(BookContract.BookTable.COLUMN_NAME_IS_AVAILABLE)));
            booksList.add(book);
        }
        cursor.close();
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Log.v("ttt", s);
        if (sharedPreferences.getString(s, "null").equals(getString(R.string.online))) {
            getBooks();
        } else if (sharedPreferences.getString(s, "null").equals(getString(R.string.offline))) {
            getLoaderManager().initLoader(LOADER_LOAD, null, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("books", (Serializable) booksList);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (i == LOADER_LOAD)
            return  new CursorLoader(this, BookContract.BookTable.CONTENT_URI, null, null, null, null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        getBooksFromDataBase(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
