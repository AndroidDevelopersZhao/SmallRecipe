package cn.com.xxutils.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/2/23.
 */
public class XXViewPagerAdapter extends PagerAdapter {
    private List<ImageView> imageViewList;

    public XXViewPagerAdapter(List<ImageView> imageViewList) {
        this.imageViewList = imageViewList;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    /**
     * 判断出去的view是否等于进来的view 如果为true直接复用
     */
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    /**
     * 销毁预加载以外的view对象, 会把需要销毁的对象的索引位置传进来就是position
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViewList.get(position % imageViewList.size()));
    }

    /**
     * 创建一个view
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(imageViewList.get(position % imageViewList.size()));
        return imageViewList.get(position % imageViewList.size());
    }
}