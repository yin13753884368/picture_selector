package com.selector.picture.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.selector.picture.R;
import com.selector.picture.activity.PhotoEditActivity;
import com.selector.picture.adapter.PhotoEditAdapter;
import com.selector.picture.base.BaseActivity;
import com.selector.picture.constant.Constant;
import com.selector.picture.model.ColorModel;
import com.selector.picture.model.PicConfig;
import com.selector.picture.utils.Function;
import com.selector.picture.utils.OnItemClickListener;
import com.selector.picture.utils.UIUtils;

import java.util.ArrayList;

/**
 * 编辑文字弹框
 * Create by Han on 2019/5/22
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/yin13753884368
 */
public class DialogEditTextUtils implements View.OnClickListener, OnItemClickListener<ColorModel> {
    private Context mContext;
    private Function<Boolean> function;
    private AlertDialog dialog;
    private EditText editText;
    private PhotoEditAdapter adapter;
    private ArrayList<ColorModel> list;//画笔颜色的结合
    private ImageView ivPencileBold;

    public DialogEditTextUtils(Context context, Function<Boolean> function) {
        this.mContext = context;
        this.function = function;
        initDialog();
    }

    private void initDialog() {
        dialog = new AlertDialog
                .Builder(mContext)
                .setCancelable(false)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (function != null) {
                            function.action(true);
                        }
                    }
                })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                            dissMiss();
                        }
                        return false;
                    }
                }).create();
        initView();
    }

    private void initView() {
        show();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.getContext().setTheme(PicConfig.getInstances().getTheme());
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                window.setContentView(R.layout.dialog_edit_text);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                window.setBackgroundDrawable(new ColorDrawable());
                window.setGravity(Gravity.CENTER);
                window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);//软键盘弹出
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                TextView rlEditTopCancel = window.findViewById(R.id.tv_edit_top_cancel);//顶部取消按钮
                TextView rlEditTopComplete = window.findViewById(R.id.tv_edit_top_complete);//顶部完成按钮
                RecyclerView ryEditPencileBottom = window.findViewById(R.id.ry_edit_pencile_bottom);//底部RecyclerView 展示颜色
                editText = window.findViewById(R.id.edit_text);
                ivPencileBold = window.findViewById(R.id.iv_pencile_bold);
                RelativeLayout rlPencileBold = window.findViewById(R.id.rl_pencile_bold);
                rlEditTopCancel.setOnClickListener(this);
                rlEditTopComplete.setOnClickListener(this);
                rlPencileBold.setOnClickListener(this);
                ivPencileBold.setSelected(false);
                list = new ArrayList<>();
                adapter = new PhotoEditAdapter((PhotoEditActivity) mContext, this, list);
                LinearLayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                ryEditPencileBottom.setAdapter(adapter);
                ryEditPencileBottom.setLayoutManager(manager);
                initColorList();
                UIUtils.openBroad(mContext, editText);
            }
        }
    }

    /**
     * 弹框消失
     */
    private void dissMiss() {
        if (dialog != null) {
            dialog.dismiss();
            UIUtils.closeBroad(mContext, editText);
        }
    }

    /**
     * 弹框显示
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 初始化画笔颜色,默认第一个缩放
     */
    private void initColorList() {
        int[] paintColor = PicConfig.getInstances().getEditPaintColor();
        if (paintColor == null) {
            paintColor = Constant.PIC_EDIT_PAINT_COLOR;
        }
        for (int i = 0; i < paintColor.length; i++) {
            if (i == 0) {
                list.add(new ColorModel(mContext.getResources().getColor(paintColor[i]), true));
            } else {
                list.add(new ColorModel(mContext.getResources().getColor(paintColor[i]), false));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.tv_edit_top_cancel:
                    //取消
                    dissMiss();
                    break;
                case R.id.tv_edit_top_complete:
                    //完成
                    dissMiss();
                    break;
                case R.id.rl_pencile_bold:
                    ivPencileBold.setSelected(!ivPencileBold.isSelected());
                    break;
            }
        }
    }

    @Override
    public void onItemClick(ColorModel colorModel) {
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ColorModel model = list.get(i);
                if (model != null) {
                    model.reductionCoefficient();
                }
            }
        }
        colorModel.setScaleCoefficient();
        adapter.notifyDataSetChanged();
    }
}