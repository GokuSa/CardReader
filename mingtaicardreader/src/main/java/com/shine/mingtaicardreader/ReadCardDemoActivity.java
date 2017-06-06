package com.shine.mingtaicardreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.Integer.parseInt;

public class ReadCardDemoActivity extends AppCompatActivity {

    @Bind(R.id.tv_status)
    TextView mTvStatus;
    @Bind(R.id.edit_section)
    EditText mEditSection;
    @Bind(R.id.edit_blockaddr)
    EditText mEditBlockaddr;
    @Bind(R.id.edit_secret)
    EditText mEditSecret;
    @Bind(R.id.tv_content)
    TextView mTvContent;
    private CardReader mCardReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_card_demo);
        ButterKnife.bind(this);
        mCardReader = CardReader.getInstance(this);
        mCardReader.init(mTvStatus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCardReader.close();
    }

    @OnClick({R.id.btn_id, R.id.btn_social, R.id.btn_M1,
            R.id.btn_shanghai_social_card,R.id.btn_get_region_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_id:
                mCardReader.readId(mTvContent);
                break;
            case R.id.btn_social:
                mCardReader.readSocialCard(mTvContent);
                break;
            case R.id.btn_M1:
                String secret = mEditSecret.getText().toString().trim();
                String blockAddress = mEditBlockaddr.getText().toString().trim();
                String section = mEditSection.getText().toString().trim();
                if (TextUtils.isEmpty(blockAddress) || TextUtils.isEmpty(section)) {
                    mTvContent.append("扇区号或块地址不能为空");
                    return;
                }
                mCardReader.readM1(mTvContent,parseInt(blockAddress), parseInt(section),secret);
                break;
            case R.id.btn_shanghai_social_card:
                mCardReader.readShangHaiSocialCard(mTvContent);
                break;
            case R.id.btn_get_region_code:
                mCardReader.readSocialCardRegionCode(mTvContent);
                break;
        }
    }
}
