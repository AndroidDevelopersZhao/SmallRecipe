package cn.com.xxutils.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import cn.com.xxutils.util.XXListViewAnimationMode;
import cn.com.xxutils.util.XXlistViewAnimation;


public class XXListViewCompat extends ListView {

    private static final String TAG = "ListViewCompat";

    private SlideView mFocusedItemView;

    public XXListViewCompat(Context context) {
        super(context);
    }

    public XXListViewCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XXListViewCompat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void shrinkListItem(int position) {
        View item = getChildAt(position);

        if (item != null) {
            try {
                ((SlideView) item).shrink();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);
                Log.e(TAG, "postion=" + position);
                if (position != INVALID_POSITION) {
                    MessageItem data = (MessageItem) getItemAtPosition(position);
                    mFocusedItemView = data.slideView;
                    Log.e(TAG, "FocusedItemView=" + mFocusedItemView);
                }
            }
            default:
                break;
        }

        if (mFocusedItemView != null) {
            mFocusedItemView.onRequireTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    private XXlistViewAnimation animation = null;

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


}
