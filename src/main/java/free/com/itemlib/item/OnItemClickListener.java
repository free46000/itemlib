package free.com.itemlib.item;

import android.util.Log;
import android.view.View;

import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;


/**
 * @author Administrator
 */
public abstract class OnItemClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null || !(tag instanceof ItemViewHolder)) {
            Log.e("OnItemClickListener", "OnItemClickListener点击监听通过view的tag没获取到ItemViewHolder，所以不予执行OnClick");
            return;
        }

        ItemViewHolder itemViewHolder = (ItemViewHolder) tag;
        Item item = itemViewHolder.getCurrItem();
        if (itemViewHolder.getItemHandlerView().getId() == v.getId()) {
            onItemClick(itemViewHolder, item);
        } else {
            onChildClick(v, itemViewHolder, item);
        }
    }

    private void onItemClick(ItemViewHolder itemViewHolder, Item item) {
        onItemClick(item, itemViewHolder.location);
        onItemClick(item, itemViewHolder, itemViewHolder.location);
    }

    private void onChildClick(View view, ItemViewHolder itemViewHolder, Item item) {
        onChildClick(view, item, itemViewHolder.location);
        onChildClick(view, item, itemViewHolder, itemViewHolder.location);
    }

    public abstract void onItemClick(Item item, int location);

    public void onItemClick(Item item, ItemViewHolder itemViewHolder, int location) {
    }

    public void onChildClick(View view, Item item, int location) {

    }

    public void onChildClick(View view, Item item, ItemViewHolder itemViewHolder, int location) {
    }

}
