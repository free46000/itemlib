package free.com.itemlib.item.view.content;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import free.com.itemlib.item.view.ItemInputView;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.common.Validate;

/**
 * Created by free46000 on 2015/5/18 0018.
 */
public abstract class ItemInput extends ItemBase {
    protected String viewTypeStr;
    protected Validate.Rule rule;

    /**
     * @param key 本录入View的值对应的存储key
     */
    public ItemInput(String key) {
        this.key = key;
    }

    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        return new ItemInputView<>(context, this, parent);
    }

    public Validate.Rule getRule() {
        return rule;
    }

    public void setRule(Validate.Rule rule) {
        this.rule = rule;
    }

    @Override
    public void fillData(ItemViewHolder itemViewHolder) {

    }

    public abstract Object getValue(View itemView);

    public Map<String, Object> getValueMap(View itemView) {
        return null;
    }

    @Override
    public boolean isClickable() {
        return false;
    }
}
