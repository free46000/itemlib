package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import free.com.itemlib.item.view.content.ItemBase;
import free.com.itemlib.item.view.content.ItemSimple;

/**
 * Created by wzx on 2016/7/5.
 */
public class ItemBaseView<T extends ItemBase> extends ItemViewHolder<T> {

    public ItemBaseView(Context context, T item, ViewGroup viewGroup) {
        super(context, item, viewGroup);
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
