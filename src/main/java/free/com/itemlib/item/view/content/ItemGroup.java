package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;



import java.util.List;

import free.com.itemlib.item.ItemEntity;
import free.com.itemlib.item.view.ItemViewGroupHolder;
import free.com.itemlib.item.view.ItemViewHolder;

/**
 * Created by free46000 on 2015/5/14 0014.
 */
public class ItemGroup extends ItemBase {
    private List<Item> itemList;
    protected boolean isInterceptItemClick = true;
    protected boolean isInterceptItemChildClick = false;

    public ItemGroup() {
        this(null, null);
    }

    public ItemGroup(List<Item> itemList) {
        this(null, itemList);
    }

    public ItemGroup(ItemEntity itemEntity, List<Item> itemList) {
        super(itemEntity);
        this.itemList = itemList;
    }

    public void setIsInterceptItemClick(boolean isInterceptItemClick) {
        this.isInterceptItemClick = isInterceptItemClick;
    }

    public void setIsInterceptItemChildClick(boolean isInterceptItemChildClick) {
        this.isInterceptItemChildClick = isInterceptItemChildClick;
    }

    public boolean isInterceptItemClick() {
        return isInterceptItemClick;
    }

    public boolean isInterceptItemChildClick() {
        return isInterceptItemChildClick;
    }


    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        return new ItemViewGroupHolder(context, this);
    }

    @Override
    public String getItemViewType() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        for (Item item : getItemList()) {
            sb.append(item.getItemViewType());
        }
        return sb.toString();
    }

    @Override
    public void setShrinkLength(int shrinkLength) {
        for (Item item : getItemList()) {
            item.setShrinkLength(shrinkLength);
        }
    }

    @Override
    public int getShrinkLength() {
        if (shrinkLength == 0) {
            for (Item item : getItemList()) {
                shrinkLength = Math.max(shrinkLength, item.getShrinkLength());
            }
        }
        return shrinkLength;
    }

    public List<Item> getItemList() {
        if (itemList == null) {
            throw new IllegalArgumentException("ItemGroup里面itemList不能为null");
        }
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public void setItemEntity(ItemEntity itemEntity) {
        super.setItemEntity(itemEntity);
        for (Item item : getItemList()) {
            item.setItemEntity(itemEntity);
        }
    }

    //以下都为不需要覆写的方法，先记录下来，以免以后忘记
    @Override
    public View newItemView2Show(Context context, ViewGroup parent) {
        return super.newItemView2Show(context, parent);
    }

    @Override
    public View newItemView2Show(ItemViewHolder itemViewHolder) {
        return super.newItemView2Show(itemViewHolder);
    }

    @Override
    public int getBackResId() {
        return super.getBackResId();
    }

    @Override
    public boolean isActivated() {
        return super.isActivated();
    }

    @Override
    public void setActivated(boolean activated) {
        super.setActivated(activated);
    }

    @Override
    public void setBackResId(int backResId) {
        super.setBackResId(backResId);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
    }
}
