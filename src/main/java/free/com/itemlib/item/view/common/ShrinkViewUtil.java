package free.com.itemlib.item.view.common;

import android.widget.TextView;

public class ShrinkViewUtil {
    public static void setShrinkView(TextView textView, int shrinkLength) {
        textView.setMaxEms(shrinkLength + 1);
        textView.setMinEms(shrinkLength + 1);
    }
}
