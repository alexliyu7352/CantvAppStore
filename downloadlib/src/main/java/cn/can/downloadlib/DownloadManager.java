package cn.can.downloadlib;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

/**
 * ================================================
 * 作    者：朱洪龙
 * 版    本：1.0
 * 创建日期：2016/10/10
 * 描    述：下载器
 * 修订历史：
 * ================================================
 */
public class DownloadManager {
    private static final String TAG = "DownloadManager";
    private static final int READ_TIMEOUT = 5;
    private static final int WRITE_TIMEOUT = 2;
    private static final int CONNECT_TIMEOUT = 5;

    private static final int MSG_SUBMITTASK = 1000;
    private static final int MSG_RESUME = 1001;

    private static DownloadManager mInstance;
    private static DownloadDao mDownloadDao;
    private Context mContext;
    private int mPoolSize = 2;//Runtime.getRuntime().availableProcessors();
    private ExecutorService mExecutorService;
//    private Map<String, Future> mFutureMap;
    private OkHttpClient mOkHttpClient;
    private Map<String, DownloadTask> mCurrentTaskList = new HashMap<String, DownloadTask>();
    private BlockingQueue<String> mWorkTaskQueue = new LinkedBlockingQueue<>();
    public static BlockingQueue<String> mErrorTaskQueue = new LinkedBlockingQueue<>();

    private HandlerThread mHandlerThread;
    private Handler mHander;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUBMITTASK:
                    if (NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
                        if (((ThreadPoolExecutor)mExecutorService).getActiveCount() < mPoolSize) {
                            try {
                                String taskId = mErrorTaskQueue.poll();
                                if (taskId == null) {
                                    taskId = mWorkTaskQueue.poll(1, TimeUnit.SECONDS);
                                }
                                if (taskId != null) {
                                    Future future = mExecutorService.submit(mCurrentTaskList.get(taskId));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mHander.sendEmptyMessageDelayed(MSG_SUBMITTASK, 1000);
                    break;
                case MSG_RESUME:
                    break;
                case 3:
                    break;
                case 4:
                    break;
            }
            return false;
        }
    };

    public DownloadManager(OkHttpClient client, Context context) {
        this.mOkHttpClient = client;
        this.mContext = context;

    }

    private DownloadManager() {
        init();
    }

    private DownloadManager(Context context, InputStream in) {
        this.mContext = context;
        init(in, null);
    }

    /**
     * 支持https
     *
     * @param certificates
     * @return
     */
    public static SSLSocketFactory initCertificates(InputStream... certificates) {
        CertificateFactory certificateFactory;
        SSLContext sslContext = null;
        try {
            certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory
                        .generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sslContext != null) {
            return sslContext.getSocketFactory();
        }
        return null;
    }

    /**
     * @param context
     * @param sslKey  https签名文件
     * @return
     */
    public static DownloadManager getInstance(Context context, InputStream sslKey) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                mInstance = new DownloadManager(context, sslKey);
            }
        }
        return mInstance;
    }

    public static DownloadManager getInstance(Context context) {
        return getInstance(context, null);
    }

    public static DownloadManager getInstance(OkHttpClient okHttpClient, Context context) {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                mInstance = new DownloadManager(okHttpClient, context);
            }
        }
        return mInstance;
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public Map<String, DownloadTask> getCurrentTaskList() {
        return mCurrentTaskList;
    }

    /**
     * 设置线程池数量
     *
     * @param count
     */
    public void setPoolSize(int count) {
        mPoolSize = count;
    }

    /**
     * 初始化，使用OkHttpClient
     *
     * @param in
     * @param okHttpClient
     */
    private void init(InputStream in, OkHttpClient okHttpClient) {
        mHandlerThread = new HandlerThread("queue");
        mHandlerThread.start();
        mHander = new Handler(mHandlerThread.getLooper(), mCallback);

        mExecutorService = Executors.newFixedThreadPool(mPoolSize);
//        mFutureMap = new HashMap<String, Future>();
        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext, "downloadDB", null);
        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
        mDownloadDao = daoMaster.newSession().getDownloadDao();
        if (okHttpClient != null) {
            mOkHttpClient = okHttpClient;
        } else {
            OkHttpClient.Builder buider = new OkHttpClient.Builder();
            if (in != null) {
                buider.sslSocketFactory(initCertificates(in));
            }
            mOkHttpClient = buider.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS).build();
        }
    }

    private void init() {
        init(null, null);
    }

    /**
     * 如果任务存在，则返回task，否则返回null
     *
     * @param task
     * @param listener
     * @return
     */
    public boolean addDownloadTask(DownloadTask task, DownloadTaskListener listener) {
        if (!NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
            return false;
        }
        DownloadTask downloadTask = mCurrentTaskList.get(task.getId());
        if (null != downloadTask && downloadTask.getDownloadStatus() != DownloadStatus
                .DOWNLOAD_STATUS_CANCEL) {
            Log.d(TAG, "task already exist");
            return false;
        }
        mCurrentTaskList.put(task.getId(), task);
        try {
            mWorkTaskQueue.put(task.getId());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PREPARE);
        task.setDownloadDao(mDownloadDao);
        task.setHttpClient(mOkHttpClient);
        task.addDownloadListener(listener);
        if (getDBTaskById(task.getId()) == null) {
            DownloadDBEntity dbEntity = new DownloadDBEntity(task.getId(), task.getTotalSize(),
                    task.getCompletedSize(), task.getUrl(), task.getSaveDirPath(), task
                    .getFileName(), task.getDownloadStatus());
            mDownloadDao.insertOrReplace(dbEntity);
        }
//        Future future = mExecutorService.submit(task);
//        mFutureMap.put(task.getId(), future);
        mHander.sendEmptyMessage(MSG_SUBMITTASK);
        return true;
    }

    /**
     * 根据taskId获取task
     *
     * @param taskId
     * @return
     */
    public DownloadTask resume(String taskId) {
        if (!NetworkUtils.isNetworkConnected(mContext.getApplicationContext())) {
            return null;
        }
        DownloadTask downloadTask = getCurrentTaskById(taskId);
        if (downloadTask != null) {
            if (downloadTask.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
                Future future = mExecutorService.submit(downloadTask);
//                mFutureMap.put(downloadTask.getId(), future);
            }

        } else {
            downloadTask = getDBTaskById(taskId);
            if (downloadTask != null) {
                downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
                mCurrentTaskList.put(taskId, downloadTask);
                Future future = mExecutorService.submit(downloadTask);
//                mFutureMap.put(downloadTask.getId(), future);
            }
        }
        return downloadTask;
    }

    /**
     * 添加下载监听
     *
     * @param task
     * @param listener
     */
    public void addDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.addDownloadListener(listener);
    }

    /**
     * 删除下载监听
     *
     * @param task
     * @param listener
     */
    public void removeDownloadListener(DownloadTask task, DownloadTaskListener listener) {
        task.removeDownloadListener(listener);
    }

    /**
     * 取消任务
     *
     * @param task
     */
    public void cancel(DownloadTask task) {
        task.cancel();
        mCurrentTaskList.remove(task.getId());
        mWorkTaskQueue.remove(task.getId());
//        mFutureMap.remove(task.getId());
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        mDownloadDao.deleteByKey(task.getId());
    }

    /**
     * 取消任务
     *
     * @param taskId
     */
    public void cancel(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            cancel(task);
        }
    }

    /**
     * 暂停任务
     *
     * @param task
     */
    public void pause(DownloadTask task) {
        task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
    }

    /**
     * 暂停任务
     *
     * @param taskId
     */
    public void pause(String taskId) {
        DownloadTask task = getTaskById(taskId);
        if (task != null) {
            pause(task);
        }
    }

    /**
     * 读取任务属性信息
     *
     * @return
     */
    public List<DownloadDBEntity> loadAllDownloadEntityFromDB() {
        return mDownloadDao.loadAll();
    }

    /**
     * 读取未执行任务
     *
     * @return
     */
    public List<DownloadTask> loadAllDownloadTaskFromDB() {
        List<DownloadDBEntity> list = loadAllDownloadEntityFromDB();
        List<DownloadTask> downloadTaskList = null;
        if (list != null && !list.isEmpty()) {
            downloadTaskList = new ArrayList<DownloadTask>();
            for (DownloadDBEntity entity : list) {
                downloadTaskList.add(DownloadTask.parse(entity));
            }
        }
        return downloadTaskList;
    }

    /**
     * 获取所有任务（执行中与未执行）
     *
     * @return
     */
    public List<DownloadTask> loadAllTask() {
        List<DownloadTask> list = loadAllDownloadTaskFromDB();
        Map<String, DownloadTask> currentTaskMap = getCurrentTaskList();
        List<DownloadTask> currentList = new ArrayList<DownloadTask>();
        if (currentTaskMap != null) {
            currentList.addAll(currentTaskMap.values());
        }
        if (!currentList.isEmpty() && list != null) {
            for (DownloadTask task : list) {
                if (!currentList.contains(task)) {
                    currentList.add(task);
                }
            }
        } else {
            if (list != null) currentList.addAll(list);
        }
        return currentList;
    }

    /**
     * 获取队列中的任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getCurrentTaskById(String taskId) {
        return mCurrentTaskList.get(taskId);
    }

    /**
     * 通过taskId获取任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getTaskById(String taskId) {
        DownloadTask task = null;
        task = getCurrentTaskById(taskId);
        if (task != null) {
            return task;
        }
        return getDBTaskById(taskId);
    }

    /**
     * 通过taskId获取任务
     *
     * @param taskId
     * @return
     */
    public DownloadTask getDBTaskById(String taskId) {
        DownloadDBEntity entity = mDownloadDao.load(taskId);
        if (entity != null) {
            return DownloadTask.parse(entity);
        }
        return null;
    }

    public void release() {
        for (String key : mCurrentTaskList.keySet()) {
            System.out.println("key= " + key + " and value= " + mCurrentTaskList.get(key));
            mCurrentTaskList.get(key).removeAllDownloadListener();
            mCurrentTaskList.get(key).pause();
        }
        mCurrentTaskList.clear();
        mCurrentTaskList = null;

//        for (String key : mFutureMap.keySet()) {
//            System.out.println("key= " + key + " and value= " + mFutureMap.get(key));
//            mFutureMap.get(key).cancel(true);
//        }
//        mFutureMap.clear();
//        mFutureMap = null;

        mExecutorService.shutdownNow();
        mExecutorService = null;

        mOkHttpClient = null;
        mDownloadDao = null;
    }
}