package free.com.itemlib.item.decoration.divider;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * paint方式
 *
 * Created by free46000 on 2016/7/24 0024.
 */
public class DividerRect implements Divider {
    private Paint mDividerPaint;
    private float mDividerHeight;
    private float mDividerWidth;

    public DividerRect(int dividerColor, float dividerSize) {
        this.mDividerHeight = dividerSize;
        this.mDividerWidth = dividerSize;
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(dividerColor);
        mDividerPaint.setStyle(Paint.Style.FILL);
    }

    public DividerRect(int dividerColor, float dividerHeight, float dividerWidth) {
        this.mDividerHeight = dividerHeight;
        this.mDividerWidth = dividerWidth;
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(dividerColor);
        mDividerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas c, int left, int top, int right, int bottom) {
        c.drawRect(left, top, right, bottom, mDividerPaint);
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
