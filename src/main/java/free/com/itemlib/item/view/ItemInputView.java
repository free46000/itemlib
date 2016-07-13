package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;

import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemInput;


/**
 * Created by free46000 on 2015/5/18 0018.
 */
public class ItemInputView extends ItemBaseView<ItemInput> {

    public ItemInputView(Context context, ItemInput item) {
        super(context, item);
    }

    @Deprecated
    @Override
    /**
     * 输入View不需要setData
     */
    public void setData(ItemInput itemContent) {
    }

    @Override
    public Object getValue() {
        return currItem.getValue(itemView);
    }

    @Override
    public String getKey() {
        return currItem.getKey();
    }

}
