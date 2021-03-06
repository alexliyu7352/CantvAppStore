package com.can.appstore.myapps.addappsview;


import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.entity.SelectedAppInfo;

import java.util.List;


/**
 * Created by wei on 2016/11/3.
 */

public interface AddAppsContract {
    interface Presenter extends BasePresenter {
        void startLoad();

        void addListener();

        void release();
    }

    interface View extends BaseView{

        void loadAddAppInfoSuccess(List<SelectedAppInfo> infoList);

        void showCanSelectCount(int cansel, int alreadyshow);

        void saveSelectInfo(List<SelectedAppInfo> list);

        void setAlreadySelectApp(int[] alreadySelect);
    }

}
