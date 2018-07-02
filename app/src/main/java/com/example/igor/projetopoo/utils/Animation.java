package com.example.igor.projetopoo.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

import com.mancj.materialsearchbar.MaterialSearchBar;

public class Animation {
    public static final int ANIMATION_DURATION_SHORTEST = 150;
    public static final int ANIMATION_DURATION_SHORT = 250;
    public static final int ANIMATION_DURATION_MEDIUM = 400;
    public static final int ANIMATION_DURATION_LONG = 800;
    public static final String TITLE_SP = "SP";

    @TargetApi(21)
    public static void circleRevealView(View view, int duration) {
        // get the center for the clipping circle
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

        if (duration > 0) {
            anim.setDuration(duration);
        }
        else {
            anim.setDuration(ANIMATION_DURATION_SHORT);
        }

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    @TargetApi(21)
    public static void circleRevealView(View view) {
        circleRevealView(view,ANIMATION_DURATION_SHORT);
    }

    @TargetApi(21)
    public static void circleHideView(final View view, AnimatorListenerAdapter listenerAdapter) {
        circleHideView(view,ANIMATION_DURATION_SHORT,listenerAdapter);
    }

    @TargetApi(21)
    public static void circleHideView(final View view, int duration, AnimatorListenerAdapter listenerAdapter) {
        // get the center for the clipping circle
        int cx = view.getWidth();
        int cy = view.getHeight() / 2;

        // get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(listenerAdapter);

        if (duration > 0) {
            anim.setDuration(duration);
        }
        else {
            anim.setDuration(ANIMATION_DURATION_SHORT);
        }

//        anim.setStartDelay(200);

        // start the animation
        anim.start();
    }

    public static void fadeInView(View view) {
        fadeInView(view, ANIMATION_DURATION_SHORTEST);
    }

    public static void fadeInView(View view, int duration) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);

        // Setting the listener to null, so it won't keep getting called.
        ViewCompat.animate(view).alpha(1f).setDuration(duration).setListener(null);
    }

    public static void fadeOutView(View view) {
        fadeOutView(view, ANIMATION_DURATION_SHORTEST);
    }

    public static void fadeOutView(final View view, int duration) {
        ViewCompat.animate(view).alpha(0f).setDuration(duration).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {
                view.setDrawingCacheEnabled(true);
            }

            @Override
            public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
                view.setAlpha(1f);
                view.setDrawingCacheEnabled(false);
            }

            @Override
            public void onAnimationCancel(View view) { }
        });
    }

    public static void crossFadeViews(View showView, View hideView) {
        crossFadeViews(showView, hideView, ANIMATION_DURATION_SHORT);
    }

    public static void crossFadeViews(View showView, final View hideView, int duration) {
        fadeInView(showView, duration);
        fadeOutView(hideView, duration);
    }

    public static void openSearch(final MaterialSearchBar search, ActionBar actionBar, FrameLayout layout) {
        search.setVisibility(View.VISIBLE);

        circleRevealView(search, ANIMATION_DURATION_MEDIUM);

        search.postDelayed(new Runnable() {
            @Override
            public void run() {
                search.enableSearch();
            }
        }, ANIMATION_DURATION_MEDIUM);

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        TransitionDrawable background = (TransitionDrawable) layout.getBackground();
        background.startTransition(1000);
    }

    public static void closeSearch(final MaterialSearchBar search, ActionBar actionBar, FrameLayout layout) {
        search.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleHideView(search, ANIMATION_DURATION_MEDIUM, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
//                        search.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }, ANIMATION_DURATION_SHORT);
        search.postDelayed(new Runnable() {
            @Override
            public void run() {
                search.setVisibility(View.INVISIBLE);
            }
        }, 350);
        //search.setVisibility(View.INVISIBLE);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final TransitionDrawable background = (TransitionDrawable) layout.getBackground();
        background.reverseTransition(1000);


    }
}