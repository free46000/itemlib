package free.com.itemlib.item.listener;

import android.view.View;

/**
 * Created by free46000 on 2016/7/6 0006.
 */
public abstract class OnLoadMoreListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        onLoadMore();
    }

    public abstract void onLoadMore();
}
