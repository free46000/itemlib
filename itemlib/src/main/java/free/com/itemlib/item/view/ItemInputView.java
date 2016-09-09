package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemInput;


/**
 * Created by free46000 on 2015/5/18 0018.
 */
public class ItemInputView<T extends ItemInput> extends ItemBaseView<T> {

    public ItemInputView(Context context, T item, ViewGroup viewGroup) {
        super(context, item, viewGroup);
    }

    @Deprecated
    @Override
    /**
     * 输入View不需要setData
     */
    public void setData(T itemContent) {
    }

    @Override
    public Object getValue() {
        return currItem.getValue(itemView);
    }

    @Override
    public Map<String, Object> getValueMap() {
        Map<String, Object> valueMap = currItem.getValueMap(itemView);

        return valueMap == null ? super.getValueMap() : valueMap;
    }
}
