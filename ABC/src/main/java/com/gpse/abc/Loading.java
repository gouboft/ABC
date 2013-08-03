package com.gpse.abc;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by bo on 7/30/13.
 */
public class Loading extends ImageView implements Runnable {
    private static final String TAG = "Loading";
    private boolean isStop = false;
    private int image = R.drawable.logo;

    public Loading(Context context) {
        this(context, null);
    }

    public Loading(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isStop = true;
        Log.d(TAG, "onDetachedFromWindow");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setImageResource(image);
    }

    @Override
    public void run() {
        while (!isStop) {
            Log.d(TAG, "is running~");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startLoading() {
        new Thread(this).start();
    }
}
