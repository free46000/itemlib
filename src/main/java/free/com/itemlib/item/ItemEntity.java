package free.com.itemlib.item;

import android.content.Context;
import android.view.ViewGroup;


import java.util.List;
import java.util.Map;

import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.common.ReflectUtil;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;

/**
 * Created by Administrator on 2015/5/9 0009.
 */
public abstract class ItemEntity extends ItemBase {
    List<Item> itemList;
    List<Item> itemIntroList;
    List<Item> itemInputList;


    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup viewGroup) {
        return null;
    }

    public List<Item> getItemList(Context context) {
        if (itemList == null) {
            itemList = initItemList(context);
        }
        return itemList;
    }

    public List<Item> getItemIntroList(Context context) {
        if (itemIntroList == null) {
            itemIntroList = initItemIntroList(context);
        }
        return itemIntroList;
    }

    public List<Item> getItemInputList(Context context) {
        if (itemInputList == null) {
            itemInputList = initItemInputList(context);
        }
        return itemInputList;
    }

    protected abstract List<Item> initItemList(Context context);

    protected abstract List<Item> initItemIntroList(Context context);

    protected abstract List<Item> initItemInputList(Context context);

    public void fillSelf(Map<String, Object> dataMap) {
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            fill(entry.getKey(), entry.getValue());
        }
    }

    protected void fill(String key, Object value) {
        ReflectUtil.setValue(key, value, this);
    }

}
