package free.com.itemlib.item;

import android.content.Context;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemGroup;

public class BaseItemEntityAdapter extends BaseItemAdapter {
    protected ItemEntityHelper itemEntityHelper;

    public BaseItemEntityAdapter(Context context) {
        this(context, ItemEntity.FLAG_DEFAULT);
    }

    public BaseItemEntityAdapter(Context context, @ItemEntity.Flag int entityFlag) {
        super(context);
        itemEntityHelper = new ItemEntityHelper(context, entityFlag);
    }

    public void setDataItemEntityList(List<? extends ItemEntity> itemEntityList) {
        setDataEntity(itemEntityList);
    }

    public void addDataItemEntityList(List<? extends ItemEntity> list) {
        addDataEntity(list);
    }

    public void addDataItemEntity(ItemEntity... entities) {
        addDataEntity(Arrays.asList(entities));
    }

    /**
     * 设置ItemEntity最终调用方法
     */
    protected void setDataEntity(List<? extends ItemEntity> itemEntityList) {
        setDataItemList(itemEntityHelper.getItemList(itemEntityList));
    }

    /**
     * 添加ItemEntity最终调用方法
     */
    protected void addDataEntity(List<? extends ItemEntity> itemEntityList) {
        addDataItemList(itemEntityHelper.getItemList(itemEntityList));
    }


}