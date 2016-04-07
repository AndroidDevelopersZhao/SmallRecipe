package com.cn.smallrecipe.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/4/7.
 */
public class QRCodeActivity extends MyActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ImageView imageview_qr = (ImageView) findViewById(R.id.imageview_qr);
        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("qr");
        imageview_qr.setImageBitmap(bitmap);
    }

}
