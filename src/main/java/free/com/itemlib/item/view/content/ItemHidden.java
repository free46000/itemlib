package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.ViewGroup;

import free.com.itemlib.item.view.ItemHiddenView;
import free.com.itemlib.item.view.ItemViewHolder;

/**
 * Created by free46000 on 2015/6/16 0016.
 */
public class ItemHidden extends ItemInput {
    protected CharSequence value;


    /**
     * @param key 本录入View的值对应的存储key
     */
    public ItemHidden(String key, CharSequence value) {
        super(key);
        this.value = value;
    }

    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        return new ItemHiddenView(context, this);
    }

    public CharSequence getValue() {
        return value;
    }

    public void setValue(CharSequence value) {
        this.value = value;
    }
}
