package com.hw.autotest.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hw.autotest.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FlashUserData extends Activity implements Handler.Callback {
    private static final String TAG = "FlashUserData";
    private static final String[] FLASH_DATA_DIR = {Environment.getExternalStorageDirectory().getAbsolutePath()+"/flashtmp", "/data/flashtmp"};
    private static final String FILE_NAME_TEMPLATE = "flashbin";

    private static final int K_SIZE = 1024;
    private static final int WRITE_FILE_SIZE = 1024 * 1024;
    private static final int MIN_BLOCK_COUNT = 1024;
    private static final int STATE_DISABLE = 0;
    private static final int STATE_IDLE = 1;
    private static final int STATE_WORK = 2;
    private static final int MSG_UPDATE_UI = 1;
    private static final long MAX_FLASH_BUFF = 512 * 1024 * 1024;

    private boolean mIsRunning = false;
    private int mState = 1;
    private int mCurrItem = 0;

    private String mLastErrorInfo = "";
    private String[] mRootDir = {Environment.getExternalStorageDirectory().getAbsolutePath(), "/data"};

    private TextView mDataSpace = null;
    private TextView mSdcardSpace = null;
    private TextView mErrorInfo = null;
    private Button mBtnStart = null;
    private Button mBtnStop = null;
    private Handler mHandler = new Handler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flash_userdata);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mDataSpace = (TextView) findViewById(R.id.flash_data_spaceInfo);
        mSdcardSpace = (TextView) findViewById(R.id.flash_sdcard_spaceInfo);

        mErrorInfo = (TextView) findViewById(R.id.flash_error_info);
        mBtnStart = (Button) findViewById(R.id.btnStart);
        mBtnStop = (Button) findViewById(R.id.btnStop);

        binderBtnStart();
        bindBtnStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initParameters();
        updateUIInfo();
    }

    @Override
    protected void onStop() {
        if (mIsRunning) {
            mIsRunning = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        clearMessage();

        super.onStop();
    }

    private void binderBtnStart() {
        mBtnStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mLastErrorInfo = "";
                mState = STATE_WORK;
                mIsRunning = true;
                updateUIInfo();
                doThread();
                
            }
        });
    }

    private void bindBtnStop() {
        mBtnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mHandler.hasMessages(MSG_UPDATE_UI)) {
                    mHandler.removeMessages(MSG_UPDATE_UI);
                }
                mIsRunning = false;
                mBtnStop.setEnabled(false);
            }
        });
    }

    private void initParameters() {
        mState = STATE_IDLE;
        mLastErrorInfo = "";
        mCurrItem = 0;
        int noSpace = 0;
        for(int i = 0; i < FLASH_DATA_DIR.length; i++) {
            String dirPath = FLASH_DATA_DIR[i];

            File dir = new File(dirPath);
            if(!dir.exists()) {
                boolean newDir = dir.mkdirs();
                if(!newDir) {
                    noSpace++;
                    continue;
                } else {
                    mRootDir[i] = dirPath;
                }
            }

            if (getAvailableSize(mRootDir[i]) < MIN_BLOCK_COUNT) {
                noSpace++;
            }
        }

        if(noSpace == FLASH_DATA_DIR.length) {
            mState = STATE_DISABLE;
            Toast.makeText(this, "available space low!", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUIInfo() {
        mSdcardSpace.setText(getSpaceInfo(mRootDir[0]));
        mDataSpace.setText(getSpaceInfo(mRootDir[1]));

        mErrorInfo.setText(mLastErrorInfo);

        if ((mState & STATE_IDLE) > 0) {
            if (!mBtnStart.isEnabled()) {
                mBtnStart.setEnabled(true);
            }
        } else {
            if (mBtnStart.isEnabled()) {
                mBtnStart.setEnabled(false);
            }
        }

        if ((mState & STATE_WORK) > 0) {
            if (!mBtnStop.isEnabled()) {
                mBtnStop.setEnabled(true);
            }
        } else {
            if (mBtnStop.isEnabled()) {
                mBtnStop.setEnabled(false);
            }
        }
    }

    private void doThread() {
        Thread threadrun = new Thread() {
            public void run() {
                mCurrItem = 0;
                byte[] buffer = new byte[WRITE_FILE_SIZE];
                String dirName = mRootDir[mCurrItem];
                String fileName = getFileName();
                File file = new File(fileName);
                FileOutputStream fos = null;
                Log.i(TAG,dirName);
                long tmpCount = 0;
                int availableSize = 0;
                int writeSize = 0;
                while (mIsRunning) {
                    try {
                        availableSize = getAvailableSize(dirName);
                        
                        if ((availableSize <= MIN_BLOCK_COUNT) || (tmpCount >= MAX_FLASH_BUFF)) {
                            fos = null;
                            if(availableSize <= MIN_BLOCK_COUNT) {
                                if(mCurrItem == 0) {
                                    mCurrItem = 1;
                                    dirName = mRootDir[mCurrItem];
                                } else {
                                    mIsRunning = false;
                                }
                            }
                            fileName = getFileName();
                            file = new File(fileName);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException te) {

                            }

                            tmpCount = 0;
                        } else {
                            if (null == fos) {
                                fos = new FileOutputStream(file, true);
                            }

                            if(availableSize - MIN_BLOCK_COUNT < WRITE_FILE_SIZE / K_SIZE) {
                                Log.d(TAG, "low space");
                                writeSize = Math.min((availableSize - MIN_BLOCK_COUNT + 8) * K_SIZE, WRITE_FILE_SIZE);
                                if(writeSize < 0) {
                                    writeSize = 8 * K_SIZE;
                                }
                            } else {
                                writeSize = WRITE_FILE_SIZE;
                            }

                            fos.write(buffer, 0, writeSize);
                            fos.flush();

                            tmpCount += writeSize;
                        }
                    } catch (IOException e) {
                        handleError(e.toString());
                    } catch(Exception e1) {
                        handleError(e1.toString());
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException innerE) {
                            }

                            fos = null;
                        }
                    }

                    if (mIsRunning) {
                        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    }
                }
                clearMessage();
                mState = STATE_IDLE;
                mHandler.sendEmptyMessage(MSG_UPDATE_UI);
            }
        };

        threadrun.start();

    }

    private void handleError(String msg) {
        mIsRunning = false;
        mLastErrorInfo = msg;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    private String getSpaceInfo(String path) {
        StatFs statFs = new StatFs(path);
        int availableCnt = statFs.getAvailableBlocks();
        int blockCount = statFs.getBlockCount();
        int blockSize = statFs.getBlockSize();
        long availableSize = (long) availableCnt * (long) blockSize / K_SIZE;
        long totalSize = (long) blockCount * (long) blockSize / K_SIZE;
        return String.valueOf(availableSize) + "K / " + String.valueOf(totalSize) + "K";
    }

    private int getAvailableSize(String path) {
        StatFs statFs = new StatFs(path);
        int availableCnt = statFs.getAvailableBlocks();
        int blockSize = statFs.getBlockSize();
        statFs = null;
        return (int) ((long) availableCnt * (long) blockSize / K_SIZE);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case MSG_UPDATE_UI:
            updateUIInfo();
            break;

        default:
            return false;
        }

        return true;
    }

    private void clearMessage() {
        if (mHandler.hasMessages(MSG_UPDATE_UI)) {
            mHandler.removeMessages(MSG_UPDATE_UI);
        }
    }

    private String getFileName() {
        int i = 0;
        String dirName = FLASH_DATA_DIR[0];
        if(mCurrItem == 1) {
            dirName = FLASH_DATA_DIR[mCurrItem];
        }
        while(i++ < 10) {
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyMMddhhmmss");
            String tmpDate = dateFormater.format(new Date(System.currentTimeMillis()));
            String tmpRandom = String.valueOf((int)(1+Math.random() * 1000));
            String tmpFileName =  dirName + "/" + FILE_NAME_TEMPLATE + tmpDate + tmpRandom;
            File file = new File(tmpFileName);
            if(!file.exists()) {
                return tmpFileName;
            }
        }

        return "";
    }
}


