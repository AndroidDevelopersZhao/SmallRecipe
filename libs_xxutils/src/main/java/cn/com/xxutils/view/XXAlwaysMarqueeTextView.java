package cn.com.xxutils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/1/7.
 */
public class XXAlwaysMarqueeTextView extends TextView {
    public XXAlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public XXAlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XXAlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}