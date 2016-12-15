package com.brasco.simwechat.quickblox.gcm;


import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.quickblox.core.gcm.CoreGcmPushInstanceIDService;

public class GcmPushInstanceIDService extends CoreGcmPushInstanceIDService {
    @Override
    protected String getSenderId() {
        return Constant.GCM_PROJECT_NUMBER;
    }
}
