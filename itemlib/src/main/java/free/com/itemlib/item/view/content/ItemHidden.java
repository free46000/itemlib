package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

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
    public View initItemView(Context context, ViewGroup viewGroup) {
        return new View(context);
    }

    @Override
    public void fillData(View itemView) {

    }

    @Override
    public Object getValue(View itemVie) {
        return value;
    }

    public void setValue(CharSequence value) {
        this.value = value;
    }
}
