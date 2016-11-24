package com.can.appstore.upgrade;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.can.appstore.upgrade.view.UpgradeFailDialog;
import com.can.appstore.upgrade.view.UpgradeInFoDialog;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import cn.can.downloadlib.AppInstallListener;
import cn.can.downloadlib.DownloadManager;
import cn.can.downloadlib.DownloadTask;
import cn.can.downloadlib.DownloadTaskListener;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by syl on 2016/11/2.
 * 应用商城自更新服务
 */

public class UpgradeService extends IntentService {
    public static final String TAG = "UpgradeService";
    public static final int SHOW_UPGRADE_INFO_DIALOG = 1;
    public static final int SHOW_UPGRADE_INSTALL_COMPLETE = 2;
    public static final int SHOW_UPGRADE_PROGRESSBAR_DIALOG = 3;
    public static final int SHOW_UPGRADE_FAIL_DIALOG = 4;
    private int mLocalVersion;
    private String mUpdatePath;
    private String mFileName;
    private UpgradeInfo mUpgradeInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPGRADE_INFO_DIALOG:
                    UpgradeInFoDialog.OnUpgradeClickListener listener = new UpgradeInFoDialog.OnUpgradeClickListener() {
                        @Override
                        public void onClick() {
                            installApk();
                        }
                    };
                    UpgradeInFoDialog dialog = new UpgradeInFoDialog(UpgradeService.this, "系统升级", mUpgradeInfo
                            .versionName, mUpgradeInfo.newFeature, "安装", listener);
                    dialog.show();
                    break;
                case SHOW_UPGRADE_INSTALL_COMPLETE:
                    UpgradeInFoDialog.OnUpgradeClickListener listener1 = new UpgradeInFoDialog.OnUpgradeClickListener
                            () {
                        @Override
                        public void onClick() {
                            ToastUtils.showMessage(UpgradeService.this, "运行apk");
                        }
                    };
                    UpgradeInFoDialog upgradeInfoDialog = new UpgradeInFoDialog(UpgradeService.this, "已经是最新版本了",
                            mUpgradeInfo.versionName, mUpgradeInfo.newFeature, "运行", listener1);
                    upgradeInfoDialog.show();
                    break;
                case SHOW_UPGRADE_PROGRESSBAR_DIALOG:
                    //                    mProgressBarDialog = new UpgradeProgressBarDialog(UpgradeService.this);
                    //                    mProgressBarDialog.show();
                    break;
                case SHOW_UPGRADE_FAIL_DIALOG:
                    String content = (String) msg.obj;
                    UpgradeFailDialog failDialog = new UpgradeFailDialog(UpgradeService.this, content);
                    failDialog.show();
                    break;
            }

        }
    };

    public UpgradeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ToastUtils.showMessage(this, "启动服务");
        requsetInfo();
    }

    private void requsetInfo() {
        // TODO: 2016/11/2  联网获取数据
        mUpgradeInfo = Beta.getUpgradeInfo();
        if (mUpgradeInfo == null) {
            return;
        }
        checkUpgradeInfo();
    }

    /**
     * 检测更新信息并作出逻辑判断
     */
    private void checkUpgradeInfo() {
        mUpdatePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/updateapk";
        if (!UpgradeUtil.isFileExist(mUpdatePath)) {
            UpgradeUtil.creatDir(mUpdatePath);
        }
        mFileName = mUpdatePath + "/" + mUpgradeInfo.versionCode + ".apk";
        //获取本地的版本号
        try {
            mLocalVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext()
                    .getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "LocalVersionCode=" + mLocalVersion + ",UpGradeVersionCode=" + mUpgradeInfo.versionCode);
        if (mUpgradeInfo.versionCode <= mLocalVersion) {
            return;
        }
        if (checkIsExistApk()) {
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        } else {
            //先清空本地存放Apk的空间
            // TODO: 2016/11/16 要不要清空
            UpgradeUtil.delAllDateFile(mUpdatePath);
            downLoadApk(mUpgradeInfo.apkUrl);
        }
    }

    /**
     * 检测是否存在已经下载完成但没有安装的最新版本
     */
    private boolean checkIsExistApk() {
        if (UpgradeUtil.isFileExist(mFileName)) {
            String localMD5 = UpgradeUtil.getFileMD5(mFileName);
            if (mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
                Log.d(TAG, "存在已下载未安装的apk");
                return true;
            }
        }
        return false;
    }

    /**
     * 下载自升级的APK
     *
     * @param url 下载地址
     */
    private void downLoadApk(String url) {
        DownloadManager manager = DownloadManager.getInstance(this);
        DownloadTask task = new DownloadTask(url);
        task.setFileName(mUpgradeInfo.versionCode + ".apk");
        task.setSaveDirPath(mUpdatePath);

        /**
         * 调用监听，阻止下载完成自动静默安装，不可删除
         */
        task.setAppListener(new AppInstallListener() {
            @Override
            public void onInstalling(DownloadTask downloadTask) {
            }

            @Override
            public void onInstallSucess(String id) {
            }

            @Override
            public void onInstallFail(String id) {
            }

            @Override
            public void onUninstallSucess(String id) {

            }

            @Override
            public void onUninstallFail(String id) {

            }
        });

        manager.singleTask(task, new DownloadTaskListener() {
            @Override
            public void onPrepare(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onPrepare");
            }

            @Override
            public void onStart(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onStart");
            }

            @Override
            public void onDownloading(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onDownloading----" + downloadTask.getPercent());
            }

            @Override
            public void onPause(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onPause");
            }

            @Override
            public void onCancel(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onCancel");
            }

            @Override
            public void onCompleted(DownloadTask downloadTask) {
                Log.d(TAG, "DownloadManager=onCompleted");
                onLoadingCompleted();
            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                Log.d(TAG, "DownloadManager=onError===" + errorCode);
                onLoadingError();
            }

        });
    }


    private void installApk() {
        UpgradeUtil.install(this, mFileName, mUpgradeInfo.fileSize, new InstallApkListener() {

            @Override
            public void onInstallSuccess() {
                Log.d(TAG, "onInstallSuccess: ");
                onInstallComplete();
            }

            @Override
            public void onInstallFail(String reason) {
                Log.d(TAG, "onInstallFail: ");
                onInstallError(reason);
            }
        });
    }

    private void onLoadingCompleted() {
        String localMD5 = UpgradeUtil.getFileMD5(mFileName);
        if (!mUpgradeInfo.apkMd5.equalsIgnoreCase(localMD5)) {
            UpgradeUtil.delAllDateFile(mUpdatePath);
        } else {
            //显示安装apk对话框
            Message msg = Message.obtain();
            msg.what = SHOW_UPGRADE_INFO_DIALOG;
            mHandler.sendMessage(msg);
        }
    }

    private void onLoadingError() {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = "下载异常，升级失败";
        mHandler.sendMessage(msg);
    }

    private void onInstallComplete() {
        mHandler.sendEmptyMessage(SHOW_UPGRADE_INSTALL_COMPLETE);
    }

    private void onInstallError(String reason) {
        Message msg = Message.obtain();
        msg.what = SHOW_UPGRADE_FAIL_DIALOG;
        msg.obj = reason;
        mHandler.sendMessage(msg);
        //安装失败，清空文件夹
        UpgradeUtil.delAllDateFile(mUpdatePath);
    }
}
