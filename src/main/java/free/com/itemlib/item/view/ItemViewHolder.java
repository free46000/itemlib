package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import free.com.itemlib.R;
import free.com.itemlib.item.OnItemClickListener;
import free.com.itemlib.item.OnItemLongClickListener;
import free.com.itemlib.item.view.content.Item;


/**
 * Created by free46000 on 2015/5/9 0009.
 */
public abstract class ItemViewHolder<T extends Item> {
    /**
     * 标示当前TableItemView所在位置
     */
    public int location;
    public static final int[] AutoBackResIdArr = new int[]
            {R.drawable.item_auto_back1_sel, R.drawable.item_auto_back2_sel};

    protected Context context;
    protected ViewGroup parentViewGroup;
    /**
     * item的真实View，如layout布局生成布局
     */
    protected View itemView;
    /**
     * 需要操作的itemView，有的时候可能为了效果在最外层增加一层父布局
     */
    protected View itemHandlerView;
    protected T currItem;
    protected ViewHolderParams params;

    public ItemViewHolder(Context context, T item) {
        this(context, item, null);
    }

    public ItemViewHolder(Context context, T item, ViewGroup parentViewGroup) {
        this.context = context;
        this.parentViewGroup = parentViewGroup;
        this.currItem = item;
    }

    protected void initParams() {
        initLayoutParams(currItem);
    }

    protected void initLayoutParams(T itemContent) {
        if (itemContent == null) {
            return;
        }
        Item.ItemLayoutParams lp = itemContent.getItemParams();
        if (lp != null) {
            itemView.setPadding(lp.paddingLeft, lp.paddingTop, lp.paddingRight, lp.paddingBottom);
        }
    }

    public ViewHolderParams getParams() {
        return params;
    }

    public void refreshView() {
        if (currItem != null)
            populateItemView(currItem, params);
    }

    protected void setActivated(T item) {
        if (itemHandlerView != null) {
            itemHandlerView.setActivated(item.isActivated());
        }
    }

    public void setActivated(boolean isActive) {
        currItem.setActivated(isActive);
        setActivated(currItem);
    }

    public boolean isFullSpan() {
        return currItem.isFullSpan();
    }

    //    * 设置背景根据 @link #getBackResID()返回值做逻辑处理并给tableItem赋值

    /**
     * 设置位置标示 {@link ItemViewHolder#location} <br>
     * 设置当前内容Item {@link ItemViewHolder#currItem} <br>
     * 为TableItem设置内容{@link ItemViewHolder#setData(Item)}; <br>
     * 设置缩放列宽度{@link ItemViewHolder#setShrink(Item)}; <br>
     * 设置背景根据 {@link #getBackResID()}返回值做逻辑处理并给itemContent赋值
     * 设置selected {@link Item#isActivated()}
     */
    public void populateItemView(T itemContent, ViewHolderParams params) {
        setParams(itemContent, params);

        setData(itemContent);
        if (itemContent.getShrinkLength() > 0) {
            setShrink(itemContent);
        }

        setBackGround(itemContent, location);

        setActivated(itemContent);

        if (itemContent.isClickable()) {
            setOnItemClickListener(params.clickListener);
            setOnItemLongClickListener(params.longClickListener);
        }
//        else {
//            setOnItemClickListener(null);
//            setOnItemLongClickListener(null);
//        }

//        System.out.println("populateItemView:" + params.listViewScrollState + "==" + params.itemLocation);

    }

    public void populateItemViewWhenIdle(T itemContent, ViewHolderParams params) {
//        System.out.println("populateItemViewWhenIdle:" + params.listViewScrollState + "==" + params.itemLocation);
    }

    protected void setParams(T itemContent, ViewHolderParams params) {
        this.location = params.itemLocation;
        this.currItem = itemContent;
        this.params = params;
    }

    /**
     * 1.首先判断tableItem中是否含有背景ID 2.根据 {@link #getBackResID()} R.bool.isAutoBack
     * 返回值做逻辑处理并给tableItem赋值
     */
    protected void setBackGround(T item, int location) {
        if (item.getBackResId() == 0) {
            int bacResId = getBackResID();
            if (bacResId == -1) {
                if (context.getResources().getBoolean(R.bool.isAutoBack)) {
                    item.setBackResId(AutoBackResIdArr[location % AutoBackResIdArr.length]);
                } else {
                    item.setBackResId(-1);
                }
            } else if (bacResId > 0) {
                item.setBackResId(bacResId);
            } else {
                return;
            }
        }
        setBackGround(item.getBackResId());

    }

    /**
     * 设置背景
     *
     * @param resId 大于0时才有效
     */
    protected void setBackGround(int resId) {
        if (resId > 0)
            itemHandlerView.setBackgroundResource(resId);
    }

    /**
     * -1:自动化变颜色背景；0：不设置背景；大于0为需要设置的背景色<br>
     * 需注意若{@link Item#getBackResId()}中含有值以此值为准,
     */
    protected int getBackResID() {
        return 0;
    }


    /**
     * @return 当前展示内容Item
     */
    public T getCurrItem() {
        return currItem;
    }

    /**
     * 注册一个回调函数，当点击时调用
     *
     * @param listener 监听
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (itemHandlerView != null) {
            itemHandlerView.setTag(this);
            itemHandlerView.setOnClickListener(listener);
            setOnChildClickListener(listener);
        }
    }

    /**
     * 注册一个回调函数，当点击子view时调用
     *
     * @param listener 监听
     */
    public void setOnChildClickListener(OnItemClickListener listener) {
    }

    /**
     * 注册一个回调函数，当长按时调用
     *
     * @param listener 监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (itemHandlerView != null)
            itemHandlerView.setOnLongClickListener(listener);
    }


    /**
     * @return 获取需要展示的ItemView
     */
    public View getItemView() {
        if (itemView == null) {
            itemView = initItemView();
            itemHandlerView = getItemHandlerView();
            initParams();
        }
        return itemView;
    }

    /**
     *
     * {@link #getItemView()}
     * @return itemView
     */
    protected abstract View initItemView();

    /**
     * 获取真实操作的item的view
     */
    public View getItemHandlerView() {
        return itemView;
    }

    /**
     * 为Item设置内容;<br/>
     * 建议使用 {@link ItemViewHolder#populateItemView}
     *
     * @param itemContent 内容Item
     */
    public abstract void setData(T itemContent);

    /**
     * 设置缩放列宽度 <br/>
     * 注意：当 {@link Item#getShrinkLength()} 返回值大于0时才会被调用
     *
     * @param itemContent 内容Item
     */
    public void setShrink(T itemContent) {
    }

    /**
     * 获取当前ViewHolder的对应Value
     *
     * @return
     */
    public Object getValue() {
        return null;
    }

    /**
     * 获取当前ViewHolder的对应Value Map,key为{@link #getKey} ,value为{@link #getValue},
     * 主要提供给InputAdapter使用
     *
     * @return
     */
    public Map<String, Object> getValueMap() {
        if (getKey() == null || getValue() == null) {
            return null;
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put(getKey(), getValue());
        return map;
    }

    /**
     * 获取Item的key，主要作用于ItemInput模式中对应value
     *
     * @return key
     */
    public String getKey() {
        return currItem == null ? null : currItem.getKey();
    }

    public static class ViewHolderParams {
        public OnItemClickListener clickListener;
        public OnItemLongClickListener longClickListener;
        public int itemLocation;
        public int itemCount;
        public int listViewScrollState;

        public ViewHolderParams setClickListener(OnItemClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public ViewHolderParams setLongClickListener(OnItemLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
            return this;
        }

        public ViewHolderParams setItemLocation(int itemLocation) {
            this.itemLocation = itemLocation;
            return this;
        }

        public ViewHolderParams setItemCount(int itemCount) {
            this.itemCount = itemCount;
            return this;
        }

        public ViewHolderParams setListViewScrollState(int listViewScrollState) {
            this.listViewScrollState = listViewScrollState;
            return this;
        }

        public int getListViewScrollState() {
            return listViewScrollState;
        }

    }
}