package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;

import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemSimple;

/**
 * Created by wzx on 2016/7/5.
 */
public class ItemBaseView extends ItemViewHolder<ItemBase> {

    public ItemBaseView(Context context, ItemBase item) {
        super(context, item);
    }

    @Override
    protected View initItemView() {
        return currItem.initItemView(context);
    }

    @Override
    public void setData(ItemBase itemBase) {
        itemBase.fillData(itemView);
    }
}
