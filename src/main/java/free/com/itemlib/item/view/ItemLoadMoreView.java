package free.com.itemlib.item.view;//package com.shiku.commonlib.item.view;
//
//import android.content.Context;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.TextView;
//
//import com.shiku.commonlib.item.view.content.ItemLoadMore;
//
//
//public class ItemLoadMoreView extends ItemViewHolder<ItemLoadMore> {
//    private static final String loadMoreStr = "点击加载更多...";
//    private static final String loadingStr = "加载中...";
//    private static final String loadAllStr = "已加载全部数据";
//    private TextView loadMoreView;
//    private OnRefreshListener refreshListener;
//    private LoadMoreListener loadMoreListener;
//
//    public ItemLoadMoreView(Context context, ItemLoadMore itemLoadMore) {
//        super(context, itemLoadMore);
//    }
//
//    @Override
//    protected View initItemView() {
//        loadMoreListener = new LoadMoreListener();
//        loadMoreView = new TextView(context);
//        loadMoreView.setPadding(3, 30, 3, 30);
//        loadMoreView.setGravity(Gravity.CENTER);
//        loadMoreView.setTextColor(0Xff515151);
//        loadMoreView.setTextSize(15);
//        return loadMoreView;
//    }
//
//    @Override
//    protected int getBackResID() {
//        return 0;
//    }
//
//    @Override
//    public void setData(ItemLoadMore item) {
//        loadMoreView.setText(item.getValue());
//    }
//
//    public void setOnRefreshListener(OnRefreshListener refreshListener) {
//        this.refreshListener = refreshListener;
//    }
//
//    private void onLoadMore() {
//        loadMoreView.setText(loadingStr);
//        if (refreshListener != null) {
//            refreshListener.onLoadMore();
//        }
//    }
//
//    /**
//     * 通知去加载更多，和相关处理
//     */
//    public void loadMore() {
//        onLoadMore();
//    }
//
//
//    /**
//     * @param loadSize 加载数量
//     * @param pageSize 每页条数
//     */
//    @SuppressWarnings("unused")
//    public void loadComplete(int loadSize, int pageSize) {
//        loadComplete(loadSize < pageSize);
//    }
//
//    /**
//     * @param isLoadAll 是否加载完全部数据
//     */
//    public void loadComplete(boolean isLoadAll) {
//        updateData(isLoadAll);
//    }
//
//    private void updateData(boolean isLoadAll) {
//        if (isLoadAll) {
//            loadMoreView.setText(loadAllStr);
//            loadMoreView.setOnClickListener(null);
//        } else {
//            loadMoreView.setText(loadMoreStr);
//            loadMoreView.setOnClickListener(loadMoreListener);
//        }
//    }
//
//    /**
//     * 加载更多Listener
//     *
//     * @author WeiZX
//     */
//    private class LoadMoreListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View v) {
//            onLoadMore();
//        }
//    }
//}
