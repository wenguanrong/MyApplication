package com.example.administrator.myapplication.c.chapter02;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.administrator.myapplication.R;

public class SetUp1Activity extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        initView();
    }
    private void initView() {
        ((RadioButton) findViewById(R.id.rb_first)).setChecked(true);
    }
    @Override
    public void showNext() {
        startActivityAndFinishSelf(SetUp2Activity.class);
    }



    @Override
    public void showPre() {
        Toast.makeText(this,"当前页面已经是第一页",Toast.LENGTH_SHORT).show();
    }


}
