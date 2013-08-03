package com.gpse.abc;


/**
 * Created by bo on 7/31/13.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MagCard extends Fragment {
    static private String TAG = "MagCard";
    private Context mContext;
    private MyApplication myApplication;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private StringBuffer gBuffer;
    private long updateBuffer;
    private long mUpdateBuffer;
    private String mBuffer;
    private Thread mReadThread;
    private TextView mText;
    private Button mClearButton;

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {

                updateBuffer = myApplication.getUpdateBuffer();

                if (gBuffer == null)
                    return;

                try {
                    Thread.sleep(20);
//                    Log.d(TAG, ": " + gBuffer.toString() + " : " + updateBuffer + " : " + mUpdateBuffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (updateBuffer > mUpdateBuffer) {
                    mUpdateBuffer = updateBuffer;
                    if (gBuffer.charAt(9) == '1')
                        onDataReceived(gBuffer.toString());
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        myApplication = (MyApplication) getActivity().getApplication();

        try {
            mSerialPort = myApplication.getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        gBuffer = myApplication.getBuffer();
        updateBuffer = myApplication.getUpdateBuffer();

//        mReadThread = new ReadThread();
//        mReadThread.start();


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Mag Card onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Mag card onResume");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.mag_card, container, false);
        mText = (TextView) view.findViewById(R.id.textView);

        mClearButton = (Button) view.findViewById(R.id.clear_text);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clear text");
                if (mText != null)
                    mText.setText("");
            }
        });

        return view;
    }

    public void onDataReceived(final String string) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mText != null) {
                    Log.d(TAG, "onDataReceived: " + string);
                    mText.setText(string);
                }
            }
        });
    }
}
