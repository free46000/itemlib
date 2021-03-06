package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import free.com.itemlib.item.ItemEntity;
import free.com.itemlib.item.view.ItemViewHolder;


/**
 * Created by ZX on 2015/9/23.
 */
public abstract class ItemImpl implements Item {
    protected ItemEntity itemEntity;
    protected Object tag;
    protected String key;
    protected boolean clickable = true;
    protected boolean touchable = false;
    protected boolean isFullSpan = false;
    protected boolean isShowDecoration = true;
    protected int backResId = -1;
    protected boolean activated;
    protected int shrinkLength;
    protected ItemLayoutParams itemParams;


    public ItemImpl() {
        this(null);
    }

    public ItemImpl(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    @Override
    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    @Override
    public Object getTag() {
        return tag;
    }

    @Override
    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public boolean isFullSpan() {
        return isFullSpan;
    }

    @Override
    public void setIsFullSpan(boolean isFullSpan) {
        this.isFullSpan = isFullSpan;
    }

    @Override
    public boolean isShowDecoration() {
        return isShowDecoration;
    }

    @Override
    public void setShowDecoration(boolean isShowDecoration) {
        this.isShowDecoration = isShowDecoration;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public boolean isTouchable() {
        return touchable;
    }

    @Override
    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    @Override
    public int getBackResId() {
        return backResId;
    }

    @Override
    public void setBackResId(int backResId) {
        this.backResId = backResId;
    }

    @Override
    public boolean isActivated() {
        return activated;
    }

    @Override
    public void setSelected(boolean selected) {
    }

    @Override
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getShrinkLength() {
        return 0;
    }

    @Override
    public void setShrinkLength(int shrinkLength) {
        this.shrinkLength = shrinkLength;
    }

    @Override
    public final ItemViewHolder newItemViewHolder(Context context) {
        return newItemViewHolder(context, null);
    }

    @Override
    public abstract ItemViewHolder newItemViewHolder(Context context, ViewGroup parent);

    @Override
    public View newItemView2Show(Context context, ViewGroup parent) {
        ItemViewHolder itemViewHolder = newItemViewHolder(context, parent);
        return newItemView2Show(itemViewHolder);
    }

    @Override
    public View newItemView2Show(ItemViewHolder itemViewHolder) {
        View v = itemViewHolder.getItemView();
        itemViewHolder.populateItemView(this, new ItemViewHolder.ViewHolderParams());
        return v;
    }

    @Override
    public String getItemViewType() {
        return this.getClass().toString();
    }


    @Override
    public void setItemParams(ItemLayoutParams itemParams) {
        this.itemParams = itemParams;
    }

    @Override
    public ItemLayoutParams getItemParams() {
        return itemParams;
    }
}
