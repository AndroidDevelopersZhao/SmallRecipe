package com.cn.smallrecipe;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Administrator on 2016/2/23.
 */
public class MyActivity extends AppCompatActivity {
    public static final String TAG = "SmallRecipe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Test");
    }
}
