package com.brasco.simwechat.quickblox.utils.qb.callback;

/**
 * Created by Administrator on 11/11/2016.
 */

import android.os.Bundle;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;

public class QbEntityCallbackImpl<T> implements QBEntityCallback<T> {

    public QbEntityCallbackImpl() {
    }

    @Override
    public void onSuccess(T result, Bundle bundle) {

    }

    @Override
    public void onError(QBResponseException e) {

    }
}
