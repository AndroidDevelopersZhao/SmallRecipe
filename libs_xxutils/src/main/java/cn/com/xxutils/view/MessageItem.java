package cn.com.xxutils.view;

/**
 * Created by Administrator on 2016/2/23.
 */
public class MessageItem {
    public int iconRes;
    public String title;
    public String msg;
    public String time;
    public SlideView slideView;
    public String img=null;

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public SlideView getSlideView() {
        return slideView;
    }

    public void setSlideView(SlideView slideView) {
        this.slideView = slideView;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
