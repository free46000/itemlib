package free.com.itemlib.item.decoration.divider;

import android.graphics.Canvas;

/**
 * Created by free46000 on 2016/7/24 0024.
 */
public interface Divider {
    /**
     * 画出分割线
     *
     * @param c 需要画的画布
     */
    void draw(Canvas c, int left, int top, int right, int bottom);

    /**
     * @return 分割线的高度
     */
    float getHeight();

    /**
     * @return 分割线的宽度
     */
    float getWidth();
}
