package com.can.appstore.uninstallmanager;

import android.support.v4.app.LoaderManager;
import android.text.SpannableStringBuilder;

import com.can.appstore.appdetail.AppDetailContract;
import com.can.appstore.base.BasePresenter;
import com.can.appstore.base.BaseView;
import com.can.appstore.entity.SelectedAppInfo;

import java.util.List;


/**
 * Created by JasonF on 2016/10/17.
 */

public interface UninstallManagerContract {

    interface Presenter extends BasePresenter {
        void startLoad(LoaderManager loaderManager);

        void addListener();

        void onItemFocus(int position);
    }

    interface View extends BaseView<AppDetailContract.Presenter> {
        void loadAllAppInfoSuccess(List<SelectedAppInfo> infoList);

        void showCurStorageProgress(int progress, String storage);

        void refreshSelectCount(int count);

        void uninstallLastPosition(int position);//卸载最后一个位置,让刷新后的最后一个位置请求焦点

        void clickNegativeRefreshPage(int position, int count);//按取消时刷新页面选择的数量

        void refreshSelectPosition(int[] selectPosition);//当有应用安装时刷新后选择之前选择的应用

        void refreshRows(SpannableStringBuilder rows);
    }
}
