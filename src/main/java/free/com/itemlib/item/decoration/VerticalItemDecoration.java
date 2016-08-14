package free.com.itemlib.item.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 */
public class VerticalItemDecoration extends ItemDecoration {

    public VerticalItemDecoration(Drawable dividerDrawable) {
        super(dividerDrawable);
    }

    public VerticalItemDecoration(Context context, int resId) {
        super(context, resId);
    }

    public VerticalItemDecoration(int dividerColor, int dividerSize) {
        super(dividerColor, dividerSize);
    }

    //2016/7/25 0025 把本类改成垂直分割线类，参考水平类，可以暂时先使用现有逻辑，即选择end分割线模式，暂时先不考虑缺陷
    //TODO 2016/7/6 需要实现线性布局（使用现有逻辑即可）和表格布局（需判断spansize设置left）两种模式的分割线
    //目前横向上实现逻辑占用单个Item布局的空间（把分割线扩大就能看到效果），应该判断实现让分割线的size平分到每个Item（原理画在当前view的Right的右边）
    //目前竖向上的分割线已经建好HorizontalItemDecoration
    //这样添加分割线的时候只需要添加横向和竖向的两个分割线对象即可
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (isNeedHorizontalDivider(view, parent)) {

        }
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (!isNeedHorizontalDivider(child, parent)) {
                continue;
            }

            //垂直分割线的测量值
            int top = child.getTop();
            int bottom = child.getBottom() + (int) mDivider.getHeight();//需要多画出来一个分割线的高度（即横向中少画的一块）
            int left = child.getRight();
            int right = left + (int) mDivider.getWidth();

            mDivider.draw(c, left, top, right, bottom);
        }
    }


}
