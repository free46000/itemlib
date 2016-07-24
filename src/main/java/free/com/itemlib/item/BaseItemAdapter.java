package free.com.itemlib.item;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.animation.BaseAnimation;
import free.com.itemlib.item.animation.SlideInLeftAnimation;
import free.com.itemlib.item.common.ShrinkViewUtil;
import free.com.itemlib.item.listener.OnItemClickListener;
import free.com.itemlib.item.listener.OnItemLongClickListener;
import free.com.itemlib.item.listener.OnLoadMoreListener;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemSimple;
import free.com.itemlib.item.view.content.ItemLoadMore;

// TODO: 2016/7/5 0005 notifyItemRangeInserted notifyItemInserted notifyItemRangeRemoved...
public class BaseItemAdapter extends RecyclerView.Adapter<BaseItemAdapter.RecyclerViewHolder> {
    protected final List<String> mTypeList = new ArrayList<>();
    protected final List<Item> mTypeItemList = new ArrayList<>();
    protected Context context;
    private List<Item> dataItemList = new ArrayList<>();
    private List<Item> headItemList = new ArrayList<>();
    private List<Item> footItemList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    protected ShrinkViewUtil shrinkViewUtil = new ShrinkViewUtil();

    protected ItemLoadMore itemLoadMore;

    //动画相关配置
    protected int lastAnimIndex = -1;
    protected boolean isAnimEnable;
    protected boolean isShowAnimWhenFirst;
    protected BaseAnimation animation;
    protected long animDuration = 400L;
    protected Interpolator interpolator = new LinearInterpolator();

    public BaseItemAdapter(Context context) {
        this.context = context;
    }

    public void setDataItemList(List<? extends Item> itemList) {
        setData(itemList);
    }

    public void addDataItemList(List<? extends Item> list) {
        addData(list);
    }

    public void addDataItem(Item... item) {
        addData(Arrays.asList(item));
    }

    public void addHeadView(View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            ItemSimple simple = new ItemSimple(views[i]);
            simple.setIsFullSpan(true);
            headItemList.add(simple);
        }
    }

    public int getHeadCount() {
        return headItemList.size();
    }

    public void addFootView(View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            ItemSimple simple = new ItemSimple(views[i]);
            simple.setIsFullSpan(true);
            footItemList.add(simple);
        }
    }

    public void addLoadMoreView(OnLoadMoreListener listener, boolean isAutoLoadMore) {
        itemLoadMore = new ItemLoadMore(listener, isAutoLoadMore);
        footItemList.add(itemLoadMore);
    }

    public void setLoadComplete(boolean isLoadAll) {
        if (itemLoadMore != null)
            itemLoadMore.setLoadComplete(isLoadAll);
    }

    /**
     * 设置Item最终调用方法
     */
    private void setData(List<? extends Item> itemList) {
        clearData();
        dataItemList = (List<Item>) itemList;
        shrinkViewUtil.initShrinkParams(dataItemList);
        notifyDataSetChanged();
    }

    /**
     * 添加Item最终调用方法
     */
    private void addData(List<? extends Item> itemList) {
        dataItemList.addAll(itemList);
        shrinkViewUtil.addShrinkParams(itemList);
        notifyItemRangeInserted(dataItemList.size() - 1 + getHeadCount(), itemList.size());
    }


    public void clearData() {
        dataItemList.clear();
        headItemList.clear();
        footItemList.clear();
        mTypeList.clear();
        mTypeItemList.clear();
        lastAnimIndex = -1;
        itemLoadMore = null;

        shrinkViewUtil.clear();
    }

    public List<Item> getDataList() {
        return dataItemList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public Item getItem(int position) {
        if (position < headItemList.size()) {
            return headItemList.get(position);
        }
        int footPosition = position - headItemList.size() - dataItemList.size();
        if (footPosition >= 0) {
            return footItemList.get(footPosition);
        }
        return dataItemList.get(position - headItemList.size());
    }

    @Override
    public int getItemCount() {
        return dataItemList.size() + headItemList.size() + footItemList.size();
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewType的位置对应最新的Item，详情getItemViewType
        Item item = mTypeItemList.get(viewType);
        ItemViewHolder itemViewHolder = item.newItemViewHolder(context, parent);
        View view = itemViewHolder.getItemView();
        RecyclerViewHolder myViewHolder = new RecyclerViewHolder(view);
        myViewHolder.itemViewHolder = itemViewHolder;
        return myViewHolder;
    }

    @Override
    public void onViewAttachedToWindow(BaseItemAdapter.RecyclerViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.itemViewHolder.isFullSpan()) {
            setFullSpan(holder);
        }
    }

    protected void setFullSpan(RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            params.setFullSpan(true);
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
        addAnimation(itemHolder);
    }

    private void addAnimation(ItemViewHolder holder) {
        if (isAnimEnable) {
            if (!isShowAnimWhenFirst || holder.location > lastAnimIndex) {
                for (Animator anim : animation.getAnimators(holder.getItemView())) {
                    startAnim(anim, holder.location);
                }
                lastAnimIndex = holder.location;
            }
        }
    }

    protected void startAnim(Animator anim, int index) {
        anim.setDuration(animDuration).start();
        anim.setInterpolator(interpolator);
    }

    /**
     * 打开加载动画
     *
     * @param animation BaseAnimation
     */
    public void openLoadAnimation(BaseAnimation animation) {
        openLoadAnimation(animation, true);
    }

    /**
     * 打开加载动画
     *
     * @param animation           BaseAnimation
     * @param isShowAnimWhenFirst boolean 是否只有在初次加载的时候才使用动画
     */
    public void openLoadAnimation(BaseAnimation animation, boolean isShowAnimWhenFirst) {
        this.isAnimEnable = true;
        this.isShowAnimWhenFirst = isShowAnimWhenFirst;
        this.animation = animation == null ? new SlideInLeftAnimation() : animation;
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