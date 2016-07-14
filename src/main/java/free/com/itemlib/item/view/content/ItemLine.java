package free.com.itemlib.item.view.content;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import free.com.itemlib.R;
import free.com.itemlib.item.ItemEntity;
import free.com.itemlib.item.view.ItemViewHolder;


/**
 * Created by ZX on 2015/10/16.
 */
public class ItemLine extends ItemImpl {
    private int lineDrawableResId = R.color.item_line_color;
    private float lineSizeInDP = 1;

    public ItemLine(int lineDrawableResId, float lineSizeInDP) {
        this.lineDrawableResId = lineDrawableResId;
        this.lineSizeInDP = lineSizeInDP;
    }

    public ItemLine(ItemEntity itemEntity, int lineDrawableResId, float lineSizeInDP) {
        super(itemEntity);
        this.lineDrawableResId = lineDrawableResId;
        this.lineSizeInDP = lineSizeInDP;
    }

    public ItemLine() {

    }

    @Override
    public ItemViewHolder newItemViewHolder(Context context, ViewGroup parent) {
        return new ItemLineView(context, this);
    }

    public void setLineDrawableResId(int lineDrawableResId) {
        this.lineDrawableResId = lineDrawableResId;
    }

    public void setLineSizeInDP(float lineSizeInDP) {
        this.lineSizeInDP = lineSizeInDP;
    }

    @Override
    public String getItemViewType() {
        //这样如果line的背景和line的大小不同则会生产不同的line，好处是view的setData不用实现
        return super.getItemViewType() + lineDrawableResId + "=" + lineSizeInDP;
    }

    class ItemLineView extends ItemViewHolder<ItemLine> {

        public ItemLineView(Context context, ItemLine item) {
            super(context, item);
        }

        @Override
        protected View initItemView() {
            LinearLayout linearLayout = new LinearLayout(context);
            View view = new View(context);
            view.setBackgroundResource(lineDrawableResId);
            //// TODO: 2016/6/19 0019 需要增加dp和px转换
//            linearLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
//                    DensityUtils.dip2px(context, lineSizeInDP));
            linearLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)lineSizeInDP);
            return linearLayout;
        }


        @Override
        public void setData(ItemLine itemContent) {
        }
    }

}
