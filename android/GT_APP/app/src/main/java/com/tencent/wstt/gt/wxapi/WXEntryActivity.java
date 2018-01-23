package com.tencent.wstt.gt.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.wstt.gt.activity.GTBaseActivity;
import com.tencent.wstt.gt.activity.ShowFileListActivity;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.share.Constants;

import java.io.File;

/**
 * @类名称： WXEntryActivity.java
 * @创建人： Adam
 * @创建时间： 2017-11-20 17:15
 * @version： V1.0
 * @类描述：
 */

public class WXEntryActivity extends GTBaseActivity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);

        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        String result = "";
        File zipFile = new File(Env.GTR_ZIP_DATAJS_PATH_NAME);
        File dataFile = new File(Env.GTR_WX_DATAJS_PATH_NAME);
        if (zipFile.exists())
            zipFile.delete();
        if (dataFile.exists())
            dataFile.delete();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = "不支持错误";
                break;
            default:
                result = "发送返回";
                break;
        }
        Intent intent = new Intent();
        intent.setClass(WXEntryActivity.this, ShowFileListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("pullType", "wx");
        intent.putExtras(bundle);
        startActivity(intent);
        WXEntryActivity.this.finish();
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}
