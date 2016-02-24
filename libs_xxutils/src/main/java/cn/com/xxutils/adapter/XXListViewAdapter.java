package cn.com.xxutils.adapter;
/**
 * .   如果你认为你败了，那你就一败涂地；
 * .   如果你认为你不敢，那你就会退缩；
 * .   如果你想赢但是认为你不能；
 * .   那么毫无疑问你就会失利；
 * .   如果你认为你输了，你就输了；
 * .   我们发现成功是从一个人的意志开始的；
 * .   成功是一种心态。
 * .   生活之战中，
 * .   胜利并不属于那些更强和更快的人，
 * .   胜利者终究是认为自己能行的人。
 * .
 * .   If you think you are beaten,you are;
 * .   If you think you dare not,you don't;
 * .   If you can to win but think you can't;
 * .   It's almost a cinch you won't.
 * .   If you think you'll lose,you're lost;
 * .   For out of the world we find Success begins with a fellow's will;
 * .   It's all in a state of mind.
 * .   Life's battles don't always go to the stronger and faster man,But sooner or later the man who wins Is the man who thinks he can.
 * .
 * .   You can you do.  No can no bb.
 * .
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 *  项目名称： SoEasy
 *  创建日期： 2015/12/25  13:03
 *  项目作者： 赵文贇
 *  项目包名： com.shanghai.soeasylib.view
 */
public abstract class XXListViewAdapter<T> extends BaseAdapter {
    private List<T> list = new ArrayList<T>();
    private Context context;
    private int resId;//

    public XXListViewAdapter(Context context, int resId) {
        this.context = context;
        this.resId = resId;
    }

    public void removeAll() {
        this.list = new ArrayList<>();
    }

    public List<T> getList() {

        return list;
    }

    public void addItem(T item) {
        list.add(item);
    }

    public void remove(int position) {
        list.remove(position);
    }

    public void removeLast() {
        remove(getCount() - 1);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resId, null);
        }
        initGetView(position, convertView, parent);
        return convertView;
    }

    public abstract void initGetView(int position, View convertView, ViewGroup parent);
}

