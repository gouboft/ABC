package com.gpse.abc;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CardReader extends FragmentActivity {
    static private String TAG = "CardReader";

    private MyApplication myApplication;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private Thread mReadThread;

    private long updateBuffer;
    private StringBuffer mBuffer;

    private MagCard magFragment;

    private Loading mLoadingView;
    private TabHost mTabHost;
    private static int currentlayout = 0;


    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int count = 10;
                int readCount = 0;
                try {
                    final byte[] buffer = new byte[count];
                    if (mInputStream == null) {
                        return;
                    }

                    while ((readCount < count) && (buffer[readCount == 0 ? 0 : readCount - 1] != '&')) {
                        readCount += mInputStream.read(buffer, readCount, count - readCount);
                        if (buffer[0] != '1' && buffer[0] != '2' && buffer[0] != '3')
                            readCount = 0;
                        Log.d(TAG, "readCount: " + readCount
                                + " updateBuffer: " + myApplication.getUpdateBuffer()
                                + " buffer: " + new String(buffer, 0, readCount));
                    }

                    myApplication.setUpdateBuffer(++updateBuffer);
                    //Auto identify the card tpye
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (buffer[0] == '1') {
                                mTabHost.setCurrentTab(0);
                            } else if (buffer[0] == '2') {
                                mTabHost.setCurrentTab(1);
                            } else if (buffer[0] == '3') {
                                mTabHost.setCurrentTab(2);
                            } else {
                                mTabHost.setCurrentTab(0);
                            }
                        }
                    });
                    magFragment.onDataReceived(new String(buffer, 0, readCount));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApplication = (MyApplication) getApplication();

        try {
            mSerialPort = myApplication.getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        mBuffer = myApplication.getBuffer();
        updateBuffer = myApplication.getUpdateBuffer();
        magFragment = (MagCard) getSupportFragmentManager().findFragmentById(R.id.mag_card);
        mReadThread = new ReadThread();
        mReadThread.start();

/*
        mLoadingView = (Loading) findViewById(R.id.imageview);

        new Thread() {
            @Override
            public void run() {
                mLoadingView.startLoading();
            }
        }.start();
*/

        setFragment();
        changeLayout();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        myApplication.closeSerialPort();
        finish();
    }

    private void changeLayout() {
        mTabHost.setCurrentTab(currentlayout);
    }

    private void setFragment() {
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec("mag_card")
                .setIndicator("磁卡")
                .setContent(R.id.mag_card));
        mTabHost.addTab(mTabHost.newTabSpec("ic_card")
                .setIndicator("接触式 IC 卡")
                .setContent(R.id.ic_card));
        mTabHost.addTab(mTabHost.newTabSpec("rf_card")
                .setIndicator("非接触式 IC 卡")
                .setContent(R.id.rf_card));
        updateTabBackground(mTabHost);

        mTabHost.setCurrentTab(0);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                updateTabBackground(mTabHost);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Log.d(TAG, "settings clicked");
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SerialPortPreferences.class));
                break;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateTabBackground(final TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View v = tabHost.getTabWidget().getChildAt(i);
            if (tabHost.getCurrentTab() == i) {
                v.setBackground(getResources().getDrawable(android.R.drawable.spinner_background));
            } else {
                v.setBackground(getResources().getDrawable(android.R.drawable.spinner_dropdown_background));
            }
        }
    }
}
