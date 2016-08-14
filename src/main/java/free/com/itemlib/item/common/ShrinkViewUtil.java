package free.com.itemlib.item.common;

import android.widget.TextView;

import java.util.List;

import free.com.itemlib.item.view.content.Item;

/**
 * 缩进相关的工具类，对Item进行计算后，并重新统一设置合适的缩进数值
 * 主要是name-value的样式左侧对其比较好看<br/>
 * 静态方法主要是对具体view如何缩进的实现
 */
public class ShrinkViewUtil {
    protected int shrinkLenMax = 6;
    protected int currShrinkLen = 0;

    public void clear() {
        currShrinkLen = 0;
    }

    /**
     * 为新增的itemList计算缩进大小
     *
     * @param itemList 需要被计算的list
     */
    public void addShrinkParams(List<? extends Item> itemList) {
        for (Item item : itemList) {
            if (currShrinkLen < item.getShrinkLength()) {
                //如果新增加的shrink长度大于之前的，则需要重新初始化
                initShrinkParams(itemList);
                return;
            }
        }
        //如果新增加的shrink长度没有大于之前的，则直接为新增加的赋值
        setShrinkForItemContent(itemList);
    }

    /**
     * 为item list计算合适的缩进大小
     *
     * @param itemList 需要被计算的list
     */
    public void initShrinkParams(List<? extends Item> itemList) {
        for (Item item : itemList) {
            currShrinkLen = Math.max(item.getShrinkLength(), currShrinkLen);
        }
        setShrinkForItemContent(itemList);
    }

    /**
     * 为Item设置统一的shrinkLength
     *
     * @param dataItemList 需要重新设置shrink的list
     */
    private void setShrinkForItemContent(List<? extends Item> dataItemList) {
        if (currShrinkLen <= 0) {
            return;
        }
        currShrinkLen = Math.min(currShrinkLen, shrinkLenMax);
        for (Item item : dataItemList) {
            if (currShrinkLen != item.getShrinkLength()) {
                item.setShrinkLength(currShrinkLen);
            }
        }
    }


    public static void setShrinkView(TextView textView, int shrinkLength) {
        textView.setMaxEms(shrinkLength + 1);
        textView.setMinEms(shrinkLength + 1);
    }

}
