package cn.finalteam.rxgalleryfinal.ui.activity;

import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cn.finalteam.rxgalleryfinal.Configuration;
import cn.finalteam.rxgalleryfinal.R;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.di.component.ActivityFragmentComponent;
import cn.finalteam.rxgalleryfinal.di.component.DaggerActivityFragmentComponent;
import cn.finalteam.rxgalleryfinal.di.component.RxGalleryFinalComponent;
import cn.finalteam.rxgalleryfinal.di.module.ActivityFragmentModule;
import cn.finalteam.rxgalleryfinal.rxbus.RxBus;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.BaseResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.CloseRxMediaGridPageEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaCheckChangeEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.MediaViewPagerChangedEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.OpenMediaPageFragmentEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.OpenMediaPreviewFragmentEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.RequestStorageReadAccessPermissionEvent;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaGridFragment;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaPageFragment;
import cn.finalteam.rxgalleryfinal.ui.fragment.MediaPreviewFragment;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.OsCompat;
import cn.finalteam.rxgalleryfinal.utils.ThemeUtils;
import cn.finalteam.rxgalleryfinal.view.ActivityFragmentView;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Desction:
 * Author:pengjianbo
 * Date:16/5/7 上午10:01
 */
public class MediaActivity extends BaseActivity implements ActivityFragmentView {

    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;

    @Inject
    Configuration mConfiguration;
    @Inject
    MediaGridFragment mMediaGridFragment;
    MediaPageFragment mMediaPageFragment;
    MediaPreviewFragment mMediaPreviewFragment;

    private Toolbar mToolbar;
    private TextView mTvToolbarTitle;
    private TextView mTvOverAction;

    private List<MediaBean> mCheckedList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_media);

        findViews();
        setTheme();

        if(!mConfiguration.isRadio()) {
            mTvOverAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCheckedList != null && mCheckedList.size() > 0) {
                        BaseResultEvent event = new ImageMultipleResultEvent(mCheckedList);
                        RxBus.getDefault().post(event);
                        finish();
                    }
                }
            });
            mTvOverAction.setVisibility(View.VISIBLE);
        } else {
            mTvOverAction.setVisibility(View.GONE);
        }
        mCheckedList = new ArrayList<>();
        List<MediaBean> selectedList = mConfiguration.getSelectedList();
        if(selectedList != null && selectedList.size() > 0){
            mCheckedList.addAll(selectedList);
        }

        showMediaGridFragment();

        subscribeEvent();
    }

    @Override
    public void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mTvToolbarTitle = (TextView) findViewById(R.id.tv_toolbar_title);
        mTvOverAction = (TextView) findViewById(R.id.tv_over_action);
    }

    @Override
    protected void setTheme() {
        Drawable closeDrawable = ThemeUtils.resolveDrawable(this, R.attr.gallery_toolbar_close_image, R.drawable.gallery_default_toolbar_close_image);
        int closeColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_close_color, R.color.gallery_default_toolbar_widget_color);
        closeDrawable.setColorFilter(closeColor, PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(closeDrawable);

        int overButtonBg = ThemeUtils.resolveDrawableRes(this, R.attr.gallery_toolbar_over_button_bg);
        if(overButtonBg != 0) {
            mTvOverAction.setBackgroundResource(overButtonBg);
        } else {
            OsCompat.setBackgroundDrawableCompat(mTvOverAction, createDefaultOverButtonBgDrawable());
        }

        float overTextSize = ThemeUtils.resolveDimen(this, R.attr.gallery_toolbar_over_button_text_size, R.dimen.gallery_default_toolbar_over_button_text_size);
        mTvOverAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, overTextSize);

        int overTextColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_over_button_text_color, R.color.gallery_default_toolbar_over_button_text_color);
        mTvOverAction.setTextColor(overTextColor);

        float titleTextSize = ThemeUtils.resolveDimen(this, R.attr.gallery_toolbar_text_size, R.dimen.gallery_default_toolbar_text_size);
        mTvToolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);

        int titleTextColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_text_color, R.color.gallery_default_toolbar_text_color);
        mTvToolbarTitle.setTextColor(titleTextColor);

        int gravity = ThemeUtils.resolveInteger(this, R.attr.gallery_toolbar_text_gravity, R.integer.gallery_default_toolbar_text_gravity);
        mTvToolbarTitle.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT, gravity));

        int toolbarBg = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_bg, R.color.gallery_default_color_toolbar_bg);
        mToolbar.setBackgroundColor(toolbarBg);

        int toolbarHeight = (int) ThemeUtils.resolveDimen(this, R.attr.gallery_toolbar_height, R.dimen.gallery_default_toolbar_height);
        mToolbar.setMinimumHeight(toolbarHeight);

        int statusBarColor = ThemeUtils.resolveColor(this, R.attr.gallery_color_statusbar, R.color.gallery_default_color_statusbar);
        ThemeUtils.setStatusBarColor(statusBarColor, getWindow());

        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            backAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMediaGridFragment() {
        mMediaPreviewFragment = null;
        mMediaPageFragment = null;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mMediaGridFragment);
                if(mMediaPreviewFragment != null) {
                    ft.hide(mMediaPreviewFragment);
                }
                if(mMediaPageFragment != null){
                    ft.hide(mMediaPageFragment);
                }
        ft.show(mMediaGridFragment)
        .commit();

        if(mConfiguration.isImage()) {
            mTvToolbarTitle.setText(R.string.gallery_media_grid_image_title);
        } else {
            mTvToolbarTitle.setText(R.string.gallery_media_grid_video_title);
        }
    }

    @Override
    public void showMediaPageFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mMediaPageFragment = MediaPageFragment.newInstance();
        ft.add(R.id.fragment_container, mMediaPageFragment);
        mMediaPreviewFragment = null;
        ft.hide(mMediaGridFragment);
        ft.show(mMediaPageFragment);
        ft.commit();
    }

    @Override
    public void showMediaPreviewFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mMediaPreviewFragment = MediaPreviewFragment.newInstance();
        ft.add(R.id.fragment_container, mMediaPreviewFragment);
        mMediaPageFragment = null;
        ft.hide(mMediaGridFragment);
        ft.show(mMediaPreviewFragment);
        ft.commit();

        mTvToolbarTitle.setText(getString(R.string.gallery_page_title, 1, mCheckedList.size()));
    }

    @Override
    protected void setupComponent(RxGalleryFinalComponent rxGalleryFinalComponent) {
        ActivityFragmentComponent activityFragmentComponent = DaggerActivityFragmentComponent.builder()
            .rxGalleryFinalComponent(rxGalleryFinalComponent)
            .activityFragmentModule(new ActivityFragmentModule())
            .build();
        activityFragmentComponent.inject(this);
    }


    private void subscribeEvent() {
        Subscription subscriptionOpenMediaPreviewEvent = RxBus.getDefault().toObservable(OpenMediaPreviewFragmentEvent.class)
                .map(new Func1<OpenMediaPreviewFragmentEvent, OpenMediaPreviewFragmentEvent>() {
                    @Override
                    public OpenMediaPreviewFragmentEvent call(OpenMediaPreviewFragmentEvent mediaPreviewEvent) {
                        return mediaPreviewEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<OpenMediaPreviewFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenMediaPreviewFragmentEvent openMediaPreviewFragmentEvent) {
                        showMediaPreviewFragment();
                    }
                });

        RxBus.getDefault().add(subscriptionOpenMediaPreviewEvent);

        Subscription subscriptionMediaCheckChangeEvent = RxBus.getDefault().toObservable(MediaCheckChangeEvent.class)
                .map(new Func1<MediaCheckChangeEvent, MediaCheckChangeEvent>() {
                    @Override
                    public MediaCheckChangeEvent call(MediaCheckChangeEvent mediaCheckChangeEvent) {
                        return mediaCheckChangeEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<MediaCheckChangeEvent>() {
                    @Override
                    protected void onEvent(MediaCheckChangeEvent mediaCheckChangeEvent) {
                        MediaBean mediaBean = mediaCheckChangeEvent.getMediaBean();
                        if(mCheckedList.contains(mediaBean)) {
                            mCheckedList.remove(mediaBean);
                        } else {
                            mCheckedList.add(mediaBean);
                        }

                        if(mCheckedList.size() > 0){
                            String text = getResources().getString(R.string.gallery_over_button_text_checked, mCheckedList.size(), mConfiguration.getMaxSize());
                            mTvOverAction.setText(text);
                            mTvOverAction.setEnabled(true);
                        } else {
                            mTvOverAction.setText(R.string.gallery_over_button_text);
                            mTvOverAction.setEnabled(false);
                        }
                    }
                });
        RxBus.getDefault().add(subscriptionMediaCheckChangeEvent);

        Subscription subscriptionMediaViewPagerChangedEvent = RxBus.getDefault().toObservable(MediaViewPagerChangedEvent.class)
                .map(new Func1<MediaViewPagerChangedEvent, MediaViewPagerChangedEvent>() {
                    @Override
                    public MediaViewPagerChangedEvent call(MediaViewPagerChangedEvent mediaViewPagerChangedEvent) {
                        return mediaViewPagerChangedEvent;
                    }
                })
                .subscribe(new RxBusSubscriber<MediaViewPagerChangedEvent>() {
                    @Override
                    protected void onEvent(MediaViewPagerChangedEvent mediaPreviewViewPagerChangedEvent) {
                        int curIndex = mediaPreviewViewPagerChangedEvent.getCurIndex();
                        int totalSize = mediaPreviewViewPagerChangedEvent.getTotalSize();
                        String title = getString(R.string.gallery_page_title, curIndex + 1, totalSize);
                        mTvToolbarTitle.setText(title);
                    }
                });
        RxBus.getDefault().add(subscriptionMediaViewPagerChangedEvent);

        Subscription subscriptionOpenMediaPageFragmentEvent = RxBus.getDefault().toObservable(OpenMediaPageFragmentEvent.class)
                .subscribe(new RxBusSubscriber<OpenMediaPageFragmentEvent>() {
                    @Override
                    protected void onEvent(OpenMediaPageFragmentEvent openMediaPageFragmentEvent) throws Exception {
                        showMediaPageFragment();
                    }
                });
        RxBus.getDefault().add(subscriptionOpenMediaPageFragmentEvent);

        Subscription subscriptionCloseRxMediaGridPageEvent = RxBus.getDefault().toObservable(CloseRxMediaGridPageEvent.class)
                .subscribe(new RxBusSubscriber<CloseRxMediaGridPageEvent>() {
                    @Override
                    protected void onEvent(CloseRxMediaGridPageEvent closeRxMediaGridPageEvent) throws Exception {
                        finish();
                    }
                });
        RxBus.getDefault().add(subscriptionCloseRxMediaGridPageEvent);
    }

    public List<MediaBean> getCheckedList() {
        return mCheckedList;
    }

    private void backAction() {
        if((mMediaPreviewFragment != null && mMediaPreviewFragment.isVisible())
                || (mMediaPageFragment != null &&mMediaPageFragment.isVisible())){
            showMediaGridFragment();
            return;
        }
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            backAction();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().removeAllStickyEvents();
        RxBus.getDefault().clear();
    }

    private StateListDrawable createDefaultOverButtonBgDrawable() {
        int dp12 = (int) ThemeUtils.applyDimensionDp(this, 12.f);
        int dp8 = (int) ThemeUtils.applyDimensionDp(this, 8.f);
        float dp4 = ThemeUtils.applyDimensionDp(this, 4.f);
        float[] round = new float[] { dp4, dp4, dp4, dp4, dp4, dp4, dp4, dp4 };
        ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        pressedDrawable.setPadding(dp12, dp8, dp12, dp8);
        int pressedColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_over_button_pressed_color, R.color.gallery_default_toolbar_over_button_pressed_color);
        pressedDrawable.getPaint().setColor(pressedColor);

        int normalColor = ThemeUtils.resolveColor(this, R.attr.gallery_toolbar_over_button_normal_color, R.color.gallery_default_toolbar_over_button_normal_color);
        ShapeDrawable normalDrawable = new ShapeDrawable(new RoundRectShape(round, null, null));
        normalDrawable.setPadding(dp12, dp8, dp12, dp8);
        normalDrawable.getPaint().setColor(normalColor);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
        stateListDrawable.addState(new int[]{}, normalDrawable);

        return stateListDrawable;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.i("onRequestPermissionsResult:requestCode="+requestCode +" permissions=" + permissions[0]);
        switch (requestCode) {
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    RxBus.getDefault().post(new RequestStorageReadAccessPermissionEvent(true));
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}