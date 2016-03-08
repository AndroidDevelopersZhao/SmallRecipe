package cn.com.xxutils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.ListView;

import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.util.XXlistViewAnimation;

/**
 * Created by Administrator on 2016/2/25.
 */
public class XXListView extends ListView {

    private XXlistViewAnimation animation = null;

    public XXListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListViewAnimation(BaseAdapter adapter, XXListViewAnimationMode Mode) {
        if (animation == null) {
            animation = new XXlistViewAnimation(this, adapter);
        }
        if (Mode == XXListViewAnimationMode.ANIIMATION_RIGHT) {
            animation.setRightAdapter();
        } else if (Mode == XXListViewAnimationMode.ANIIMATION_LEFT) {
            animation.setLeftAdapter();
        } else if (Mode == XXListViewAnimationMode.ANIIMATION_ALPHA) {
            animation.setAlphaAdapter();
        } else if (Mode == XXListViewAnimationMode.ANIIMATION_BOTTOM) {
            animation.setBottomAdapter();
        } else if (Mode == XXListViewAnimationMode.ANIIMATION_BOTTOM_RIGHT) {
            animation.setBottomRightAdapter();
        } else if (Mode == XXListViewAnimationMode.ANIIMATION_SCALE) {
            animation.setScaleAdapter();
        }

    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
