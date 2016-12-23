package com.brasco.simwechat.app;

/**
 * Created by Administrator on 12/14/2016.
 */

public class Constant {
    // In GCM, the Sender ID is a project ID that you acquire from the API console
    //public static String GCM_SENDER_ID = "761750217637";
    public static String GCM_SENDER_ID = "25726407566";

    public static String QB_APP_ID = "51013";
    public static String QB_AUTH_KEY = "F62UjPafDSCmB-U";
    public static String QB_AUTH_SECRET = "aru4JB5XzZARfAz";
    public static String QB_ACCOUNT_KEY = "A9mV1yJvzSXquqqBUzmJ";

    //    public static String QB_USERS_TAG = "webrtcusers";
    public static String QB_USERS_TAG = "simwechatuser";
    public static String QB_USERS_PASSWORD = "111111111";

    //public static int PREFERRED_IMAGE_SIZE_PREVIEW = ResourceUtils.getDimen(R.dimen.chat_attachment_preview_size);
    //public static int PREFERRED_IMAGE_SIZE_FULL = ResourceUtils.dpToPx(320);

    public static final String GCM_PROJECT_NUMBER = "192744644051";

    public static final String FIREBASE_DEFAULT_PASS = "aru4JB5XzZARfAz";
    public static final String FIREBASE_USERS = "users";
    public static final String FIREBASE_POSTS = "posts";
    public static final String FIREBASE_COMMENT_FIELD = "comment";
    public static final String FIREBASE_GENDER_FIELD = "gender";
    public static final String FIREBASE_GENDER_MALE = "Male";
    public static final String FIREBASE_GENDER_FEMALE = "Female";

    public static final int REQ_IMAGE_FROM_CAMERA = 10001;
    public static final int REQ_IMAGE_FROM_GALLERY = 10002;
    public static final int REQ_IMAGE_FROM_GALLERY_CROP = 10003;
    public static final int REQ_VIDEO_FROM_GALLERY = 10004;
    public static final int REQ_PHOTO_FILE = 10005;
    public static final int REQ_VIDEO_FILE = 10006;
    public static final int REQ_INPUT_VALUE = 10100;

    /*
     * Extra keys
     */
    public static final String EK_ACTIVITY_ID = "EK_ACTIVITY_ID";
    public static final String EK_CATEGORY = "EK_CATEGORY";
    public static final String EK_FLAG = "EK_FLAG";
    public static final String EK_NAME = "EK_NAME";
    public static final String EK_RADIUS = "EK_RADIUS";
    public static final String EK_SENDER_ID = "EK_SENDER_ID";
    public static final String EK_URL = "EK_URL";
    public static final String REQ_VIDEO_CAMERAACTIVITY_TYPE = "EK_VIDEO_URL";
    public static final String REQ_IMAGE_CAMERAACTIVITY_TYPE = "EK_IMAGE_URL";
    public static final String REQ_VIDEO_TYPE = "EK_VIDEO_FILE";
    public static final String REQ_IMAGE_TYPE = "EK_IMAGE_FILE";
    public static final String REQ_INPUT_TYPE = "INPUT_TYPE";
    public static final String REQ_INPUT_STRING = "INPUT_STRING";

    /*
    Key for View Request and Show
     */
    public static final String SEND_VIDEO_VIEW_REQUEST  = "#####*****#####";
    public static final String SEND_VIDEO_SHOW_START    = "#####*******#####";
    public static final String SEND_VIDEO_SHOW_END      = "#####*********#####";
    /*
     * Broadcast action id
     */
    public static final String MY_ACTION_NEW_ACTIVITY = "MY_ACTION_NEW_ACTIVITY";
    public static final String MY_ACTION_MESSAGE_COUNT_CHANGED = "MY_ACTION_MESSAGE_COUNT_CHANGED";

    /*
     * Verification code
     */
    public static final String SENDER_EMAIL = "admin@veew.com";

    /*
     * Camera
     */
    public static final int CAPTURE_PHOTO = 1;
    public static final int CAPTURE_VIDEO = 2;
    public static final int CAPTURE_VIDEO_START = 3;
    public static final int CAPTURE_VIDEO_STOP = 4;

    /*
     * Video
     */
    public static int MAX_MEDIA_SIZE = 10 * 1024 * 1024; // 10 Mb
    public static int MAX_DESCRIPTION_LENGTH = 100;

    public static final int MIN_DURATION_MS = 1000 * 6;
    public static final int MAX_DURATION_MS = 1000 * 15;
    public static final int FRAME_COUNT = 10;

    public static final String[] DISTANCE_LABELS = {
            "200 Yards",
            "500 Yards",
            "1 Mile",
            "2 Miles",
            "5 Miles",
            "10 Miles",
            "20 Miles",
            "50 Miles",
            "100 Miles",
    };

    public static final int[] DISTANCE_VALUES = {
            182,
            457,
            1609,
            3218,
            8046,
            16093,
            32186,
            80467,
            160934,
    };

    public static final String[] TIME_LABELS = {
            "10 minutes",
            "30 minutes",
            "1 hours",
            "2 hours",
            "6 hours",
            "12 hours",
            "1 day",
    };

    public static final long[] TIME_VALUES = {
            10 * 60 * 1000,
            30 * 60 * 1000,
            1 * 60 * 60 * 1000,
            2 * 60 * 60 * 1000,
            6 * 60 * 60 * 1000,
            12 * 60 * 60 * 1000,
            24 * 60 * 60 * 1000,
    };
    public static final String DEFAULT_WEB_URL = "http://www.google.com";

    public static final int CALL_ACTIVITY_CLOSE = 1000;

    public static final int ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422;
    public static final int ERR_MSG_DELETING_HTTP_STATUS = 401;

    //CALL ACTIVITY CLOSE REASONS
    public static final int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    public static final String WIFI_DISABLED = "wifi_disabled";

    public static final String OPPONENTS = "opponents";
    public static final String CONFERENCE_TYPE = "conference_type";
    public static final String EXTRA_TAG = "currentRoomName";
    public static final int MAX_OPPONENTS_COUNT = 6;

    public static final String PREF_CURREN_ROOM_NAME = "current_room_name";
    public static final String PREF_CURRENT_TOKEN = "current_token";
    public static final String PREF_TOKEN_EXPIRATION_DATE = "token_expiration_date";

    public static final String EXTRA_QB_USER = "qb_user";

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_LOGIN = "user_login";
    public static final String EXTRA_USER_PASSWORD = "user_password";
    public static final String EXTRA_PENDING_INTENT = "pending_Intent";

    public static final String EXTRA_CONTEXT = "context";
    public static final String EXTRA_OPPONENTS_LIST = "opponents_list";
    public static final String EXTRA_CONFERENCE_TYPE = "conference_type";
    public static final String EXTRA_IS_INCOMING_CALL = "conversation_reason";

    public static final String EXTRA_LOGIN_RESULT = "login_result";
    public static final String EXTRA_LOGIN_ERROR_MESSAGE = "login_error_message";
    public static final int EXTRA_LOGIN_RESULT_CODE = 1002;

    public static final String[] PERMISSIONS = {android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};

    public static final String EXTRA_COMMAND_TO_SERVICE = "command_for_service";
    public static final int COMMAND_NOT_FOUND = 0;
    public static final int COMMAND_LOGIN = 1;
    public static final int COMMAND_LOGOUT = 2;
    public static final String EXTRA_IS_STARTED_FOR_CALL = "isRunForCall";
    public static final String ALREADY_LOGGED_IN = "You have already logged in chat";

    public static enum StartConversationReason {
        INCOME_CALL_FOR_ACCEPTION,
        OUTCOME_CALL_MADE
    }
}
