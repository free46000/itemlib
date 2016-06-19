package free.com.itemlib.item.view;

import android.content.Context;
import android.view.View;

import free.com.itemlib.item.view.content.Item;


/**
 * Created by free46000 on 2015/5/18 0018.
 */
public abstract class ItemInputView<T extends Item> extends ItemViewHolder<T> {
    protected T item;

    public ItemInputView(Context context, T item) {
        super(context, item);
        this.item = item;
    }

    @Override
    protected View initItemView() {
        return initItemView(item);
    }

    protected abstract View initItemView(T item);

    @Deprecated
    @Override
    /**
     * 输入View不需要setData
     */
    public void setData(T itemContent) {
    }

    @Override
    public abstract Object getValue();

    @Override
    public String getKey() {
        return item.getKey();
    }

}
