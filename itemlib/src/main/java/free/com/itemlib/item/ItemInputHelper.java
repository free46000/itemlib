package free.com.itemlib.item;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import free.com.itemlib.item.common.Const;
import free.com.itemlib.item.common.ReflectUtil;
import free.com.itemlib.item.common.Validate;
import free.com.itemlib.item.view.ItemViewGroupHolder;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemHidden;
import free.com.itemlib.item.view.content.ItemInput;

/**
 * Created by free46000 on 2016/8/14 0014.
 */
public class ItemInputHelper {
    /**
     * 必须用LinkedHashMap要保证顺序才能和originSB进行比较 {@link #isValueChanged()}
     */
    protected Map<Item, ItemViewHolder> itemInputViewMap = new LinkedHashMap<>();
    protected List<ItemViewHolder> itemHiddenInputViewList = new ArrayList<>();
    protected StringBuilder originSB = new StringBuilder();

    private Context context;

    public ItemInputHelper(Context context) {
        this.context = context;
    }

    public void clearData() {
        itemInputViewMap.clear();
        itemHiddenInputViewList.clear();
        originSB = new StringBuilder();
    }

    public List<Item> parseDataList(List<? extends Item> dataList) {
        List<Item> newDataList = new ArrayList<>();
        for (Item item : dataList) {
            if (item instanceof ItemHidden) {
                fillHiddenViewList(item);
            } else {
                newDataList.add(item);
                fillInputViewMap(item);
            }
        }
        return newDataList;
    }

    public View getView(Item item) {
        //自己管理输入View，因为输入型View不能被覆盖，最后取值会用到
        return itemInputViewMap.get(item).getItemView();
    }

    protected void fillInputViewMap(Item item) {
        ItemViewHolder itemViewHolder = item.newItemViewHolder(context, null);
        View view = itemViewHolder.getItemView();
        view.setTag(Const.ITEM_HOLDER_TAG, itemViewHolder);
        itemInputViewMap.put(item, itemViewHolder);
        originSB.append(itemViewHolder.getValue());
    }

    protected void fillHiddenViewList(Item item) {
        itemHiddenInputViewList.add(item.newItemViewHolder(context));
    }

    /**
     * 获取全部的ItemViewHolder，包括ItemViewGroupHolder里面的
     */
    private void fillItemViewHolderList(ItemViewHolder viewHolder, List<ItemViewHolder>
            list) {
        if (viewHolder instanceof ItemViewGroupHolder) {
            List<ItemViewHolder> viewHolders = ((ItemViewGroupHolder) viewHolder).getViewHolderList();
            for (int i = 0; i < viewHolders.size(); i++) {
                fillItemViewHolderList(viewHolders.get(i), list);
            }
        }

        list.add(viewHolder);
    }

    public Map<String, Object> getInputValueMap() {
        Map<String, Object> map = new HashMap<>();
        for (ItemViewHolder iiv : itemInputViewMap.values()) {
            Map<String, Object> valueMap = iiv.getValueMap();
            if (!TextUtils.isEmpty(iiv.getKey()) && valueMap != null) {
                map.putAll(valueMap);
            }
        }
        for (ItemViewHolder iiv : itemHiddenInputViewList) {
            map.putAll(iiv.getValueMap());
        }
        return map;
    }

    public boolean isValueChanged() {
        return !originSB.toString().equals(getValueStr());
    }

    public boolean isValueValidate() {
        List<Validate.Rule> ruleList = new ArrayList<>();
        fillValidateList(itemInputViewMap.values(), ruleList);
        return Validate.validateRules(context, ruleList);
    }

    private void fillValidateList(Collection<ItemViewHolder> viewHolders, List<Validate.Rule> ruleList) {

        for (ItemViewHolder iiv : viewHolders) {
            if (iiv instanceof ItemViewGroupHolder) {
                Collection<ItemViewHolder> childHolders = ((ItemViewGroupHolder) iiv).getViewHolderList();
                fillValidateList(childHolders, ruleList);
            }

            if (iiv.getCurrItem() instanceof ItemInput) {
                ItemInput itemInput = (ItemInput) iiv.getCurrItem();
                Validate.Rule rule = itemInput.getRule();
                if (rule != null) {
                    rule.value = rule.value == null ? iiv.getValue().toString() : rule.value;
                    ruleList.add(rule);
                }
            }
        }


    }


    private String getValueStr() {
        StringBuilder sb = new StringBuilder();
        for (ItemViewHolder iiv : itemInputViewMap.values()) {
            sb.append(iiv.getValue());
        }
        return sb.toString();
    }


    public static void fillObject(Map<String, Object> dataMap, Object filledObj) {
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            fill(entry.getKey(), entry.getValue(), filledObj);
        }
    }

    protected static void fill(String key, Object value, Object filledObj) {
        ReflectUtil.setValue(key, value, filledObj);
    }
}
