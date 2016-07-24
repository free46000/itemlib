package free.com.itemlib.item.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import free.com.itemlib.item.decoration.divider.Divider;
import free.com.itemlib.item.decoration.divider.DividerDrawable;
import free.com.itemlib.item.decoration.divider.DividerRect;

/**
 *
 */
public class HorizontalItemDecoration extends ItemDecoration {

    public HorizontalItemDecoration(Drawable dividerDrawable) {
        super(dividerDrawable);
    }

    public HorizontalItemDecoration(Context context, int resId) {
        super(context, resId);
    }

    public HorizontalItemDecoration(int dividerColor, int dividerSize) {
        super(dividerColor, dividerSize);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //使item下方留出分割线空间
        if (isNeedHorizontalDivider(view, parent))
            outRect.bottom = (int) mDivider.getHeight();
    }

    /**
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
//        int rightV = parent.getRight();
//        int leftV = parent.getLeft();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            if (!isNeedHorizontalDivider(child, parent)) {
                continue;
            }

            //横向分割线的测量值
            int rightV = child.getRight();
            int leftV = child.getLeft();
            int topV = child.getBottom();
            int bottomV = topV + (int) mDivider.getHeight();

            mDivider.draw(c, leftV, topV, rightV, bottomV);
        }
    }
}
