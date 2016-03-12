package com.cn.smallrecipe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;
import com.cn.smallrecipe.Util;
import com.cn.smallrecipe.datainfo.sendsayinfo.Resp_Say;
import com.cn.smallrecipe.view.HorizontalListView;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.xxutils.adapter.XXListViewAdapter;
import cn.com.xxutils.alerterview.OnItemClickListener;
import cn.com.xxutils.alerterview.XXAlertView;
import cn.com.xxutils.progress.XXSVProgressHUD;
import cn.com.xxutils.util.Listener_location;
import cn.com.xxutils.util.Location_Client;
import cn.com.xxutils.util.XXHttpClient;
import cn.com.xxutils.util.XXSharedPreferences;
import cn.com.xxutils.util.XXUtils;

/**
 * TODO 加入百度定位
 * Created by Administrator on 2016/3/10.
 */
public class SendSayActivity extends MyActivity implements AdapterView.OnItemClickListener, View.OnClickListener, Listener_location {
    private HorizontalListView lv_h_sendsay;
    private LinearLayout layout_img;
    private EditText et_input_say;//输入框
    private TextView tv_location_say;//定位显示框
    private Button bt_send_say;//发表按钮
    private String com_id = null;
    private String com_name = null;
    private XXListViewAdapter<Bitmap> adapter = new XXListViewAdapter<Bitmap>(this, R.layout.item_listview_h_sendsay) {
        @Override
        public void initGetView(int position, View convertView, ViewGroup parent) {
            ImageView item_iv_listview_h_sendsay = (ImageView) convertView.findViewById(R.id.item_iv_listview_h_sendsay);
            item_iv_listview_h_sendsay.setImageBitmap(getItem(position));
        }
    };
    private Button bt_add_img;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendsay);
        initView();
        Log.d(TAG, "intent.get ID:" + getIntent().getStringExtra("id"));
        com_id = getIntent().getStringExtra("id");
        Log.d(TAG, "intent.get com_name:" + getIntent().getStringExtra("com_name"));
        com_name = getIntent().getStringExtra("com_name");
        Log.d(TAG, "开始定位");
        new Location_Client(this, this).start();//初始化定位对象并开始定位，结果由回调带回
    }

    private void initView() {
        bt_add_img = (Button) findViewById(R.id.bt_add_img);
        bt_add_img.setOnClickListener(this);
        lv_h_sendsay = (HorizontalListView) findViewById(R.id.lv_h_sendsay);
        lv_h_sendsay.setAdapter(adapter);
        lv_h_sendsay.setOnItemClickListener(this);
        layout_img = (LinearLayout) findViewById(R.id.layout_img);
        et_input_say = (EditText) findViewById(R.id.et_input_say);
        tv_location_say = (TextView) findViewById(R.id.tv_location_say);
        bt_send_say = (Button) findViewById(R.id.bt_send_say);
        bt_send_say.setOnClickListener(this);
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(this, R.drawable.delete_default_qq_avatar));
//        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "position---------" + position);
        adapter.remove(position);
        adapter.notifyDataSetChanged();
        if (adapter.getList().size() != 0) {
            layout_img.setVisibility(View.VISIBLE);
        } else {
            layout_img.setVisibility(View.GONE);
        }
    }

    private int REQUSETCODE_CAM = 0x01;
    private int REQUSETCODE_PIC = 0x02;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_img:
                new XXAlertView("提示", "请选择图片来源", "取消", null,
                        new String[]{"图库", "相机"}, this, XXAlertView.Style.ActionSheet,
                        new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                Log.d(TAG, "position:" + position);
                                if (position == 0) {
                                    //图库
                                    Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(picture, REQUSETCODE_PIC);
//                                    layout_img.setVisibility(View.VISIBLE);
//                                    adapter.addItem(cn.com.xxutils.util.XXUtils.getBitmapFromResources(SendSayActivity.this,
//                                            R.drawable.delete_default_qq_avatar));
//                                    adapter.notifyDataSetChanged();
                                } else if (position == 1) {
                                    //相机
                                    Intent cameraintent = new Intent(
                                            MediaStore.ACTION_IMAGE_CAPTURE);
//                                    cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,
//                                            Uri.fromFile(tempFile));
                                    startActivityForResult(cameraintent, REQUSETCODE_CAM);
                                }
                            }
                        }).show();
                break;
            case R.id.bt_send_say:
                //TODO 发表说说
                String text = et_input_say.getText().toString().trim();
                if (text.equals("")) {
                    Toast.makeText(SendSayActivity.this, "您还没有输入哦", Toast.LENGTH_SHORT).show();
                    return;
                }
                XXSVProgressHUD.showWithStatus(SendSayActivity.this, "正在发表...");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < adapter.getList().size(); i++) {
                    Log.d(TAG, "bitmap" + i + "," + adapter.getItem(i));
                    sb.append("ф").append(XXUtils.bitmapToBase64(adapter.getItem(i)));
                }
                int picNum = (sb.toString().split("ф").length - 1);
                Log.d(TAG, "上送图片张数：" + (sb.toString().split("ф").length - 1));
                String usernumber = new XXSharedPreferences(MainActivity.SHAREDSESSIONIDSAVEEDNAME).get(SendSayActivity.this,
                        "usernumber", "").toString();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                String images = sb.toString();
                String city = location != null ? location.getCity() : "";
                sendSayToService(usernumber, time, text, images, this.com_id, this.com_name, city);
                break;
        }
    }

    /**
     * 将图片image压缩成大小为 size的图片（size表示图片大小，单位是KB）
     *
     * @param image 图片资源
     * @param size  图片大小
     * @return Bitmap
     */
    private Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);

        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * app 端请求发表说说
     *
     * @param usernumber 用户账号
     * @param time       发表时间
     * @param text       发表文本
     * @param images     拼接后的Base64字符串图片
     * @param com_id     菜谱ID
     * @param com_name   菜谱名称
     * @param city       定位城市
     */
    private void sendSayToService(String usernumber, String time,
                                  String text, String images, String com_id,
                                  String com_name, String city) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dissmiss();

                switch (msg.what) {
                    case -1:
                        Toast.makeText(SendSayActivity.this, msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(SendSayActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                        et_input_say.setText("");
                        adapter.removeAll();
                        adapter.notifyDataSetChanged();
                        layout_img.setVisibility(View.GONE);
                        //TODO 跳转页面至厨艺圈
                        break;
                }
            }
        };
        XXHttpClient client = new XXHttpClient(Util.URL_SENDSAY, true, new XXHttpClient.XXHttpResponseListener() {
            @Override
            public void onSuccess(int i, byte[] bytes) {
                Log.d(TAG, "发表说说的回调：" + new String(bytes));
                Resp_Say say = new Gson().fromJson(new String(bytes), Resp_Say.class);
                if (say.getResultCode() == 9000) {
                    Util.sendMsgToHandler(handler, "发表成功", true);
                } else {
                    Util.sendMsgToHandler(handler, say.getResultMsg(), false);
                }
            }

            @Override
            public void onError(int i, Throwable throwable) {
                Log.e(TAG, "说说发表失败，网络异常");
                Util.sendMsgToHandler(handler, "网络", false);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

            }
        });
        client.put("usernumber", usernumber);
        client.put("time", time);
        client.put("text", text);
        client.put("images", images);
        client.put("com_id", com_id);
        client.put("com_name", com_name);
        client.put("city", city);
        Log.i(TAG, "上送的说说报文：" + client.getAllParams());
        client.doPost(15000);

    }

    private void dissmiss() {
        if (XXSVProgressHUD.isShowing(SendSayActivity.this)) {
            XXSVProgressHUD.dismiss(SendSayActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        if (requestCode == REQUSETCODE_CAM) {
            //来源为相机
            Bundle bundle = data.getExtras();
            //获取相机返回的数据，并转换为图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            adapter.addItem( compressImage(bitmap,100));//压缩图片
        } else if (requestCode == REQUSETCODE_PIC) {
            //来源为图库
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            adapter.addItem(compressImage(bitmap,100));
        }
        if (adapter.getList().size() != 0) {
            layout_img.setVisibility(View.VISIBLE);
        } else {
            layout_img.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(SendSayActivity.this, "点击图片可以移除重新选择哦", Toast.LENGTH_SHORT).show();
    }

    private BDLocation location = null;

    @Override
    public void Location(BDLocation location) {
        Log.w(TAG, "定位成功：" + location.getCity());
        tv_location_say.setText(location.getCity());
        this.location = location;
    }
}
