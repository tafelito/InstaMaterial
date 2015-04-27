/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.froger.instamaterial;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class AnalystDetailActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final String TAG = AnalystDetailActivity.class.getSimpleName();
    private static boolean TOOLBAR_IS_STICKY = false;

    private Toolbar mToolbar;
    private View mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;
    private boolean mCanScrollToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyst_detail);

        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.style_color_primary);

        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText("Bruce Kasman");
        setTitle(null);
        mFab = findViewById(R.id.fab);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
//                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
//                mScrollView.scrollTo(0, 1);
//                mScrollView.scrollTo(0, 0);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "Go Home");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mImageView.getHeight();
        if (mImageView.getVisibility() == View.VISIBLE) {
            ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
            ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

            // Change alpha of overlay
            ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

            // Scale title text
            float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
            ViewHelper.setPivotX(mTitleView, 0);
            ViewHelper.setPivotY(mTitleView, 0);
            ViewHelper.setScaleX(mTitleView, scale);
            ViewHelper.setScaleY(mTitleView, scale);

            // Translate title text
            int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
            int titleTranslationY = maxTitleTranslationY - scrollY;
            if (TOOLBAR_IS_STICKY) {
                titleTranslationY = Math.max(0, titleTranslationY);
            }
            ViewHelper.setTranslationY(mTitleView, titleTranslationY);
        }

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        ViewHelper.setTranslationX(mFab, mImageView.getWidth() - mFabMargin - mFab.getWidth());
        ViewHelper.setTranslationY(mFab, fabTranslationY);

        // Show/hide FAB
        if (ViewHelper.getTranslationY(mFab) < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                Log.d(TAG,"Alpha 1");
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
//                ViewHelper.setTranslationY(mImageView, -mActionBarSize);
//                ViewHelper.setTranslationY(mOverlayView, -mActionBarSize);
//                mTitleView.setVisibility(View.GONE);
//                mImageView.setVisibility(View.GONE);
//                mOverlayView.setVisibility(View.GONE);
//                ViewPropertyAnimator.animate(mToolbar).cancel();
//                ViewPropertyAnimator.animate(mToolbar).translationY(-mActionBarSize).setDuration(200).start();
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

//                mToolbar.setTitle(mTitleView.getText());
//                mOverlayView.setVisibility(View.GONE);
//                mCanScrollToolbar = true;
//                TOOLBAR_IS_STICKY = false;
            } else {
                Log.d(TAG,"Alpha 0");
//                ViewHelper.setTranslationY(mToolbar, 0);
//                ViewHelper.setTranslationY(mImageView, 0);
//                ViewHelper.setTranslationY(mOverlayView, 0);
                /*ViewPropertyAnimator.animate(mToolbar).cancel();
                ViewPropertyAnimator.animate(mToolbar).translationY(0).setDuration(200).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                        mTitleView.setVisibility(View.VISIBLE);
                        mImageView.setVisibility(View.VISIBLE);
                        mOverlayView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

                    }
                }).start();*/
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
//              getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//                mToolbar.setTitle(null);
//                mTitleView.setVisibility(View.VISIBLE);
//                mOverlayView.setVisibility(View.VISIBLE);
//                mCanScrollToolbar = false;
//                TOOLBAR_IS_STICKY = true;
            }
        } else {
            // Translate Toolbar
            if (scrollY < flexibleRange) {
//                Log.d(getClass().getSimpleName(),"TranslationY 0");
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
//                Log.d(getClass().getSimpleName(),"TranslationY " + -scrollY);
                ViewHelper.setTranslationY(mToolbar, -scrollY);
//                ViewPropertyAnimator.animate(mToolbar).cancel();
//                ViewPropertyAnimator.animate(mToolbar).translationY(-mActionBarSize).setDuration(200).start();
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//        if (mCanScrollToolbar){
//            int toolbarHeight = mToolbar.getHeight();
//            if (scrollState == ScrollState.UP) {
//                if (toolbarHeight < mScrollView.getCurrentScrollY()) {
//                    ViewPropertyAnimator.animate(mToolbar).cancel();
//                    ViewPropertyAnimator.animate(mToolbar).translationY(-toolbarHeight).setDuration(200).start();
//                }
//            } else if (scrollState == ScrollState.DOWN) {
//                if (toolbarHeight < mScrollView.getCurrentScrollY()) {
//                    ViewPropertyAnimator.animate(mToolbar).cancel();
//                    ViewPropertyAnimator.animate(mToolbar).translationY(0).setDuration(200).start();
//                    mCanScrollToolbar = false;
//                }
//            }
//        }
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
}
