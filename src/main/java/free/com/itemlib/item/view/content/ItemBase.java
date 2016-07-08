package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import free.com.itemlib.item.view.ItemBaseView;
import free.com.itemlib.item.view.ItemSimpleView;
import free.com.itemlib.item.view.ItemViewHolder;

/**
 * Created by wzx on 2016/7/5.
 */
public abstract class ItemBase extends ItemImpl {
    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        return new ItemBaseView(context, this);
    }

    public abstract View initItemView(Context context);

    public abstract void fillData(View itemView);

}
