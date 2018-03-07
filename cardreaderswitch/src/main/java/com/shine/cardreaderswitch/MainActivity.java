package com.shine.cardreaderswitch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

/**
 * 班牌读卡开关demo
 * 进入页面获取读卡状态 是否在寻卡 并显示读卡开关状态
 * 功能：打开 关闭
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Switch mSwitcher;
    private TextView mTextView;
    private CardReaderOperator mCardReaderOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwitcher = findViewById(R.id.switcher);
        mTextView = findViewById(R.id.text);
        AppExecutors appExecutors = new AppExecutors();
        mCardReaderOperator = new CardReaderOperator(appExecutors, mOperatorListener);
        mSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mSwitcher.isChecked():" + mSwitcher.isChecked());
                mCardReaderOperator.sendInstruction(mSwitcher.isChecked());
            }
        });
    }

    private CardReaderOperator.OperatorListener mOperatorListener = new CardReaderOperator.OperatorListener() {
        @Override
        public void operatorResult(boolean success) {
            if (!success) {
                mTextView.setText("操作失败");
            }else{
                mTextView.setText("操作成功");
            }
        }

        @Override
        public void currentStatus(boolean isOn) {
            mTextView.setText("");
            mSwitcher.setChecked(isOn);
        }

        @Override
        public void failToCheck() {
            mTextView.setText("串口操作异常");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCardReaderOperator.exit();
    }
}
