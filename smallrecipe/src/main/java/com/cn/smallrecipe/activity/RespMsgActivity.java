package com.cn.smallrecipe.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.smallrecipe.MyActivity;
import com.cn.smallrecipe.R;

/**
 * Created by Administrator on 2016/3/9.
 */
public class RespMsgActivity extends MyActivity implements View.OnClickListener {
    private EditText et_respmsg;
    private Button bt_send_respmsg;
    private ImageView iv_back;
    private TextView tv_back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respmsg);
        et_respmsg = (EditText) findViewById(R.id.et_respmsg);
        bt_send_respmsg = (Button) findViewById(R.id.bt_send_respmsg);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        bt_send_respmsg.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send_respmsg:
                if (et_respmsg.getText() != null && !et_respmsg.getText().toString().equals("")) {

                    sendRespMsgToService(et_respmsg.getText().toString().trim());
                } else {
                    Toast.makeText(this, "您还没有输入内容哦", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                this.finish();
                break;

            case R.id.tv_back:
                this.finish();
                break;
        }
    }

    /**
     * 上送用户意见建议到服务器
     *
     * @param trim
     */
    private void sendRespMsgToService(String trim) {
        et_respmsg.setText("");
        Toast.makeText(RespMsgActivity.this, "提交成功，感谢您的支持", Toast.LENGTH_SHORT).show();
    }
}
