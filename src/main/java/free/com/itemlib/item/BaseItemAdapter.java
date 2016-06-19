package free.com.itemlib.item;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.animation.BaseAnimation;
import free.com.itemlib.item.animation.SlideInLeftAnimation;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;

public class BaseItemAdapter extends RecyclerView.Adapter<BaseItemAdapter.RecyclerViewHolder> {
    protected final List<String> mTypeList = new ArrayList<>();
    protected final List<Item> mTypeItemList = new ArrayList<>();
    protected Context context;
    private List<Item> dataItemList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    protected int shrinkLenMax = 6;
    protected int currShrinkLen = 0;

    //动画相关配置
    protected int lastAnimIndex = -1;
    protected boolean isAnimEnable;
    protected boolean isShowAnimWhenFirst;
    protected BaseAnimation animation;
    protected long animDuration = 600L;
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

    /**
     * 设置Item最终调用方法
     */
    private void setData(List<? extends Item> itemList) {
        clearData();
        dataItemList = (List<Item>) itemList;
        initShrinkParams();
        notifyDataSetChanged();
    }

    /**
     * 添加Item最终调用方法
     */
    private void addData(List<? extends Item> itemList) {
        dataItemList.addAll(itemList);
        addShrinkParams(itemList);
    }

    private void addShrinkParams(List<? extends Item> itemList) {
        for (Item item : itemList) {
            if (currShrinkLen < item.getShrinkLength()) {
                //如果新增加的shrink长度大于之前的，则需要重新初始化
                initShrinkParams();
                return;
            }
        }
        //如果新增加的shrink长度没有大于之前的，则直接为新增加的赋值
        setShrinkForItemContent(itemList);
    }

    private void initShrinkParams() {
        for (Item item : dataItemList) {
            currShrinkLen = Math.max(item.getShrinkLength(), currShrinkLen);
        }
        setShrinkForItemContent(dataItemList);
    }

    private void setShrinkForItemContent(List<? extends Item> dataItemList) {
        if (currShrinkLen <= 0) {
            return;
        }
        currShrinkLen = Math.min(currShrinkLen, shrinkLenMax);
        for (Item item : dataItemList) {
            if (currShrinkLen != item.getShrinkLength()) {
                item.setShrinkLength(currShrinkLen);
            }
        }
    }


    public void clearData() {
        dataItemList.clear();
        mTypeList.clear();
        mTypeItemList.clear();
        currShrinkLen = 0;
        lastAnimIndex = -1;
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
        return dataItemList.get(position);
    }

    @Override
    public int getItemCount() {
        System.out.println("===getItemCount===" + dataItemList.size());
        return dataItemList.size();
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //viewType的位置对应最新的Item，详情getItemViewType
        Item item = mTypeItemList.get(viewType);
        ItemViewHolder itemViewHolder = item.newItemViewHolder(context);
        View view = itemViewHolder.getItemView();
        RecyclerViewHolder myViewHolder = new RecyclerViewHolder(view);
        myViewHolder.itemViewHolder = itemViewHolder;
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ItemViewHolder itemHolder = holder.itemViewHolder;
        Item item = getItem(position);
        if (item.isClickable()) {
            itemHolder.setOnItemClickListener(onItemClickListener);
            itemHolder.setOnItemLongClickListener(onItemLongClickListener);
        } else {
            itemHolder.setOnItemClickListener(null);
            itemHolder.setOnItemLongClickListener(null);
        }
        holder.itemView.setTag(itemHolder);
        ItemViewHolder.ViewHolderParams params = new ItemViewHolder.ViewHolderParams()
                .setItemLocation(position).setItemCount(getItemCount()).setClickListener(onItemClickListener)
                .setLongClickListener(onItemLongClickListener);
        itemHolder.populateItemView(item, params);
        addAnimation(itemHolder);
        System.out.println("===onBindViewHolder===" + holder + position);
    }

    private void addAnimation(ItemViewHolder holder) {
        if (isAnimEnable) {
            if (!isShowAnimWhenFirst || holder.location > lastAnimIndex) {
                if (animation == null) {
                    animation = new SlideInLeftAnimation();
                }
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
     * Set ObjectAnimator
     *
     * @param animation BaseAnimation
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.isAnimEnable = true;
        this.animation = animation;
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
        System.out.println("===getItemViewType===" + index + "===" + position);
        return index;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ItemViewHolder itemViewHolder;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
        }
    }
}