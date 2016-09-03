package free.com.itemlib.item.decoration.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * drawable方式
 *
 * Created by free46000 on 2016/7/24 0024.
 */
public class DividerDrawable implements Divider {
    private Drawable mDividerDrawable;
    private float mDividerHeight;
    private float mDividerWidth;

    public DividerDrawable(Context context, int resId) {
        this(context.getResources().getDrawable(resId));
    }

    public DividerDrawable(Drawable dividerDrawable) {
        if (dividerDrawable == null) {
            throw new IllegalArgumentException("dividerDrawable 不能为null");
        }
        this.mDividerDrawable = dividerDrawable;
        mDividerHeight = dividerDrawable.getIntrinsicHeight();
        mDividerWidth = dividerDrawable.getIntrinsicWidth();
    }


    @Override
    public void draw(Canvas c, int left, int top, int right, int bottom) {
        mDividerDrawable.setBounds(left, top, right, bottom);
        mDividerDrawable.draw(c);
    }

    @Override
    public float getHeight() {
        return mDividerHeight;
    }

    @Override
    public float getWidth() {
        return mDividerWidth;
    }
}
