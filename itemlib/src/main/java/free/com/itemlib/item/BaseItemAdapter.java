package free.com.itemlib.item;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import free.com.itemlib.item.animation.AnimationLoader;
import free.com.itemlib.item.animation.BaseAnimation;
import free.com.itemlib.item.common.ShrinkViewUtil;
import free.com.itemlib.item.listener.OnItemClickListener;
import free.com.itemlib.item.listener.OnItemLongClickListener;
import free.com.itemlib.item.listener.OnLoadMoreListener;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemLoadMore;
import free.com.itemlib.item.view.content.ItemSimple;

public class BaseItemAdapter extends RecyclerView.Adapter<BaseItemAdapter.RecyclerViewHolder> {
    protected final List<String> mTypeList = new ArrayList<>();
    protected final List<Item> mTypeItemList = new ArrayList<>();
    protected Context context;
    private List<Item> dataItemList = new ArrayList<>();
    private List<Item> headItemList = new ArrayList<>();
    private List<Item> footItemList = new ArrayList<>();
    private List<ItemViewHolder> viewHolderList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    //shrink相关  用来规范name—value模式左侧视图宽度
    protected ShrinkViewUtil shrinkViewUtil = new ShrinkViewUtil();
    //加载更多
    protected ItemLoadMore itemLoadMore;
    //动画相关
    protected AnimationLoader animationLoader = new AnimationLoader();


    public BaseItemAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置数据源List 建议在创建Adapter时调用此方法设置数据源
     *
     * @param itemList 数据源ItemList
     */
    public void setDataItemList(List<? extends Item> itemList) {
        setData(itemList);
    }

    /**
     * 添加数据源 建议动态添加的时候使用此方法，比直接set效率更高
     *
     * @param list 数据源ItemList
     */
    public void addDataItemList(List<? extends Item> list) {
        addData(list);
    }

    /**
     * 添加数据源 建议动态添加的时候使用此方法，比直接set效率更高
     *
     * @param item 数据源Item
     */
    public void addDataItem(Item... item) {
        addData(Arrays.asList(item));
    }

    /**
     * 为指定位置添加数据源 建议动态添加的时候使用此方法，比直接set效率更高
     *
     * @param position 一定要包含HeadView的count #getHeadCount()
     * @param item     数据源Item
     */
    public void addDataItem(int position, Item... item) {
        addData(position, Arrays.asList(item));
    }

    /**
     * 设置Item最终调用方法
     */
    private void setData(List<? extends Item> itemList) {
        clearParams();
        dataItemList = (List<Item>) itemList;
        shrinkViewUtil.initShrinkParams(dataItemList);
        notifyDataSetChanged();
    }

    private void addData(List<? extends Item> itemList) {
        addData(dataItemList.size() + getHeadCount(), itemList);
    }

    /**
     * 添加Item最终调用方法
     *
     * @param position 要包含HeadView的count #getHeadCount()
     */
    private void addData(int position, List<? extends Item> itemList) {
        dataItemList.addAll(position - getHeadCount(), itemList);
        shrinkViewUtil.addShrinkParams(itemList);
        notifyItemRangeInserted(position, itemList.size());
    }

    /**
     * 移动Item的位置 包括数据源和界面的移动
     *
     * @param fromPosition Item之前所在位置 要包含HeadView的count #getHeadCount()
     * @param toPosition   Item新的位置 要包含HeadView的count #getHeadCount()
     */
    public void moveDataItem(int fromPosition, int toPosition) {
        int fromDataPos = fromPosition - getHeadCount();
        int toDatePos = toPosition - getHeadCount();
        if (fromDataPos > toDatePos) {
            for (int i = fromDataPos; i > toDatePos; i--) {
                Collections.swap(dataItemList, i, i - 1);
            }
//            notifyItemMoved(toPosition, fromPosition);
        } else {
            for (int i = fromDataPos; i < toDatePos; i++) {
                Collections.swap(dataItemList, i, i + 1);
            }
//            notifyItemMoved(fromPosition, toPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
        //这种方式在隔position的时候会顺序混乱
        //Collections.swap(dataItemList, fromPos, toPos);

    }

    /**
     * 移除Item 包括数据源和界面的移除
     *
     * @param position 需要被移除Item的position 要包含HeadView的count #getHeadCount()
     */
    public void removeDataItem(int position) {
        dataItemList.remove(position - getHeadCount());
        notifyItemRemoved(position);
    }

    /**
     * 添加头View，并且设置每个Item为full span
     *
     * @param views 头view
     */
    public void addHeadView(View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            addHeadItem(new ItemSimple(view));
        }
    }

    /**
     * 添加头Item，并且设置每个Item为full span
     *
     * @param items 头item
     */
    public void addHeadItem(Item... items) {
        for (Item item : items) {
            item.setIsFullSpan(true);
            headItemList.add(item);
        }
    }

    /**
     * @return head view个数
     */
    public int getHeadCount() {
        return headItemList.size();
    }

    /**
     * @return foot view个数
     */
    public int getFootCount() {
        return footItemList.size();
    }


    /**
     * 添加foot View，并且设置每个Item为full span
     *
     * @param views head view
     */
    public void addFootView(View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            addFootItem(new ItemSimple(view));
        }
    }

    /**
     * 添加尾Item，并且设置每个Item为full span
     *
     * @param items 头item
     */
    public void addFootItem(Item... items) {
        for (Item item : items) {
            item.setIsFullSpan(true);
            footItemList.add(item);
        }
    }

    /**
     * 添加加载更多Item
     *
     * @param itemLoadMore ItemLoadMore
     */
    public void addLoadMoreItem(ItemLoadMore itemLoadMore) {
        this.itemLoadMore = itemLoadMore;
        footItemList.add(itemLoadMore);
    }

    /**
     * 添加加载更多
     *
     * @param listener       OnLoadMoreListener
     * @param isAutoLoadMore 是否开启滑动到底部自动加载更多
     */
    public void addLoadMoreView(OnLoadMoreListener listener, boolean isAutoLoadMore) {
        addLoadMoreItem(new ItemLoadMore(listener, isAutoLoadMore));
    }

    /**
     * 加载数据完成后需要调用此方法更新加载更多视图的状态
     *
     * @param isLoadAll
     */
    public void setLoadComplete(boolean isLoadAll) {
        if (itemLoadMore != null)
            itemLoadMore.setLoadComplete(isLoadAll);
    }

    /**
     * 清空adapter 不只是单纯清空数据源
     */
    public void clearData() {
        dataItemList.clear();
        headItemList.clear();
        footItemList.clear();
        itemLoadMore = null;
        //todo 如果clear后更新item的种类有可能会造成type和item对应混乱
        //[ItemA ItemA] 这个时候ItemA是初始type=1 然后clear了 设置新的[ItemB ItemB]这样ItemB就是初始type=1 就会使用ItemA对应的ViewHolder
        mTypeList.clear();
        mTypeItemList.clear();
        clearParams();
    }

    /**
     * 清空adapter除了Head和Foot 不只是单纯清空数据源
     */
    public void clearParams() {
        animationLoader.clear();
        shrinkViewUtil.clear();
    }

    /**
     * 返回指定位置ItemViewHolder，若不存在返回null
     *
     * @param position int
     * @return 返回指定位置ItemViewHolder，若不存在返回null
     */
    public ItemViewHolder getItemViewHolder(int position) {
        for (ItemViewHolder viewHolder : viewHolderList) {
            if (viewHolder.location == position) {
                return viewHolder;
            }
        }
        return null;
    }

    /**
     * @return 获取当前数据源List，不包含head和foot
     */
    public List<Item> getDataList() {
        return dataItemList;
    }

    /**
     * 设置Item点击Listener
     *
     * @param onItemClickListener OnItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置Item长按Listener
     *
     * @param onItemLongClickListener OnItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * @param position int
     * @return 返回指定位置Item
     */
    public Item getItem(int position) {
        if (position < headItemList.size()) {
            return headItemList.get(position);
        }
        int footPosition = position - getHeadCount() - dataItemList.size();
        if (footPosition >= 0) {
            return footItemList.get(footPosition);
        }
        return dataItemList.get(position - headItemList.size());
    }

    @Override
    public int getItemCount() {
        return dataItemList.size() + getHeadCount() + getFootCount();
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewType的位置对应最新的Item，详情getItemViewType
        Item item = mTypeItemList.get(viewType);
        ItemViewHolder itemViewHolder = item.newItemViewHolder(context, parent);
        View view = itemViewHolder.getItemView();
        RecyclerViewHolder myViewHolder = new RecyclerViewHolder(view);
        myViewHolder.itemViewHolder = itemViewHolder;
        viewHolderList.add(itemViewHolder);
        return myViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(BaseItemAdapter.RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.itemViewHolder.isFullSpan()) {
            setFullSpan(holder);
        }
    }

    /**
     * 设置当前Item展示时是否全行，仅在StaggeredGridLayoutManager用到
	 * todo    GridLayoutManager时没有支持（设置下 spansize） 不行的话只能用StaggeredGridLayoutManager
     */
    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
        } else if(holder.itemView.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ItemViewHolder itemHolder = holder.itemViewHolder;
        holder.itemView.setTag(itemHolder);
        ItemViewHolder.ViewHolderParams params = new ItemViewHolder.ViewHolderParams()
                .setItemLocation(position).setItemCount(getItemCount()).setClickListener(onItemClickListener)
                .setLongClickListener(onItemLongClickListener);
        itemHolder.populateItemView(getItem(position), params);
        animationLoader.addAnimation(itemHolder);
    }


    /**
     * 启动加载动画
     *
     * @param animation BaseAnimation
     */
    public void enableLoadAnimation(BaseAnimation animation) {
        enableLoadAnimation(animation, true);
    }

    /**
     * 启动加载动画
     *
     * @param animation           BaseAnimation
     * @param isShowAnimWhenFirst boolean 是否只有在初次加载的时候才使用动画
     */
    public void enableLoadAnimation(BaseAnimation animation, boolean isShowAnimWhenFirst) {
        animationLoader.enableLoadAnimation(animation, isShowAnimWhenFirst);
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItem(position);
        String typeName = item.getItemViewType();
        int index = mTypeList.indexOf(typeName);
        if (index == -1) {
            index = mTypeList.size();
            mTypeList.add(typeName);
            mTypeItemList.add(item);
        } else {
            mTypeItemList.set(index, item);
        }
        return index;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ItemViewHolder itemViewHolder;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }
}