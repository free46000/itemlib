package free.com.itemlib.item.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import free.com.itemlib.item.decoration.divider.Divider;
import free.com.itemlib.item.decoration.divider.DividerDrawable;
import free.com.itemlib.item.decoration.divider.DividerRect;

/**
 *
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private Divider divider;

    public GridItemDecoration(Drawable dividerDrawable) {
        divider = new DividerDrawable(dividerDrawable);
    }

    public GridItemDecoration(Context context, int resId) {
        divider = new DividerDrawable(context, resId);
    }

    public GridItemDecoration(Context context, int dividerColor, int dividerSize) {
        divider = new DividerRect(dividerColor, dividerSize);
    }

    // TODO: 2016/7/17 0017 四周边框实现 可以再最外层设置分割线，然后本类实现是否展示最后一行（需根据spansize和isFullSpan判断）分割线
    //实在不行可以在建一个没有最后分割线的类（实现逻辑判断第0个则不设置offset，所以top是0分割线画在屏幕外，其余的top是分割线的高度则边线填充此区域（原理画在当前itemview的Top以上））
    // TODO: 2016/7/17 headview 和 footview实现可展示隐藏的分割线
    // （首先在ItemViewHolder中实现isShowDecoration方法
    // 1.不好！！！使用onDraw然后判断是否是展示则对应是否设置offset，原理如果item本身有背景则会覆盖住分割线，但未考虑到margin问题）
    //2.建议使用！！！getItemOffsets判断是否是展示则对应是否设置，画的时候也做下判断
    // TODO: 2016/7/6 需要实现线性布局（使用现有逻辑即可）和表格布局（需判断spansize设置left）两种模式的分割线
    //目前横向上实现逻辑占用单个Item布局的空间（把分割线扩大就能看到效果），应该判断实现让分割线的size平分到每个Item（原理画在当前view的Right的右边）
    //目前竖向上实现逻辑正常，画在视图的下方并且不占用Item空间
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //判断是不是最后一个，如果是表格布局则不能代表是否为最后一行
        if (parent.getChildLayoutPosition(view) == state.getItemCount() - 1) {
        }

        //使item下方留出分割线空间
        outRect.bottom = (int) divider.getHeight();
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
            int bottomV = topV + (int) divider.getHeight();


            //纵向分割线的测量值
            int topH = child.getTop();
            int bottomH = child.getBottom() + (int) divider.getHeight();//需要多画出来一个分割线的高度（即横向中少画的一块）
            int leftH = child.getRight();
            int rightH = leftH + (int) divider.getWidth();
//            int rightH = child.getLeft() - params.leftMargin;
//            int leftH = rightH - dividerDrawable.getIntrinsicWidth();

            divider.draw(c, leftH, topH, rightH, bottomH);
            divider.draw(c, leftV, topV, rightV, bottomV);
        }
    }


}
