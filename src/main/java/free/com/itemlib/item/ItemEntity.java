package free.com.itemlib.item;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.ViewGroup;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Map;

import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.common.ReflectUtil;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemImpl;

/**
 * Created by free46000 on 2015/5/9 0009.
 */
public interface ItemEntity {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FLAG_DEFAULT, FLAG_INTRO, FLAG_INPUT})
    @interface Flag {
    }

    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_INTRO = 1;
    public static final int FLAG_INPUT = 2;


    List<Item> getItemList(Context context,@Flag int flag);
}
