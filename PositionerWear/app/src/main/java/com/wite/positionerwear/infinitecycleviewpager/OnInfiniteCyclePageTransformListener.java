package com.wite.positionerwear.infinitecycleviewpager;

import android.view.View;


public interface OnInfiniteCyclePageTransformListener {
    void onPreTransform(final View page, final float position);

    void onPostTransform(final View page, final float position);
}