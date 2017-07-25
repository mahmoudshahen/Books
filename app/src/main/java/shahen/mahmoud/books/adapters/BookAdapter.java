package shahen.mahmoud.books.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import shahen.mahmoud.books.R;
import shahen.mahmoud.books.models.Book;

/**
 * Created by harty on 7/18/2017.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    List<Book> bookList;
    Context context;
    final private ListItemClickListener listItemClickListener;
    public BookAdapter(List<Book> books, Context context, ListItemClickListener listener) {
        this.bookList = books;
        this.context = context;
        this.listItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(bookList.get(position).getTitle());
        Glide.with(context)
                .load(bookList.get(position).getSmallThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.notification_error)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_image);
            textView = itemView.findViewById(R.id.tv_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
    public interface ListItemClickListener{
        void onListItemClick(int clickedItemIndex);
    }
}
