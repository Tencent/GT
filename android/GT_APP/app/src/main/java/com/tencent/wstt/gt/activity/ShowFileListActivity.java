package com.tencent.wstt.gt.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.wstt.gt.R;
import com.tencent.wstt.gt.api.utils.Env;
import com.tencent.wstt.gt.utils.FileUtils;
import com.tencent.wstt.gt.utils.ToastUtil;

import com.tencent.wstt.gt.analysis4.GTRDataToJsManager;
import com.tencent.wstt.gt.share.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by p_gumingcai on 2017/8/1.
 */

public class ShowFileListActivity extends GTBaseActivity {
    //存储文件名称
    private ArrayList<String> names = null;
    //存储文件路径
    private ArrayList<String> paths = null;

    private String originalPath = Env.GTR_PATH;
    private String desPath = Env.GTR_DATA_PATH;
    private ListView lv_Showfile;
    private ProgressDialog progressDialog;
    private static final int COPYSUCCESS = 1;
    private static final int COPYFAILED = 2;

    private String pullType = "";
    private IWXAPI api;
    private int CONTENT_LENGTH_LIMIT = 10485760;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COPYSUCCESS:
                    ToastUtil.ShowLongToast(ShowFileListActivity.this,
                            "导出成功", "center");
                    dismissProgress();
                    break;
                case COPYFAILED:
                    ToastUtil.ShowLongToast(ShowFileListActivity.this,
                            "请选择需要导出带有测试数据的文件夹", "center");
                    dismissProgress();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gtr_activity_showfile);
        lv_Showfile = (ListView) findViewById(R.id.lv_showfile);

        // 获取导出方式
        pullType = getIntent().getStringExtra("pullType");
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        // 将该app注册到微信
        api.registerApp(Constants.APP_ID);

        showFileDir(originalPath);
    }

    private void showFileDir(String path) {
        names = new ArrayList<String>();
        paths = new ArrayList<String>();
        File file = new File(path);
        File[] files = file.listFiles();
        //添加所有文件
        for (File f : files) {
            names.add(f.getName());
            paths.add(f.getPath());
        }
        lv_Showfile.setAdapter(new MyAdapter(this, names, paths));
        lv_Showfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (pullType) {
                    case "local":
                        new AlertDialog.Builder(ShowFileListActivity.this)
                                .setMessage(R.string.pi_file_alert)
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        displayProgress();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean success = false;
                                                try {
                                                    Log.d("adam", "开始解析数据 ");
                                                    long startTime = System.currentTimeMillis();
                                                    Boolean b = GTRDataToJsManager.toAnalysis(paths.get(position), false);
                                                    if (b) {
                                                        Log.d("adam", "解析数据完成，耗时 = " + (System.currentTimeMillis() - startTime) + "ms");
                                                        success = true;
                                                    } else {
                                                        ToastUtil.ShowShortToast(ShowFileListActivity.this, "Can not to analysis");
                                                    }
                                                } catch (Exception e) {
                                                    Message message = new Message();
                                                    message.what = COPYFAILED;
                                                    mHandler.handleMessage(message);
                                                    success = false;
                                                    e.printStackTrace();
                                                }
                                                if (success) {
                                                    Message message = new Message();
                                                    message.what = COPYSUCCESS;
                                                    mHandler.handleMessage(message);
                                                }
                                            }
                                        }).start();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case "wx":
                        new AlertDialog.Builder(ShowFileListActivity.this)
                                .setMessage("是否导出数据并分享到微信")
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        displayProgress();
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    long startTime = System.currentTimeMillis();
                                                    boolean b = GTRDataToJsManager.toAnalysis(paths.get(position), true);
                                                    if (b) {
                                                        long endTime = System.currentTimeMillis();
                                                        Log.i("adam", "解析数据完成使用" + (endTime - startTime) + "ms");
                                                        boolean isZipSuccess = FileUtils.zipFile(Env.GTR_WX_DATAJS_PATH_NAME,Env.GTR_ZIP_DATAJS_PATH_NAME);
                                                        if (isZipSuccess){
                                                            Log.i("adam", "压缩数据完成使用" + (System.currentTimeMillis() - endTime) + "ms");
                                                            File zipFile = new File(Env.GTR_ZIP_DATAJS_PATH_NAME);
                                                            dismissProgress();
                                                            if (isFileExceedsSize(zipFile)) {
                                                                File dataFile = new File(Env.GTR_WX_DATAJS_PATH_NAME);
                                                                if (zipFile.exists())
                                                                    zipFile.delete();
                                                                if (dataFile.exists())
                                                                    dataFile.delete();
                                                                ToastUtil.ShowShortToast(ShowFileListActivity.this, "测试数据压缩后大小超过10M,建议导出文件到手机根目录");
                                                            }else {
                                                                shareFileToWX(zipFile);
                                                                ShowFileListActivity.this.finish();
                                                            }
                                                        } else {
                                                            ToastUtil.ShowShortToast(ShowFileListActivity.this, "Compressed file failed");
                                                        }
                                                    } else {
                                                        ToastUtil.ShowShortToast(ShowFileListActivity.this, "Serialization file failed");
                                                    }
                                                } catch (Exception e) {
                                                    Log.i("adam", "run: error~~" + e.toString());
                                                    dismissProgress();
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    default:
                        new Exception("The pullType is not found");
                        break;
                }
            }
        });
    }

    private void displayProgress() {
        displayProgress(Env.GTR_DATA_DEFAULT_MESSAGE);
    }

    private void displayProgress(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        //存储文件名称
        private ArrayList<String> names = null;
        //存储文件路径
        private ArrayList<String> paths = null;

        //参数初始化
        public MyAdapter(Context context, ArrayList<String> na, ArrayList<String> pa) {
            names = na;
            paths = pa;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return names.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.item_file, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.textView);
                holder.image = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            File f = new File(paths.get(position).toString());
            holder.text.setText(f.getName());
            if (f.isDirectory()) {
                holder.image.setImageResource(R.drawable.folder);
            } else if (f.isFile()) {
                holder.image.setImageResource(R.drawable.file);
            } else {
                Log.e("file", f.getName());
            }
            return convertView;
        }

        private class ViewHolder {
            private TextView text;
            private ImageView image;
        }
    }

    private boolean isFileExceedsSize(File file) {
        if (file.length() > CONTENT_LENGTH_LIMIT)
            return true;
        return false;
    }

    private boolean shareFileToWX(File file) {
        WXFileObject fileObject = new WXFileObject();
        fileObject.setContentLengthLimit(CONTENT_LENGTH_LIMIT);
        fileObject.setFilePath(file.getPath());

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = fileObject;
        msg.title = file.getName();

        SendMessageToWX.Req req = new SendMessageToWX.Req();

        req.transaction = "file" + System.currentTimeMillis();
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        api.sendReq(req);
        return true;
    }
}
