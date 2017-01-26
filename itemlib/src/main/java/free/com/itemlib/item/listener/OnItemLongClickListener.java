package free.com.itemlib.item.listener;

import android.view.View;

import free.com.itemlib.item.common.Const;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;


/**
 * @author Administrator
 */
public abstract class OnItemLongClickListener implements View.OnLongClickListener {

    @Override
    public boolean onLongClick(View v) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) v.getTag(Const.ITEM_HOLDER_TAG);
        Item item = itemViewHolder.getCurrItem();
        onItemLongClick(item, itemViewHolder.location);
        onItemLongClick(item, itemViewHolder, itemViewHolder.location, 0);
        return true;
    }

    public abstract void onItemLongClick(Item item, int location);

    public void onItemLongClick(Item item, ItemViewHolder itemViewHolder, int location, int columnLoc) {
    }

}
