package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import free.com.itemlib.item.view.content.ItemHidden;


/**
 * Created by free46000 on 2015/6/16 0016.
 */
public class ItemHiddenView extends ItemInputView<ItemHidden> {


    public ItemHiddenView(Context context, ItemHidden item) {
        super(context, item);
    }

    @Override
    protected View initItemView(ItemHidden item) {
        TextView v = new TextView(context);
        v.setWidth(0);
        v.setHeight(0);
//        v.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        v.setVisibility(View.GONE);
        return v;
    }


    @Override
    public Object getValue() {
        return item.getValue();
    }
}
