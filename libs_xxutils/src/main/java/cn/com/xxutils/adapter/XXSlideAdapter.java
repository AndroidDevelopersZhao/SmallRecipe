package cn.com.xxutils.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.com.xxutils.R;
import cn.com.xxutils.XXApplication;
import cn.com.xxutils.interfac.ItemDeleteButtonOnTochListener;
import cn.com.xxutils.util.XXImagesLoader;
import cn.com.xxutils.view.MessageItem;
import cn.com.xxutils.view.SlideView;

/**
 * Created by Administrator on 2016/2/23.
 */
public class XXSlideAdapter extends BaseAdapter implements SlideView.OnSlideListener {
    private LayoutInflater mInflater;
    private List<MessageItem> mMessageItems;
    private Context context;
    private ItemDeleteButtonOnTochListener itemDeleteButtonOnTochListener;
    private SlideView mLastSlideViewWithStatusOn;

    public XXSlideAdapter(Activity context, List<MessageItem> mMessageItems) {
        super();
        mInflater = context.getLayoutInflater();
        this.context = context.getApplicationContext();
        this.mMessageItems = mMessageItems;

    }

    public void addItem(MessageItem mMessageItems) {
        this.mMessageItems.add(mMessageItems);
    }

    public void removeItem(int postion) {
        mMessageItems.remove(postion);
    }

    public void setItemDeleteListener(ItemDeleteButtonOnTochListener itemDeleteButtonOnTochListener) {
        this.itemDeleteButtonOnTochListener = itemDeleteButtonOnTochListener;
    }

    @Override
    public int getCount() {
        return mMessageItems.size();
    }

    @Override
    public MessageItem getItem(int position) {
        return mMessageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        SlideView slideView = (SlideView) convertView;
        if (slideView == null) {
            View itemView = mInflater.inflate(R.layout.item_listview_delete, null);

            slideView = new SlideView(context);
            slideView.setContentView(itemView);

            holder = new ViewHolder(slideView);
            slideView.setOnSlideListener(this);
            slideView.setTag(holder);
        } else {
            holder = (ViewHolder) slideView.getTag();
        }
        MessageItem item = mMessageItems.get(position);
        item.slideView = slideView;
        item.slideView.shrink();

//        holder.icon.setImageResource(item.iconRes);
        try {

            new XXImagesLoader(null, true, R.drawable.delete_default_qq_avatar,
                    R.drawable.delete_default_qq_avatar, R.drawable.delete_default_qq_avatar)
                    .disPlayImage(item.img, holder.icon);
        } catch (Throwable throwable) {
            holder.icon.setImageResource(item.iconRes);
        }
        holder.title.setText(item.title);
        holder.msg.setText(item.msg);
        holder.time.setText(item.time);
        holder.deleteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(XXApplication.TAG, "Position：" + position);
                itemDeleteButtonOnTochListener.onItemDelete(v, position);
            }
        });

        return slideView;
    }

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
        }
    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView title;
        public TextView msg;
        public TextView time;
        public ViewGroup deleteHolder;

        ViewHolder(View view) {
            icon = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            msg = (TextView) view.findViewById(R.id.msg);
            time = (TextView) view.findViewById(R.id.time);
            deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
        }
    }
}
