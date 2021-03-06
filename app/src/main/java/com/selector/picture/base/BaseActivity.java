package com.selector.picture.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.selector.picture.R;
import com.selector.picture.utils.UIUtils;
import com.selector.picture.view.DialogLoading;
import com.selector.picture.view.StatusBarUtil;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Create by Han on 2019/5/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/yin13753884368
 */
public abstract class BaseActivity extends AppCompatActivity {


    protected abstract void setThem();//设置主题

    protected abstract void initView(Bundle savedInstanceState);//初始化view

    protected abstract void initData();//初始化数据

    protected BaseActivity mActivity;//初始化数据

    protected DialogLoading dialog;//加载框

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setThem();
        mActivity = BaseActivity.this;
        setContentView(R.layout.activity_base);
        initView(savedInstanceState);
        initData();
        dialog = new DialogLoading(mActivity);
        StatusBarUtil.changeStatusBar(this, false);
        CrashReport.initCrashReport(getApplicationContext(), "e3ef5afa9a", false);
    }

    /**
     * 打开加载框
     */
    public void loadingProgressDialog() {
        if (dialog != null && mActivity != null && !mActivity.isDestroyed()) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
        }
    }

    /**
     * 关闭加载框
     */
    public void cancelProgressDialog() {
        if (dialog != null && mActivity != null && !mActivity.isDestroyed()) {
            if (dialog.isShowing()) {
                dialog.dissMiss();
            }
        }
    }

}
