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

import free.com.itemlib.item.common.Const;
import free.com.itemlib.item.common.ShrinkViewUtil;
import free.com.itemlib.item.listener.OnItemClickListener;
import free.com.itemlib.item.listener.OnItemLongClickListener;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;

public class ListItemAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    protected final List<String> mTypeList = new ArrayList<>();
    protected Context context;
    private List<Item> dataItemList = new ArrayList<>();
    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;

    protected ShrinkViewUtil shrinkViewUtil = new ShrinkViewUtil();

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
        clearData();
        this.dataItemList = (List<Item>) dataList;
        shrinkViewUtil.initShrinkParams(dataItemList);
    }

    /**
     * 添加Item最终调用方法
     */
    protected void addData(List<? extends Item> itemList) {
        dataItemList.addAll(itemList);
        shrinkViewUtil.addShrinkParams(itemList);
    }

    public Item getTableItem(int position) {
        return dataItemList.get(position);
    }

    public void clearData() {
        dataItemList.clear();
        shrinkViewUtil.clear();
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
            if (view.getTag(Const.ITEM_HOLDER_TAG) instanceof ItemViewHolder) {
                ItemViewHolder mViewHolder = (ItemViewHolder) view.getTag(Const.ITEM_HOLDER_TAG);
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) view.getTag(Const.ITEM_HOLDER_TAG);
        ItemViewHolder.ViewHolderParams params = new ItemViewHolder.ViewHolderParams()
                .setItemLocation(location).setItemCount(getCount()).setClickListener(onItemClickListener)
                .setLongClickListener(onItemLongClickListener).setListViewScrollState(scrollState);
        itemViewHolder.populateItemView(getItem(location), params);
    }

    protected View getViewByLocation(int position) {
        ItemViewHolder itemViewHolder = getItem(position).newItemViewHolder(context, null);
        View view = itemViewHolder.getItemView();
        view.setTag(Const.ITEM_HOLDER_TAG, itemViewHolder);
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
                if (view.getTag(Const.ITEM_HOLDER_TAG) != null && view.getTag(Const.ITEM_HOLDER_TAG) instanceof ItemViewHolder) {
                    ItemViewHolder itemViewHolder = (ItemViewHolder) view.getTag(Const.ITEM_HOLDER_TAG);
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
