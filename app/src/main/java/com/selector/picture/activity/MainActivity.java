package com.selector.picture.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.selector.picture.R;
import com.selector.picture.base.BaseActivity;
import com.selector.picture.model.PicSelector;
import com.selector.picture.fragment.PictureSelectorFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private PictureSelectorFragment pictureSelectorFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        View tvSelectPic = findViewById(R.id.tv_select_pic);
        tvSelectPic.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_pic:
                picSelect();
                break;
            default:
                break;
        }
    }


    private void picSelect() {
        PicSelector.create(MainActivity.this)
                .minSelectNum(1)
                .maxSelectNum(9)
                .gridSize(3)
                .theme(R.style.pictrue_white_Theme)
                .setResult(10);
    }
}
