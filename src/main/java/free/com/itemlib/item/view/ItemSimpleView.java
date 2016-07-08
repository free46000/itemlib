package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;

import free.com.itemlib.item.view.content.ItemSimple;

/**
 * Created by wzx on 2016/7/5.
 */
public class ItemSimpleView extends ItemViewHolder<ItemSimple> {

    public ItemSimpleView(Context context, ItemSimple item, View view) {
        super(context, item);
        itemView = view;
    }

    @Override
    protected View initItemView() {
        return itemView;
    }

    @Override
    public void setData(ItemSimple itemContent) {

    }
}
