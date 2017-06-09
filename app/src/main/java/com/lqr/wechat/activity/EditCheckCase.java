package com.lqr.wechat.activity;

import android.support.v7.widget.Toolbar;
        import android.text.TextUtils;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
import android.widget.ImageView;

import com.lqr.wechat.R;
        import com.lqr.wechat.model.Contact;
        import com.lqr.wechat.nimsdk.NimFriendSDK;
        import com.lqr.wechat.utils.UIUtils;
        import com.netease.nimlib.sdk.RequestCallback;
        import com.netease.nimlib.sdk.friend.constant.FriendFieldEnum;

        import java.util.HashMap;
        import java.util.Map;

        import butterknife.ButterKnife;
        import butterknife.InjectView;
        import butterknife.OnClick;

public class EditCheckCase extends BaseActivity {
    public static final int REQ_CHANGE_EDIT_TEXT = 102;
    private  EditText mEditText;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.btnOk)
    Button mBtnOk;

    @OnClick({R.id.btnOk})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnOk:
                saveAliasChange();
                break;
        }
    }

    @Override
    public void init() {
        mEditText = (EditText)findViewById(R.id.check_case_text);
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_checkcase_preview);
        ButterKnife.inject(this);
        initToolbar();
    }
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("编辑文本");
        mToolbar.setNavigationIcon(R.mipmap.ic_back);
        mBtnOk.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void saveAliasChange() {
        String alias = mEditText.getText().toString().trim();
        showWaitingDialog("请稍等");
        Map<FriendFieldEnum, Object> map = new HashMap<>(1);
        map.put(FriendFieldEnum.ALIAS, alias);
        /*NimFriendSDK.updateFriendFields(mContact.getAccount(), map, new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                UIUtils.showToast("修改备注信息成功");
                hideWaitingDialog();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailed(int code) {
                UIUtils.showToast("修改备注信息失败" + code);
                hideWaitingDialog();
            }

            @Override
            public void onException(Throwable exception) {
                exception.printStackTrace();
                hideWaitingDialog();
            }
        });*/
    }
}

