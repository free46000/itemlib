package free.com.itemlib.item;

import android.content.Context;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemGroup;

public class BaseItemEntityAdapter extends BaseItemAdapter {
    //    private List<ItemEntity> dataItemEntityList = new ArrayList<>();
    public boolean introFlag = false;

    public BaseItemEntityAdapter(Context context) {
        super(context);
    }

    public void setDataItemEntityList(List<? extends ItemEntity> itemEntityList) {
        setDataEntity(itemEntityList);
    }

    public void addDataItemEntityList(List<? extends ItemEntity> list) {
        addDataEntity(list);
    }

    public void addDataItemEntity(ItemEntity... item) {
        addDataEntity(Arrays.asList(item));
    }

    /**
     * 设置Item最终调用方法
     */
    private void setDataEntity(List<? extends ItemEntity> itemEntityList) {
        setDataItemList(getItemGroupList(itemEntityList));
    }

    /**
     * 添加Item最终调用方法
     */
    private void addDataEntity(List<? extends ItemEntity> itemEntityList) {
        addDataItemList(getItemGroupList(itemEntityList));
    }

    private List<ItemGroup> getItemGroupList(List<? extends ItemEntity> itemEntityList) {
        List<ItemGroup> itemGroupList = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntityList) {
            //为ItemGroup赋值ItemEntity方便以后OnClick等其他地方用到
            itemGroupList.add(new ItemGroup(itemEntity, getItemList(itemEntity)));
        }
        return itemGroupList;
    }

    private List<Item> getItemList(ItemEntity itemEntity) {
        List<Item> itemList;
        if (introFlag) {
            itemList = itemEntity.getItemIntroList(context);
        } else {
            itemList = itemEntity.getItemList(context);
        }
        return itemList;
    }

//    public List<ItemEntity> getDataList() {
//        return dataItemEntityList;
//    }

}