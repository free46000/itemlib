package free.com.itemlib.item.decoration;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import free.com.itemlib.item.decoration.divider.Divider;
import free.com.itemlib.item.decoration.divider.DividerDrawable;
import free.com.itemlib.item.decoration.divider.DividerRect;

/**
 * Created by free46000 on 2016/7/24 0024.
 */
public abstract class ItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 结束分割线模式，即每个Item下方或者右方（根据不同方向分割线区别）都有分割线
     */
    public static final int END_DIVIDER_MODE = 1;
    /**
     * 内部分割线模式，只在相邻Item之间才会有分割线
     */
    public static final int INSIDE_DIVIDER_MODE = 2;


    protected Divider mDivider;
    protected int mDividerMode = END_DIVIDER_MODE;

    public ItemDecoration(Drawable dividerDrawable) {
        mDivider = new DividerDrawable(dividerDrawable);
    }

    public ItemDecoration(Context context, int resId) {
        mDivider = new DividerDrawable(context, resId);
    }

    public ItemDecoration(int dividerColor, int dividerSize) {
        mDivider = new DividerRect(dividerColor, dividerSize);
    }

    /**
     * 设置分割线Mode {@link #INSIDE_DIVIDER_MODE}{@link #END_DIVIDER_MODE}
     * <p/>
     * PS:如果想实现四周边框模式，则选择INSIDE_DIVIDER_MODE，然后为RecycleView设置背景图，这样圆角问题也可以解决
     * TODO 实现另一种模式
     *
     * @param dividerMode
     */
    public void setDividerMode(int dividerMode) {
        if (mDividerMode != END_DIVIDER_MODE) {
            throw new UnsupportedOperationException("暂未实现END_DIVIDER_MODE以外的其他模式");
        }
        mDividerMode = dividerMode;
    }

    /**
     * 根据内部{@link #INSIDE_DIVIDER_MODE}和结束{@link #END_DIVIDER_MODE}分割线确定是否需要水平分割线
     * <p/>
     * 如果是列表模式则只需要判断是否最后一个
     * 如果是表格模式则比较麻需要span size和当前span坐下对比，还需要判断是否fullSpan
     * <p/>
     * TODO: 2016/7/17 headview 和 footview实现可展示隐藏的分割线
     * （在Item和ItemViewHolder中已经实现isShowDecoration方法，这样不仅head和foot其他也可以实现是否有分割线
     * 现在需要再BaseItemAdapter中实现方法设置是否需要head和foot的分割线
     *
     * @return
     */
    protected boolean isNeedHorizontalDivider(View childView, RecyclerView recyclerView) {
        if (mDividerMode == END_DIVIDER_MODE) {
            return true;
        } else {
            throw new UnsupportedOperationException("暂未实现END_DIVIDER_MODE以外的其他模式");
        }
    }

    protected boolean isNeedVerticalDivider(int position, RecyclerView recyclerView) {
        if (mDividerMode == END_DIVIDER_MODE) {
            return true;
        } else {
            throw new UnsupportedOperationException("暂未实现END_DIVIDER_MODE以外的其他模式");
        }
    }


    protected boolean isLastVerticalNeedDecoration() {
        return false;
    }


}
