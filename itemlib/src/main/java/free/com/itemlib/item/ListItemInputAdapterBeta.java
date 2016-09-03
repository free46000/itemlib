package free.com.itemlib.item;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import free.com.itemlib.item.view.content.Item;

/**
 * Created by free46000 on 2015/6/4
 * <p/>
 * 注意使用此类的Activity需在Manifest中配置android:windowSoftInputMode="adjustPan"
 */
public class ListItemInputAdapterBeta extends ListItemEntityAdapter {
    protected ItemInputHelper itemInputHelper;

    public ListItemInputAdapterBeta(Context context) {
        super(context, ItemEntity.FLAG_INPUT);
        itemInputHelper = new ItemInputHelper(context);
    }


    @Override
    public void clearData() {
        itemInputHelper.clearData();
        super.clearData();
    }

    @Override
    protected void setData(List<? extends Item> dataList) {
        super.setData(itemInputHelper.parseDataList(dataList));
    }

    @Override
    protected void addData(List<? extends Item> itemList) {
        super.addData(itemInputHelper.parseDataList(itemList));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = itemInputHelper.getView(getItem(position));
        return super.getView(position, convertView, parent);
    }

    public Map<String, Object> getInputValueMap() {
        return itemInputHelper.getInputValueMap();
    }

    public boolean isValueChanged() {
        return itemInputHelper.isValueChanged();
    }

    public boolean isValueValidate() {
        return itemInputHelper.isValueValidate();
    }
}
