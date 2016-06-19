package free.com.itemlib.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.common.Validate;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemHidden;
import free.com.itemlib.item.view.content.ItemInput;

/**
 * Created by free46000 on 2015/6/4
 * <p/>
 * 注意使用此类的Activity需在Manifest中配置android:windowSoftInputMode="adjustPan"
 */
public class ListItemInputAdapter extends ListItemEntityAdapter {
    /**
     * 必须用LinkedHashMap要保证顺序才能和originSB进行比较 {@link #isValueChanged()}
     */
    protected Map<Integer, ItemViewHolder> itemInputViewMap = new LinkedHashMap<>();
    protected List<ItemViewHolder> itemHiddenInputViewList = new ArrayList<>();
    protected StringBuilder originSB = new StringBuilder();

    public ListItemInputAdapter(Context context) {
        super(context);
        isUseGroupMode = false;
    }

    @Override
    protected List<Item> getItemList(ItemEntity itemEntity) {
        return itemEntity.getItemInputList(context);
    }

    @Override
    protected void setData(List<? extends Item> dataList) {
        //防止用户直接在调用端修改dataList后直接调用notifyDataSetChanged()方法去刷新UI
        //目的：作为更新主入口需要初始化以下集合
        itemInputViewMap = new LinkedHashMap<>();
        itemHiddenInputViewList = new ArrayList<>();
        List<Item> newDataList = parseDataList(dataList);
        originSB = new StringBuilder();
        super.setData(newDataList);
    }

    private List<Item> parseDataList(List<? extends Item> dataList) {
        List<Item> newDataList = new ArrayList<>();
        for (Item item : dataList) {
            if (item instanceof ItemHidden) {
                itemHiddenInputViewList.add(item.newItemViewHolder(context));
            } else {
                newDataList.add(item);
            }
        }
        return newDataList;
    }

    @Override
    protected void addData(List<? extends Item> itemList) {
        super.addData(parseDataList(itemList));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //自己管理输入View，因为输入型View不能被覆盖，最后取值会用到
        if (itemInputViewMap.containsKey(position)) {
            convertView = itemInputViewMap.get(position).getItemView();
        } else {
            convertView = getViewByLocation(position);
            ItemViewHolder tag = (ItemViewHolder) convertView.getTag();
            itemInputViewMap.put(position, tag);
            originSB.append(tag.getValue());
        }
        return super.getView(position, convertView, parent);
    }

    public Map<String, Object> getInputValueMap() {
        Map<String, Object> map = new HashMap<>();
        for (ItemViewHolder iiv : itemInputViewMap.values()) {
            if (iiv.getKey() != null && !iiv.getKey().isEmpty()) {
                map.put(iiv.getKey(), iiv.getValue());
            }
        }
        for (ItemViewHolder iiv : itemHiddenInputViewList) {
            map.put(iiv.getKey(), iiv.getValue());
        }
        return map;
    }

    public boolean isValueChanged() {
        return !originSB.toString().equals(getValueStr());
    }

    public boolean isValueValidate() {
        List<Validate.Rule> ruleList = new ArrayList<>();
        for (ItemViewHolder iiv : itemInputViewMap.values()) {
            if (iiv.getCurrItem() instanceof ItemInput) {
                ItemInput itemInput = (ItemInput) iiv.getCurrItem();
                Validate.Rule rule = itemInput.getRule();
                rule.value = iiv.getValue().toString();
                if (rule != null)
                    ruleList.add(itemInput.getRule());
            }
        }
        return Validate.validateRules(context, ruleList);
    }


    private String getValueStr() {
        StringBuilder sb = new StringBuilder();
        for (ItemViewHolder iiv : itemInputViewMap.values()) {
            sb.append(iiv.getValue());
        }
        return sb.toString();
    }


}
