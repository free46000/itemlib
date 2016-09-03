package free.com.itemlib.item.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 添加水平和垂直两个分割线即可
 *
 * @deprecated {@link HorizontalItemDecoration} {@link VerticalItemDecoration}
 */
public class GridItemDecoration extends ItemDecoration {

    public GridItemDecoration(Drawable dividerDrawable) {
        super(dividerDrawable);
    }

    public GridItemDecoration(Context context, int resId) {
        super(context, resId);
    }

    public GridItemDecoration(int dividerColor, int dividerSize) {
        super(dividerColor, dividerSize);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //判断是不是最后一个，如果是表格布局则不能代表是否为最后一行
        if (parent.getChildLayoutPosition(view) == state.getItemCount() - 1) {
        }

        //使item下方留出分割线空间
        outRect.bottom = (int) mDivider.getHeight();
//        outRect.right = dividerWidth;
//        outRect.left = dividerDrawable.getIntrinsicWidth();
//        if (orientation == LinearLayoutManager.VERTICAL) {
//            outRect.top = dividerDrawable.getIntrinsicHeight();
//        } else if (orientation == LinearLayoutManager.HORIZONTAL) {
//            outRect.left = dividerDrawable.getIntrinsicWidth();
//        }
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
//        int rightV = parent.getWidth();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            //横向分割线的测量值
            int leftV = child.getLeft();
            int rightV = child.getRight();
//            int bottomV = child.getTop() - params.topMargin;
//            int topV = bottomV - dividerHeight;
            int topV = child.getBottom();
            int bottomV = topV + (int) mDivider.getHeight();


            //纵向分割线的测量值
            int topH = child.getTop();
            int bottomH = child.getBottom() + (int) mDivider.getHeight();//需要多画出来一个分割线的高度（即横向中少画的一块）
            int leftH = child.getRight();
            int rightH = leftH + (int) mDivider.getWidth();
//            int rightH = child.getLeft() - params.leftMargin;
//            int leftH = rightH - dividerDrawable.getIntrinsicWidth();

            mDivider.draw(c, leftH, topH, rightH, bottomH);
            mDivider.draw(c, leftV, topV, rightV, bottomV);
        }
    }


}
