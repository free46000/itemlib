package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import free.com.itemlib.item.OnItemClickListener;
import free.com.itemlib.item.OnItemLongClickListener;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemGroup;

public class ItemViewGroupHolder<T extends ItemGroup> extends ItemViewHolder<T> {
    protected List<ItemViewHolder> viewHolderList;

    public ItemViewGroupHolder(Context context, T itemGroup) {
        super(context, itemGroup);
        initItemViewHolderList(itemGroup.getItemList());
    }

    @Override
    protected void initParams() {
        super.initParams();
        int i = 0;
        for (Item item : currItem.getItemList()) {
            viewHolderList.get(i++).initParams();
        }
    }

    public List<ItemViewHolder> getViewHolderList() {
        return viewHolderList;
    }

    private void initItemViewHolderList(List<Item> itemList) {
        if (itemList == null) {
            throw new IllegalArgumentException("ItemViewGroupHolder初始化itemList不能为null");
        }
        viewHolderList = new ArrayList<>();
        for (Item item : itemList) {
            viewHolderList.add(item.newItemViewHolder(context));
        }
    }


    protected boolean isInterceptItemClick() {
        return currItem != null && currItem.isInterceptItemClick();
    }

    protected boolean isInterceptItemChildClick() {
        return currItem != null && currItem.isInterceptItemChildClick();
    }

    @Override
    protected View initItemView() {
        if (viewHolderList.size() == 1) {
            return viewHolderList.get(0).getItemView();
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (ItemViewHolder viewHolder : viewHolderList) {
            linearLayout.addView(viewHolder.getItemView());
        }
        return linearLayout;
    }

    @Override
    public void setData(T itemContent) {
        List<Item> itemList = getItemList(itemContent);
        for (int i = 0; i < itemList.size(); i++) {
            viewHolderList.get(i).setData(itemList.get(i));
        }
    }


    @Override
    public void setShrink(T itemContent) {
        List<Item> itemList = getItemList(itemContent);
        for (int i = 0; i < itemList.size(); i++) {
            viewHolderList.get(i).setShrink(itemList.get(i));
        }
    }

    @Override
    protected void setBackGround(T itemContent, int location) {
        super.setBackGround(itemContent, location);
        List<Item> itemList = getItemList(itemContent);
        for (int i = 0; i < itemList.size(); i++) {
            viewHolderList.get(i).setBackGround(itemList.get(i), location);
        }
    }

    private List<Item> getItemList(T itemContent) {
        List<Item> itemList = null;
        ItemGroup group = (ItemGroup) itemContent;
        itemList = group.getItemList();
        if (viewHolderList.size() != itemList.size()) {
            //"ItemViewGroupHolder中ItemGroup的itemList和viewHolderList不对应"所以返回一个空的List
            itemList.clear();
        }
        return itemList;
    }

    @Override
    protected void setParams(T itemContent, ViewHolderParams params) {
        super.setParams(itemContent, params);
        List<Item> itemList = getItemList(itemContent);
        for (int i = 0; i < itemList.size(); i++) {
            viewHolderList.get(i).setParams((Item) itemList.get(i), params);
        }
    }

    @Override
    public void populateItemViewWhenIdle(T itemContent, ViewHolderParams params) {
        super.populateItemViewWhenIdle(itemContent, params);
        List<Item> itemList = getItemList(itemContent);
        for (int i = 0; i < itemList.size(); i++) {
            viewHolderList.get(i).populateItemViewWhenIdle((Item) itemList.get(i), params);
        }
    }

    @Override
    public void setOnChildClickListener(OnItemClickListener listener) {
        if (isInterceptItemChildClick()) {
            super.setOnChildClickListener(listener);
        } else {
            for (int i = 0; i < viewHolderList.size(); i++) {
                viewHolderList.get(i).setOnChildClickListener(listener);
            }
        }
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (isInterceptItemClick()) {
            super.setOnItemClickListener(listener);
        } else {
            for (int i = 0; i < viewHolderList.size(); i++) {
                viewHolderList.get(i).setOnItemClickListener(listener);
            }
        }
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        if (isInterceptItemClick()) {
            super.setOnItemLongClickListener(listener);
        } else {
            for (int i = 0; i < viewHolderList.size(); i++) {
                viewHolderList.get(i).setOnItemLongClickListener(listener);
            }
        }
    }

    @Override
    public Object getValue() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < viewHolderList.size(); i++) {
            sb.append(viewHolderList.get(i).getValue());
        }
        return sb.toString();
    }

    @Override
    public Map<String, Object> getValueMap() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < viewHolderList.size(); i++) {
            if (viewHolderList.get(i).getValueMap() != null)
                map.putAll(viewHolderList.get(i).getValueMap());
        }
        return map;
    }
}
