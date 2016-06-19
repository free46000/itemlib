package free.com.itemlib.item;

import android.content.Context;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemGroup;

public class ListItemEntityAdapter extends ListItemAdapter {
    private List<ItemEntity> dataItemEntityList = new ArrayList<>();
    public boolean introFlag = false;
    public boolean isUseGroupMode = true;

    public ListItemEntityAdapter(Context context) {
        super(context);
    }

    public void setDataItemEntityList(List<? extends ItemEntity> itemEntityList) {
        setDataEntity(itemEntityList);
    }

    public void setDataItemEntity(ItemEntity... entities) {
        setDataEntity(Arrays.asList(entities));
    }

    public void addDataItemEntityList(List<? extends ItemEntity> list) {
        addDataEntity(list);
    }

    public void addDataItemEntity(ItemEntity... entities) {
        addDataEntity(Arrays.asList(entities));
    }

    @Override
    public void notifyDataSetChanged() {
        if (!dataItemEntityList.isEmpty()) {
            setDataItemList(getItemList(dataItemEntityList));
        }
        super.notifyDataSetChanged();
    }

    /**
     * 设置Item最终调用方法
     */
    private void setDataEntity(List<? extends ItemEntity> itemEntityList) {
        setDataItemList(getItemList(itemEntityList));
        dataItemEntityList = (List<ItemEntity>) itemEntityList;
    }

    /**
     * 添加Item最终调用方法
     */
    private void addDataEntity(List<? extends ItemEntity> itemEntityList) {
        addDataItemList(getItemList(itemEntityList));
        dataItemEntityList.addAll(itemEntityList);
    }

    protected List<? extends Item> getItemList(List<? extends ItemEntity> itemEntityList) {
        List<Item> list = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntityList) {
            list.addAll(getItemList(itemEntity));
        }
        return list;
    }

    protected List<Item> getItemList(ItemEntity itemEntity) {
        List<Item> itemList = new ArrayList<>();
        List<Item> list = getItemOrIntroList(itemEntity);
        if (isUseGroupMode && !(list.size() > 1)) {
            itemList.add(new ItemGroup(list));
        } else {
            itemList.addAll(list);
        }
        for (Item item : itemList) {
            item.setItemEntity(itemEntity);
        }
        return itemList;
    }

    protected List<Item> getItemOrIntroList(ItemEntity itemEntity) {
        if (introFlag) {
            return itemEntity.getItemIntroList(context);
        } else {
            return itemEntity.getItemList(context);
        }
    }

    public List<ItemEntity> getDataEntityList() {
        return dataItemEntityList;
    }

    public int getCount_ItemEntity() {
        return dataItemEntityList.size();
    }

}