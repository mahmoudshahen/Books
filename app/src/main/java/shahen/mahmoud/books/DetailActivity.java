package shahen.mahmoud.books;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import shahen.mahmoud.books.Data.BookContract;
import shahen.mahmoud.books.models.Book;

public class DetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView titleTextView, dateTextView, descriptionTextView;
    Book book;
    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        titleTextView = (TextView) findViewById(R.id.tv_book_title);
        dateTextView = (TextView) findViewById(R.id.tv_published_date);
        descriptionTextView = (TextView) findViewById(R.id.tv_description);
        imageView = (ImageView) findViewById(R.id.iv_book_pic);
        addButton = (Button) findViewById(R.id.b_mark_fav);
        Bundle bundle = getIntent().getExtras();
        book = (Book) bundle.getSerializable("book");
        titleTextView.setText(book.getTitle());
        dateTextView.setText(book.getPublishedDate());
        descriptionTextView.setText(book.getDescription());
        Glide.with(this)
                .load(book.getSmallThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.notification_error)
                .into(imageView);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addButton.getText().equals("MARK AS FAVOURITE")) {
                    addDataBase();
                    addButton.setText("MARK AS UN FAVORITE");
                } else {
                    deleteDataBase();
                    addButton.setText("MARK AS FAVOURITE");
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mi_download:
                if (book.getAvailable()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getAcsTokenLink()));
                    startActivity(browserIntent);
                    Log.v("uuu", book.getAcsTokenLink());
                }
                else
                    Toast.makeText(this, book.getAcsTokenLink(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.mi_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, book.getTitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, book.getWebReaderLink());
                startActivity(shareIntent);
                break;
            case R.id.mi_online:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(book.getWebReaderLink()));
                startActivity(browserIntent);
                break;
        }
        return true;
    }

    public void addDataBase() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookTable.COLUMN_NAME_ID, book.getId());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_IMAGE, book.getSmallThumbnail());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_NAME, book.getTitle());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_DESCRIPTION, book.getDescription());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_PUBLISHED_DATE, book.getPublishedDate());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_ACSTOKENLINK, book.getAcsTokenLink());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_WEBREADERLINK, book.getWebReaderLink());
        contentValues.put(BookContract.BookTable.COLUMN_NAME_IS_AVAILABLE, book.getIsAvailableInt());

        Uri uri = this.getContentResolver().insert(BookContract.BookTable.CONTENT_URI, contentValues);
    }
    public void deleteDataBase() {
        this.getContentResolver().delete(BookContract.BookTable.CONTENT_URI, book.getId(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = this.getContentResolver().query(BookContract.BookTable.CONTENT_URI_ID, null, book.getId(), null, null);
        if (cursor != null) {
            addButton.setText("MARK AS UN FAVOURITE");
        }
    }
}
