package com.can.appstore.index.model;

import android.content.Context;
import android.util.Log;

import com.can.appstore.entity.ListResult;
import com.can.appstore.entity.Navigation;

import cn.can.tvlib.utils.PreferencesUtils;

/**
 * Created by liuhao on 2016/10/17.
 */

public class DataUtils {

    private Context mContext;

    private ListResult<Navigation> mListResult;

    private DataUtils(Context context) {
        mContext = context;
    }

    private static DataUtils instance;

    public static DataUtils getInstance(Context context) {
        if (null == instance) {
            instance = new DataUtils(context);
        }
        return instance;
    }

    public static final String INDEX_DATA = "indexData";

    public String getCache() {
        String indexData = PreferencesUtils.getString(mContext, INDEX_DATA);
        if (null != indexData) {
            Log.i("DataUtils", "indexData " + indexData);
            return indexData;
        } else {
            return indexCache;
        }
    }

    public void clearData() {
        PreferencesUtils.putString(mContext, INDEX_DATA, null);
    }

    public void setCache(String mJson) {
        PreferencesUtils.putString(mContext, INDEX_DATA, mJson);
    }

    public void setIndexData(ListResult<Navigation> listResult) {
        mListResult = listResult;
    }

    public ListResult<Navigation> getIndexData() {
        return mListResult;
    }

    public static String indexCache = "{\"status\":0,\"message\":\"成功\",\"data\":[{\"id\":\"20\",\"title\":\"推荐\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"action\":\"action_topic_detail\",\"height\":1,\"x\":0,\"id\":\"426\",\"y\":0,\"title\":\"装机必备\",\"width\":1,\"actionData\":\"45\",\"icon\":\"recommend_photo1\"},{\"action\":\"action_topic_detail\",\"height\":1,\"x\":0,\"id\":\"427\",\"y\":1,\"title\":\"最新上架\",\"width\":1,\"actionData\":\"45\",\"icon\":\"recommend_photo2\"},{“action\":\"action_activity_detail\",\"height\":1,\"x\":0,\"id\":\"428\",\"y\":2,\"title\":\"活动专区\",\"width\":1,\"actionData\":\"43\",\"icon\":\"recommend_photo3\"},{“action\":\"action_app_detail\",\"height\":2,\"x\":1,\"id\":\"429\",\"y\":0,\"title\":\"芒果TV\",\"width\":2,\"actionData\":\"6063\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583647050f4bd.jpg\"},{\"action\":\"action_topic_detail\",\"height\":1,\"x\":1,\"id\":\"430\",\"y\":2,\"title\":\"高能撩妹\",\"width\":2,\"actionData\":\"35\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583678fb04867.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":3,\"id\":\"431\",\"y\":0,\"title\":\"华数TV\",\"width\":1,\"actionData\":\"6064\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367954ab069.jpg\"},{\"action\":\"action_topic_detail\",\"height\":1,\"x\":3,\"id\":\"432\",\"y\":2,\"title\":\"踏青觅春\",\"width\":2,\"actionData\":\"45\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836799f5e812.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":4,\"id\":\"433\",\"y\":0,\"title\":\"银河奇异果\",\"width\":1,\"actionData\":\"6065\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583688eb97d43.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":5,\"id\":\"434\",\"y\":0,\"title\":\"才智小天地\",\"width\":2,\"actionData\":\"6066\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367a11d67b7.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":5,\"id\":\"435\",\"y\":2,\"title\":\"直播秀\",\"width\":1,\"actionData\":\"6067\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367a33c7d96.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":6,\"id\":\"436\",\"y\":2,\"title\":\"fitime\",\"width\":1,\"actionData\":\"6053\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367b7830f7a.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":7,\"id\":\"437\",\"y\":0,\"title\":\"圣剑联盟\",\"width\":1,\"actionData\":\"6068\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583688df4437c.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":7,\"id\":\"438\",\"y\":2,\"title\":\"南瓜电影\",\"width\":2,\"actionData\":\"6072\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367c13aa95b.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":8,\"id\":\"439\",\"y\":0,\"title\":\"全球购\",\"width\":1,\"actionData\":\"6057\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367c7a2766b.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":9,\"id\":\"440\",\"y\":0,\"title\":\"树\",\"width\":1,\"actionData\":\"6058\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367cf18f66f.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":9,\"id\":\"441\",\"y\":1,\"title\":\"优酷\",\"width\":1,\"actionData\":\"6052\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367d2bd1e13.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":9,\"id\":\"442\",\"y\":2,\"title\":\"元气勇士\",\"width\":1,\"actionData\":\"6050\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367d6e623ad.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":10,\"id\":\"443\",\"y\":0,\"title\":\"释魂\",\"width\":1,\"actionData\":\"6049\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367d99e3c45.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":10,\"id\":\"444\",\"y\":2,\"title\":\"有乐斗地主\",\"width\":1,\"actionData\":\"6052\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367ddfe3d4b.jpg\"}]},{\"id\":\"22\",\"title\":\"游戏\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"445\",\"y\":0,\"title\":\"体感外设\",\"width\":1,\"actionData\":\"61\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367e50151c1.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"446\",\"y\":1,\"title\":\"棋牌中心\",\"width\":1,\"actionData\":\"60\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367e6ce8bb3.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"447\",\"y\":2,\"title\":\"更多分类\",\"width\":1,\"actionData\":\"65\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367e8f26119.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":1,\"id\":\"448\",\"y\":0,\"title\":\"红包斗地主\",\"width\":1,\"actionData\":\"6049\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367ebd58b5d.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":1,\"id\":\"449\",\"y\":2,\"title\":\"天生好斗\",\"width\":2,\"actionData\":\"6050\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367ef681015.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":2,\"id\":\"450\",\"y\":0,\"title\":\"激情对战\",\"width\":1,\"actionData\":\"6068\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367f2ff1a17.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":2,\"id\":\"451\",\"y\":1,\"title\":\"超级玛丽\",\"width\":1,\"actionData\":\"6063\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367f5868f2a.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":3,\"id\":\"452\",\"y\":0,\"title\":\"全民奇迹\",\"width\":2,\"actionData\":\"6064\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367f8de0509.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":3,\"id\":\"453\",\"y\":2,\"title\":\"百战天虫\",\"width\":2,\"actionData\":\"6065\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58367ff130fa5.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":5,\"id\":\"454\",\"y\":0,\"title\":\"囧囧兔\",\"width\":1,\"actionData\":\"6067\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836805ae9ba0.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":5,\"id\":\"455\",\"y\":1,\"title\":\"口袋战争\",\"width\":1,\"actionData\":\"6053\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58368083aa631.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":5,\"id\":\"456\",\"y\":2,\"title\":\"人气新服\",\"width\":1,\"actionData\":\"6068\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583680a913b9b.jpg\"}]},{\"id\":\"23\",\"title\":\"教育\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"457\",\"y\":0,\"title\":\"孕婴早教\",\"width\":1,\"actionData\":\"54\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836814f68747.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"458\",\"y\":1,\"title\":\"职业教育\",\"width\":1,\"actionData\":\"59\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58368145d4a07.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"459\",\"y\":2,\"title\":\"更多分类\",\"width\":1,\"actionData\":\"65\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836817697d2a.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":1,\"id\":\"460\",\"y\":0,\"title\":\"熊猫拼音\",\"width\":2,\"actionData\":\"6052\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583681af973a3.jpg\"},{\"action\":\"action_topic_detail\",\"height\":1,\"x\":1,\"id\":\"461\",\"y\":2,\"title\":\"感恩母亲节\",\"width\":2,\"actionData\":\"45\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583681da068b2.jpg\"},{\"action\":\"action_app_detail\",\"height\":3,\"x\":3,\"id\":\"462\",\"y\":0,\"title\":\"鱼乐贝贝\",\"width\":1,\"actionData\":\"6063\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583681fe4574a.jpg\"},{\"action\":\"action_topic_detail\",\"height\":2,\"x\":4,\"id\":\"463\",\"y\":0,\"title\":\"开学季\",\"width\":1,\"actionData\":\"35\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58368295eafbc.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":4,\"id\":\"464\",\"y\":2,\"title\":\"我图幼儿\",\"width\":1,\"actionData\":\"6065\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583682b35c8c0.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":5,\"id\":\"465\",\"y\":0,\"title\":\"学习\",\"width\":1,\"actionData\":\"6066\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583682ce1dbc1.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":5,\"id\":\"466\",\"y\":2,\"title\":\"快乐学堂\",\"width\":1,\"actionData\":\"6067\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836831ebb014.jpg\"}]},{\"id\":\"24\",\"title\":\"应用2\",\"baseWidth\":270,\"baseHeight\":180,\"lineSpace\":8,\"layout\":[{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"498\",\"y\":0,\"title\":\"影音资讯\",\"width\":1,\"actionData\":\"61\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583685fc104aa.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"499\",\"y\":1,\"title\":\"实用工具\",\"width\":1,\"actionData\":\"60\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58368617ecdc9.jpg\"},{\"action\":\"action_app_list\",\"height\":1,\"x\":0,\"id\":\"500\",\"y\":2,\"title\":\"更多分类\",\"width\":1,\"actionData\":\"65\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583686318b08f.jpg\"},{\"action\":\"action_topic_detail\",\"height\":2,\"x\":1,\"id\":\"501\",\"y\":0,\"title\":\"4月优质应用\",\"width\":2,\"actionData\":\"45\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836865909fb2.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":1,\"id\":\"502\",\"y\":2,\"title\":\"去哪儿旅行\",\"width\":1,\"actionData\":\"6052\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/58368681f1dbd.jpg\"},{\"action\":\"action_app_detail\",\"height\":1,\"x\":2,\"id\":\"503\",\"y\":2,\"title\":\"快手看片\",\"width\":1,\"actionData\":\"6068\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583686b9ab057.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":3,\"id\":\"504\",\"y\":0,\"title\":\"唯品会\",\"width\":1,\"actionData\":\"6050\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583686d87d728.jpg\"},{\"action\":\"action_topic_detail\",\"height\":1,\"x\":3,\"id\":\"505\",\"y\":2,\"title\":\"五一欢乐颂\",\"width\":2,\"actionData\":\"35\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/583686f8a2d3e.jpg\"},{\"action\":\"action_app_detail\",\"height\":2,\"x\":4,\"id\":\"506\",\"y\":0,\"title\":\"听说交通\",\"width\":1,\"actionData\":\"6064\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836871f0205d.jpg\"},{\"action\":\"action_app_detail\",\"height\":3,\"x\":5,\"id\":\"507\",\"y\":0,\"title\":\"演技派\",\"width\":1,\"actionData\":\"6065\",\"icon\":\"http://172.16.11.32:8010/upload/Recommend/2016-11-24/5836873711597.jpg\"}]}]}";


}
