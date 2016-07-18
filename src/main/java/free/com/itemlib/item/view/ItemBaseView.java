package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;

import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemSimple;

/**
 * Created by wzx on 2016/7/5.
 */
public class ItemBaseView<T extends ItemBase> extends ItemViewHolder<T> {

    public ItemBaseView(Context context, T item) {
        super(context, item);
    }

    @Override
    protected View initItemView() {
        return currItem.initItemView(context, parentViewGroup);
    }

    @Override
    public void setData(T itemBase) {
        itemBase.fillData(itemView);
    }
}
