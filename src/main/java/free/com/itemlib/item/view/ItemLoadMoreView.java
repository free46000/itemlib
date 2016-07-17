package free.com.itemlib.item.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import free.com.itemlib.item.OnLoadMoreListener;
import free.com.itemlib.item.view.content.ItemLoadMore;


public class ItemLoadMoreView extends ItemViewHolder<ItemLoadMore> {
    private static final String loadMoreStr = "点击加载更多...";
    private static final String loadingStr = "加载中...";
    private static final String loadAllStr = "已加载全部数据";
    private TextView loadMoreView;
    private OnLoadMoreListener loadMoreListener;

    public ItemLoadMoreView(Context context, ItemLoadMore itemLoadMore) {
        super(context, itemLoadMore);
    }

    @Override
    protected View initItemView() {
        loadMoreListener = currItem.getOnLoadMoreListener();
        LinearLayout linearLayout = new LinearLayout(context);
        loadMoreView = new TextView(context);
        loadMoreView.setPadding(3, 30, 3, 30);
        loadMoreView.setGravity(Gravity.CENTER);
        loadMoreView.setTextColor(0Xff515151);
        loadMoreView.setTextSize(15);
//        linearLayout.addView(loadMoreView);
        updateLoadMoreText(1);
        return loadMoreView;
    }

    @Override
    protected int getBackResID() {
        return 0;
    }

    @Override
    public void setData(ItemLoadMore item) {
        if (item.isAutoLoadMore()) {
            onLoadMore();
        }
    }


    private void onLoadMore() {
        updateLoadMoreText(3);
        loadMoreListener.onLoadMore();
    }

    /**
     * 通知去加载更多，和相关处理
     */
    public void loadMore() {
        onLoadMore();
    }

    /**
     * @param isLoadAll 是否加载完全部数据
     */
    public void loadComplete(boolean isLoadAll) {
        updateLoadMoreText(isLoadAll ? 2 : 1);
    }

    /**
     * @param state 1:has more 2:load all 3:loading
     */
    protected void updateLoadMoreText(int state) {
        if (state == 1) {
            loadMoreView.setText(loadMoreStr);
            loadMoreView.setOnClickListener(loadMoreListener);
        } else if (state == 3) {
            loadMoreView.setText(loadingStr);
            loadMoreView.setOnClickListener(null);
        } else {
            loadMoreView.setText(loadAllStr);
            loadMoreView.setOnClickListener(null);
        }

    }


}
