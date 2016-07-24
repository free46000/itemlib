package free.com.itemlib.item.decoration.divider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by free46000 on 2016/7/24 0024.
 */
public class DividerRect implements Divider {
    private Paint dividerPaint;
    private float dividerHeight;
    private float dividerWidth;

    public DividerRect(int dividerColor, float dividerSize) {
        this.dividerHeight = dividerSize;
        this.dividerWidth = dividerSize;
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStyle(Paint.Style.FILL);
    }

    public DividerRect(int dividerColor, float dividerHeight, float dividerWidth) {
        this.dividerHeight = dividerHeight;
        this.dividerWidth = dividerWidth;
        dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dividerPaint.setColor(dividerColor);
        dividerPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas c, int left, int top, int right, int bottom) {
        c.drawRect(left, top, right, bottom, dividerPaint);
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
