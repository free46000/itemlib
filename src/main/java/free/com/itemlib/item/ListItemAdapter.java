package free.com.itemlib.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;

public class ListItemAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    protected final List<String> mTypeList = new ArrayList<>();
    protected Context context;
    private List<Item> dataItemList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    protected int shrinkLenMax = 6;
    protected int currShrinkLen = 0;

    protected int scrollState;

    public ListItemAdapter(Context context) {
        this.context = context;
    }


    public void setDataItemList(List<? extends Item> itemList) {
        setData(itemList);
    }

    public void addDataItem(Item... item) {
        addData(Arrays.asList(item));
    }

    public void addDataItemList(List<? extends Item> list) {
        addData(list);
    }

    public void enableListViewState(ListView listView) {
        listView.setOnScrollListener(this);
    }

    /**
     * 设置ItemList最终调用方法
     */
    protected void setData(List<? extends Item> dataList) {
        this.dataItemList = (List<Item>) dataList;
        initShrinkParams();
    }

    /**
     * 添加Item最终调用方法
     */
    protected void addData(List<? extends Item> itemList) {
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

    public Item getTableItem(int position) {
        return dataItemList.get(position);
    }

    @SuppressWarnings("unused")
    public void clearData() {
        dataItemList.clear();
        currShrinkLen = 0;
    }

    public List<? extends Item> getDataList() {
        return dataItemList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void notifyDataSetChanged(ItemViewHolder itemViewHolder) {
        if (itemViewHolder != null)
            itemViewHolder.refreshView();
    }

    public void notifyDataSetChanged(AbsListView absView, int itemIndex) {
        int firstVisiblePosition = absView.getFirstVisiblePosition();
        int lastVisiblePosition = absView.getLastVisiblePosition();
        if (itemIndex >= firstVisiblePosition && itemIndex <= lastVisiblePosition) {
            View view = absView.getChildAt(itemIndex - firstVisiblePosition);
            int type = getItemViewType(itemIndex);
            if (view.getTag() instanceof ItemViewHolder) {
                ItemViewHolder mViewHolder = (ItemViewHolder) view.getTag();
                notifyDataSetChanged(mViewHolder);
            }
        }

    }

    public void toggleItemActive(Item toggleItem, ItemViewHolder itemViewHolder) {
        toggleItemActive(toggleItem, true, itemViewHolder);
    }

    public void toggleItemActive(Item toggleItem, boolean isSingle, ItemViewHolder itemViewHolder) {
        if (isSingle) {
            for (Item item : dataItemList) {
                item.setActivated(false);
            }
        }
        toggleItem.setActivated(toggleItem.isActivated() ? false : true);

        notifyDataSetChanged(itemViewHolder);
    }

    @Override
    public int getCount() {
        return dataItemList.size();
    }

    @Override
    public Item getItem(int position) {
        return dataItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = getViewByLocation(position);
            }
            populate(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    protected void populate(View view, int location) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) view.getTag();
        ItemViewHolder.ViewHolderParams params = new ItemViewHolder.ViewHolderParams()
                .setItemLocation(location).setItemCount(getCount()).setClickListener(onItemClickListener)
                .setLongClickListener(onItemLongClickListener).setListViewScrollState(scrollState);
        itemViewHolder.populateItemView(getItem(location), params);
    }

    protected View getViewByLocation(int position) {
        ItemViewHolder itemViewHolder = getItem(position).newItemViewHolder(context, null);
        View view = itemViewHolder.getItemView();
        view.setTag(itemViewHolder);
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return dataItemList.get(position).isClickable();
    }

    @Override
    public int getViewTypeCount() {
        return 100;
    }

    @Override
    public int getItemViewType(int position) {
        Item item = getItem(position);
        String typeName = item.getItemViewType();
        int index = mTypeList.indexOf(typeName);
        if (index == -1) {
            index = mTypeList.size();
            mTypeList.add(typeName);
        }
        return index;
    }

    @Override
    public void onScrollStateChanged(AbsListView absView, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {
            for (int i = 0; i < absView.getChildCount(); i++) {
                View view = absView.getChildAt(i);
                if (view.getTag() != null && view.getTag() instanceof ItemViewHolder) {
                    ItemViewHolder itemViewHolder = (ItemViewHolder) view.getTag();
                    int state = itemViewHolder.getParams().getListViewScrollState();
                    if (state == SCROLL_STATE_FLING) {
                        itemViewHolder.populateItemViewWhenIdle(getItem(itemViewHolder.location), itemViewHolder.getParams());
                    }
                    itemViewHolder.getParams().setListViewScrollState(SCROLL_STATE_IDLE);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
