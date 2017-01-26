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
import free.com.itemlib.item.view.ItemViewGroupHolder;
import free.com.itemlib.item.view.ItemViewHolder;
import free.com.itemlib.item.common.Validate;
import free.com.itemlib.item.view.content.Item;
import free.com.itemlib.item.view.content.ItemGroup;
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
        super(context, ItemEntity.FLAG_INPUT);
    }

    public void clearData() {
        itemInputViewMap.clear();
        itemHiddenInputViewList.clear();
        originSB = new StringBuilder();
        super.clearData();
    }

    @Override
    protected void setData(List<? extends Item> dataList) {
        //防止用户直接在调用端修改dataList后直接调用notifyDataSetChanged()方法去刷新UI
        //目的：作为更新主入口需要初始化以下集合
        super.setData(parseDataList(dataList));
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
        if (!itemInputViewMap.containsKey(position)) {
            initAllInputView();
        }
        convertView = itemInputViewMap.get(position).getItemView();
        return super.getView(position, convertView, parent);
    }


    private void initAllInputView() {
        for (int i = 0; i < getCount(); i++) {
            getViewByLocation(i);
        }
    }

    @Override
    protected View getViewByLocation(int position) {
        View convertView = super.getViewByLocation(position);
        ItemViewHolder tag = (ItemViewHolder) convertView.getTag(Const.ITEM_HOLDER_TAG);
        itemInputViewMap.put(position, tag);
        originSB.append(tag.getValue());
        return convertView;
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


}
