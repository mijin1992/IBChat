package com.brasco.simwechat.quickblox.utils;

import android.text.TextUtils;

import com.brasco.simwechat.app.Constant;
import com.brasco.simwechat.quickblox.core.utils.SharedPrefsHelper;
import com.quickblox.auth.QBAuth;
import com.quickblox.core.exception.BaseServiceException;

import java.util.Date;

/**
 * Created by Administrator on 11/28/2016.
 */

public class TokenUtils {
    public static final String TAG = TokenUtils.class.getSimpleName();
    public static boolean isTokenValid() {
        String token = getCurrentToken();
        Date expirationDate = getTokenExpirationDate();

        if (TextUtils.isEmpty(token)) {
            return false;
        }

        if (expirationDate != null && System.currentTimeMillis() >= expirationDate.getTime()) {
            return false;
        }

        return true;
    }

    private static String getCurrentToken(){
        return SharedPrefsHelper.getInstance().get(Constant.PREF_CURRENT_TOKEN);
    }

    private static Date getTokenExpirationDate(){
        Date tokenExpirationDate = null;
        long tokenExpitationDateMilis = SharedPrefsHelper.getInstance().get(Constant.PREF_TOKEN_EXPIRATION_DATE, 0l);
        if (tokenExpitationDateMilis != 0l){
            tokenExpirationDate = new Date(tokenExpitationDateMilis);
        }

        return tokenExpirationDate;
    }

    public static void saveTokenData() {
        try {
            String currentToken = QBAuth.getBaseService().getToken();
            Date tokenExpirationDate = QBAuth.getBaseService().getTokenExpirationDate();
            SharedPrefsHelper.getInstance().save(Constant.PREF_CURRENT_TOKEN, currentToken);
            SharedPrefsHelper.getInstance().save(Constant.PREF_TOKEN_EXPIRATION_DATE, tokenExpirationDate.getTime());

        } catch (BaseServiceException e) {
            e.printStackTrace();
        }
    }

    public static boolean restoreExistentQbSessionWithResult() {
        if (isTokenValid()) {
            try {
                QBAuth.createFromExistentToken(getCurrentToken(), getTokenExpirationDate());
                return true;
            } catch (BaseServiceException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
