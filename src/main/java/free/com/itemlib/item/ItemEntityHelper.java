package free.com.itemlib.item;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import free.com.itemlib.item.view.content.Item;

/**
 * Created by free46000 on 2016/8/14 0014.
 */
public class ItemEntityHelper {
    private int itemEntityFlag;
    private Context context;

    public ItemEntityHelper(Context context, int itemEntityFlag) {
        this.itemEntityFlag = itemEntityFlag;
        this.context = context;
    }

    public List<? extends Item> getItemList(List<? extends ItemEntity> itemEntityList) {
        List<Item> list = new ArrayList<>();
        for (ItemEntity itemEntity : itemEntityList) {
            list.addAll(getItemList(itemEntity, itemEntityFlag));
        }
        return list;
    }

    public List<Item> getItemList(ItemEntity itemEntity, int itemEntityFlag) {
        List<Item> itemList = new ArrayList<>();
        itemList.addAll(itemEntity.getItemList(context, itemEntityFlag));
        for (Item item : itemList) {
            item.setItemEntity(itemEntity);
        }
        return itemList;
    }


}
