package free.com.itemlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable dividerDrawable;
    private Paint dividerPaint;
    private int dividerHeight;
    private int orientation = LinearLayoutManager.VERTICAL;

    public GridItemDecoration(Drawable divider) {
        dividerDrawable = divider;
    }

    public GridItemDecoration(Context context, int resId) {
        dividerDrawable = context.getResources().getDrawable(resId);
        dividerHeight = dividerDrawable.getIntrinsicHeight();
    }

    public GridItemDecoration(Context context, int dividerColor, int dividerHeight) {
        this.dividerHeight = dividerHeight;
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStyle(Paint.Style.FILL);
    }

    // TODO: 2016/7/6 需要实现线性布局（使用现有逻辑即可）和表格布局（需判断spansize设置left）两种模式的分割线
    //目前横向上实现逻辑占用单个Item布局的空间，应该判断实现让分割线的size平分到每个Item
    //目前竖向上实现逻辑第0个的top是0所以边线画在屏幕外，其余的top是边线的高度则边线填充此区域（原理画在当前view的Top以上）
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (dividerDrawable == null) {
            return;
        }

        if (parent.getChildLayoutPosition(view) < 1) {
            return;
        }

        outRect.top = dividerHeight;
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
        int rightV = parent.getWidth();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int leftV = parent.getPaddingLeft() + child.getPaddingLeft();
            int bottomV = child.getTop() - params.topMargin;
            int topV = bottomV - dividerHeight;

            int topH = child.getTop() + params.topMargin;
            int bottomH = child.getBottom() + params.bottomMargin;
            int leftH = child.getRight() + params.rightMargin;
            int rightH = leftH + dividerHeight;
//            int rightH = child.getLeft() - params.leftMargin;
//            int leftH = rightH - dividerDrawable.getIntrinsicWidth();
            // TODO: 2016/7/8 不能简单if...else
            if (dividerDrawable != null) {
                dividerDrawable.setBounds(leftH, topH, rightH, bottomH);
                dividerDrawable.draw(c);
                dividerDrawable.setBounds(leftV, topV, rightV, bottomV);
                dividerDrawable.draw(c);
            } else if (dividerPaint != null) {
                c.drawRect(leftH, topH, rightH, bottomH, dividerPaint);
                c.drawRect(leftV, topV, rightV, bottomV, dividerPaint);
            }
        }
    }


}
