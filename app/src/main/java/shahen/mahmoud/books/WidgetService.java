package shahen.mahmoud.books;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by harty on 7/22/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new ListProvider(this.getApplicationContext());
    }
}
