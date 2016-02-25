package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/2/25.
 */
public class RegisterActivity extends MyActivity implements View.OnClickListener {
    private LinearLayout layout_register_back;
    private static final int RESULT_CODE = 0x01;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "inter register activity");
        setContentView(R.layout.activity_register);
        findId();
        setOclick();
    }

    private void setOclick() {
        layout_register_back.setOnClickListener(this);
    }

    private void findId() {
        layout_register_back = (LinearLayout) findViewById(R.id.layout_register_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_register_back:
                setResult(RESULT_CODE);
                this.finish();
                break;
        }
    }
}
