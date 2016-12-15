package com.brasco.simwechat.quickblox;

import com.brasco.simwechat.R;
import com.brasco.simwechat.quickblox.core.utils.ResourceUtils;

/**
 * Created by jontb on 7/20/2016.
 */
public interface QBConstants {

//	  String QB_APP_ID = "92";
//    String QB_AUTH_KEY = "wJHdOcQSxXQGWx5";
//    String QB_AUTH_SECRET = "BTFsj7Rtt27DAmT";
//    String QB_ACCOUNT_KEY = "rz2sXxBt5xgSxGjALDW6";

//    String QB_USERS_PASSWORD = "x6Bt0VDy5";

    String QB_APP_ID = "43578";
    String QB_AUTH_KEY = "PmwTZsS3rwVX5GE";
    String QB_AUTH_SECRET = "G2yCHfrj6VQO-9Y";
    String QB_ACCOUNT_KEY = "hZ21cPo1TyNwqR2GRBYg";

    String QB_USERS_TAG = "webrtcusers";
    String QB_USERS_PASSWORD = "kissprince";

    int PREFERRED_IMAGE_SIZE_PREVIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_preview_size);
    int PREFERRED_IMAGE_SIZE_VIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_view_size);
    int PREFERRED_IMAGE_SIZE_FULL = ResourceUtils.dpToPx(320);
}
