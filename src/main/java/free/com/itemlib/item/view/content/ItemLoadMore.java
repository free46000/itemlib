package free.com.itemlib.item.view.content;

import free.com.itemlib.item.OnLoadMoreListener;
import free.com.itemlib.item.view.ItemLoadMoreView;
import free.com.itemlib.item.view.ItemViewHolder;


import android.content.Context;
import android.view.ViewGroup;


public class ItemLoadMore extends ItemBase {
    protected OnLoadMoreListener mOnLoadMoreListener;
    public ItemLoadMoreView itemViewHolder;
    protected boolean isAutoLoadMore;

    public ItemLoadMore(OnLoadMoreListener loadMoreListener) {
        this(loadMoreListener, true);
    }

    /**
     *
     * @param loadMoreListener
     * @param isAutoLoadMore 是否需要滑动到就自动加载
     */
    public ItemLoadMore(OnLoadMoreListener loadMoreListener, boolean isAutoLoadMore) {
        if (loadMoreListener == null) {
            throw new NullPointerException();
        }
        this.mOnLoadMoreListener = loadMoreListener;
        this.isAutoLoadMore = isAutoLoadMore;
    }

    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        if (itemViewHolder == null)
            itemViewHolder = new ItemLoadMoreView(context, this);
        return itemViewHolder;
    }

    public OnLoadMoreListener getOnLoadMoreListener() {
        return mOnLoadMoreListener;
    }

    public boolean isAutoLoadMore() {
        return isAutoLoadMore;
    }

    @Override
    public boolean isClickable() {
        return false;
    }

}
