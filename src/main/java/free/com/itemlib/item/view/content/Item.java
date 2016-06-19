package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import free.com.itemlib.item.ItemEntity;
import free.com.itemlib.item.view.ItemViewHolder;


/**
 * Created by free46000 on 2015/5/14 0014.
 */
public interface Item {

    void setTag(Object tag);

    Object getTag();

    void setItemEntity(ItemEntity itemEntity);

    ItemEntity getItemEntity();

    void setShrinkLength(int shrinkLength);

    int getShrinkLength();

    ItemViewHolder newItemViewHolder(Context context);

    ItemViewHolder newItemViewHolder(Context context, ViewGroup parent);

    View newItemView2Show(Context context, ViewGroup parent);

    View newItemView2Show(ItemViewHolder itemViewHolder);

    /**
     * 获取ListView中的ItemView的类型
     * {@linkplain ListItemAdapter#getItemViewType(int)}
     *
     * @return getClass().toString()
     */
    String getItemViewType();

    int getBackResId();

    void setBackResId(int backResId);


    /**
     * 设置此Item是否可以被点击
     */
    void setClickable(boolean clickable);

    boolean isClickable();

    /**
     * 设置此Item是否可以被监听触摸事件
     */
    void setTouchable(boolean touchable);

    boolean isTouchable();


    void setActivated(boolean activated);

    boolean isActivated();

    String getKey();

    void setKey(String key);

    /**
     * 请使用 {@link #setActivated(boolean)}
     *
     * @param selected
     */
    @Deprecated
    void setSelected(boolean selected);

    void setItemParams(ItemLayoutParams itemParams);

    ItemLayoutParams getItemParams();

    public static class ItemLayoutParams {
        public int paddingTop, paddingBottom, paddingLeft, paddingRight;
        public int marginTop, marginBottom, marginLeft, marginRight = -1;


        public void setPadding(int top, int bottom, int left, int right) {
            paddingTop = top;
            paddingBottom = bottom;
            paddingLeft = left;
            paddingRight = right;
        }

        public void setMargin(int top, int bottom, int left, int right) {
            marginTop = top;
            marginBottom = bottom;
            marginLeft = left;
            marginRight = right;
        }
    }

}
