package free.com.itemlib.item.decoration.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by free46000 on 2016/7/24 0024.
 */
public class DividerDrawable implements Divider {
    private Drawable dividerDrawable;
    private float dividerHeight;
    private float dividerWidth;

    public DividerDrawable(Context context, int resId) {
        this(context.getResources().getDrawable(resId));
    }

    public DividerDrawable(Drawable dividerDrawable) {
        if (dividerDrawable == null) {
            throw new IllegalArgumentException("dividerDrawable 不能为null");
        }
        this.dividerDrawable = dividerDrawable;
        dividerHeight = dividerDrawable.getIntrinsicHeight();
        dividerWidth = dividerDrawable.getIntrinsicWidth();
    }


    @Override
    public void draw(Canvas c, int left, int top, int right, int bottom) {
        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(c);
    }

    @Override
    public float getHeight() {
        return dividerHeight;
    }

    @Override
    public float getWidth() {
        return dividerWidth;
    }
}
