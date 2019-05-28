package com.selector.picture.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.selector.picture.R;
import com.selector.picture.activity.PhotoSelectActivity;
import com.selector.picture.adapter.GridItemDecoration;
import com.selector.picture.adapter.GridPicAdapter;
import com.selector.picture.base.BaseFragment;
import com.selector.picture.constant.Constant;
import com.selector.picture.model.LocalMedia;
import com.selector.picture.model.LocalMediaFolder;
import com.selector.picture.model.PicConfig;
import com.selector.picture.model.PicSelector;
import com.selector.picture.utils.DateUtils;
import com.selector.picture.utils.OnItemClickListener;
import com.selector.picture.utils.StringUtils;
import com.selector.picture.utils.UIUtils;
import com.selector.picture.view.DialogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 图片选择的fragment
 * Create by Han on 2019/5/10
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/yin13753884368
 */
public class PhotoSelectFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener<LocalMedia> {

    private List<LocalMedia> list;
    private GridPicAdapter adapter;
    private FragmentActivity activity;
    private TextView tvBottomLeftText;
    private TextView tvBottomCenterText;
    private List<LocalMediaFolder> localMediaFolders;//本地所有数据的集合
    private ArrayList<LocalMedia> sendMedia;//发送和预览的集合
    private TextView tvTopSendText;
    private TextView tvBottomPreviewText;
    private TextView tvTopSlideLeftText;

    @Override
    protected int initView() {
        return R.layout.fragment_photo_select;
    }

    @Override
    protected void initData() {
        View view = getView();
        RelativeLayout rlTopRoot = view.findViewById(R.id.rl_top_root);//顶部根布局
        ImageView ivTopLeftBack = view.findViewById(R.id.iv_top_left_back);//顶部左侧后退按钮
        TextView tvTopLeftText = view.findViewById(R.id.tv_top_lef_text);//顶部左侧标题
        tvTopSendText = view.findViewById(R.id.tv_top_send_text); //顶部右侧发送按钮
        tvTopSlideLeftText = view.findViewById(R.id.tv_top_slide_lef_text); //recyclerview 滑动时显示的提示框
        RecyclerView ry = view.findViewById(R.id.ry);//recyclerview
        RelativeLayout rlBottomRoot = view.findViewById(R.id.rl_bottom_root);//底部根布局
        LinearLayout llBottomLeftText = view.findViewById(R.id.ll_bottom_lef_text);//底部底部左侧标题根布局
        tvBottomLeftText = view.findViewById(R.id.tv_bottom_lef_text);//底部左侧标题
        tvBottomCenterText = view.findViewById(R.id.tv_bottom_center_text);//底部中间原图标题
        tvBottomPreviewText = view.findViewById(R.id.tv_bottom_preview_text);//底部右侧预览按钮

        activity = getActivity();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, PicConfig.getInstances().getGridSize(), GridLayoutManager.VERTICAL, false);
        ry.setPadding(UIUtils.dp2px(activity, Constant.PIC_GRID_SPACE), 0, 0, 0);
        ry.setLayoutManager(gridLayoutManager);
        ry.addItemDecoration(new GridItemDecoration());
        list = new ArrayList<>();
        adapter = new GridPicAdapter(activity, PhotoSelectFragment.this, this, list);
        ry.setAdapter(adapter);
        ry.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    tvTopSlideLeftText.setVisibility(View.VISIBLE);
                } else {
                    tvTopSlideLeftText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑动状态
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    int visibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    if (list != null) {
                        LocalMedia media = list.get(visibleItemPosition);
                        if (media != null) {
                            String addedTime = StringUtils.nullToString(media.getAddedTime());
                            String timeParse = DateUtils.slideTimeParse(activity, Long.valueOf(addedTime) * Constant.PIC_UNITS_SECONDS);
                            tvTopSlideLeftText.setText(timeParse);
                        }
                    }
                }
            }
        });
        sendMedia = new ArrayList<>();
        ivTopLeftBack.setOnClickListener(this);
        tvTopSendText.setOnClickListener(this);
        llBottomLeftText.setOnClickListener(this);
        tvBottomCenterText.setOnClickListener(this);
        tvBottomPreviewText.setOnClickListener(this);
        initBottomCenterText();
    }

    /**
     * 设置原图  默认false
     */
    private void initBottomCenterText() {
        tvBottomCenterText.setSelected(PicConfig.getInstances().isLoadOriginalImage());
    }

    /**
     * 设置配置
     *
     * @param picSelector PicSelector
     */
    public void setConfiguration(PicSelector picSelector) {

    }

    /**
     * 加载图片数据
     *
     * @param localMedia List<LocalMediaFolder>
     */
    public void setList(List<LocalMediaFolder> localMedia) {
        this.localMediaFolders = localMedia;
        if (localMediaFolders != null && localMediaFolders.size() > 0) {
            LocalMediaFolder mediaFolders = localMediaFolders.get(0);
            refreshPic(mediaFolders);
        }
    }

    /**
     * 刷新列表数据
     *
     * @param mediaFolders LocalMediaFolder
     */
    private void refreshPic(LocalMediaFolder mediaFolders) {
        if (localMediaFolders != null && localMediaFolders.size() > 0) {
            for (LocalMediaFolder media : localMediaFolders) {
                if (media != null) {
                    media.setChecked(false);
                }
            }
        }
        list.clear();
        if (mediaFolders != null) {
            tvBottomLeftText.setText(StringUtils.nullToString(mediaFolders.getName()));
            mediaFolders.setChecked(true);
            list.addAll(mediaFolders.getImages());
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.iv_top_left_back:
                    if (activity != null) {
                        activity.finish();
                    }
                    break;
                case R.id.tv_top_send_text:
                    //顶部右侧发送按钮
                    setResult();
                    break;
                case R.id.ll_bottom_lef_text:
                    //弹框选择相册
                    new DialogUtils(activity, localMediaFolders, new OnItemClickListener<LocalMediaFolder>() {
                        @Override
                        public void onItemClick(LocalMediaFolder localMediaFolder) {
                            refreshPic(localMediaFolder);
                        }
                    });
                    break;
                case R.id.tv_bottom_center_text:
                    //是否选择原图
                    PicConfig.getInstances().setLoadOriginalImage(!PicConfig.getInstances().isLoadOriginalImage());
                    initBottomCenterText();
                    break;
                case R.id.tv_bottom_preview_text:
                    //底部右侧预览按钮

                    break;

            }
        }
    }

    @Override
    public void onItemClick(LocalMedia localMedia) {
        if (localMedia != null && sendMedia != null) {
            boolean checked = localMedia.isChecked();
            Iterator<LocalMedia> iterator = sendMedia.iterator();
            while (iterator.hasNext()) {
                LocalMedia nextMedia = iterator.next();
                if (nextMedia != null) {
                    if (TextUtils.equals(nextMedia.getId(), localMedia.getId())) {
                        if (!checked) {
                            iterator.remove();
                        }
                    }
                }
            }
            if (checked) {
                sendMedia.add(localMedia);
            }
        }
        setText();
    }


    /**
     * 设置发送和预览按钮状态
     */
    private void setText() {
        if (sendMedia != null && sendMedia.size() > 0) {
            setSelected(true);
            tvTopSendText.setText(getString(R.string.picture_selector_top_send_text_select, String.valueOf(sendMedia.size()), String.valueOf(PicConfig.getInstances().getMaxSelectNum())));
            tvBottomPreviewText.setText(getString(R.string.picture_selector_bottom_preview_text, String.valueOf(sendMedia.size())));
        } else {
            setSelected(false);
            tvTopSendText.setText(R.string.picture_selector_top_send_text_default);
            tvBottomPreviewText.setText(R.string.picture_selector_bottom_preview_default);
        }
    }

    /**
     * 设置view 选中
     *
     * @param isSelected true选中  false未选中
     */
    public void setSelected(boolean isSelected) {
        tvTopSendText.setSelected(isSelected);
        tvBottomPreviewText.setSelected(isSelected);
    }


    /**
     * 选择后返回数据
     */
    private void setResult() {
        if (sendMedia != null && sendMedia.size() > 0) {
            if (activity != null) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (LocalMedia media : sendMedia) {
                    if (media != null) {
                        arrayList.add(StringUtils.nullToString(media.getPath()));
                    }
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra(Constant.PIC_INTENT_ACTIVITY_KEY, arrayList);
                ((PhotoSelectActivity) activity).setResult(intent);
            }
        }
    }

    /**
     * 获取选中的集合
     *
     * @return List<LocalMedia>
     */
    public List<LocalMedia> getSendMedia() {
        return sendMedia;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("fragment----", "onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.e("fragment----", "onRestoreInstanceState");
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}