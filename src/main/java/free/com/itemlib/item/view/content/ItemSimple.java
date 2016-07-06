package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import free.com.itemlib.item.view.ItemSimpleView;
import free.com.itemlib.item.view.ItemViewHolder;

/**
 * Created by wzx on 2016/7/5.
 */
public class ItemSimple extends ItemBase {
    private View itemView;
    private int itemViewLayoutId;

    public ItemSimple(View itemView) {
        this.itemView = itemView;
    }

//    public ItemSimple(int itemViewLayoutId) {
//        this.itemViewLayoutId = itemViewLayoutId;
//    }

    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {

        return new ItemSimpleView(context, this, itemView);
    }

    @Override
    public String getItemViewType() {
        return super.getItemViewType() + itemView == null ? itemViewLayoutId + "" : itemView.toString();
    }
}
