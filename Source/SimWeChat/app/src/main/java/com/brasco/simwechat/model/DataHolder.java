package com.brasco.simwechat.model;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 12/14/2016.
 */
public class DataHolder {
    public static final String TAG = "DataHolder";

    private static DataHolder instance;
    private List<QBUser> qbUsers;
    private QBUser signInQbUser;

    private DataHolder() {
        qbUsers = new ArrayList<>();
    }

    public static synchronized DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public void addQbUsers(List<QBUser> qbUsers) {
        for (QBUser qbUser : qbUsers) {
            addQbUser(qbUser);
        }
    }

    public void addQbUser(QBUser qbUser) {
        if (!qbUsers.contains(qbUser)) {
            qbUsers.add(qbUser);
        }
    }

    public void updateQbUserList(int location, QBUser qbUser) {
        if (location != -1) {
            qbUsers.set(location, qbUser);
        }
    }

    public List<QBUser> getQBUsers() {
        return qbUsers;
    }

    public void clear() {
        qbUsers.clear();
    }

    public QBUser getSignInQbUser() {
        return signInQbUser;
    }

    public void setSignInQbUser(QBUser singInQbUser) {
        this.signInQbUser = singInQbUser;
    }

    public boolean isSignedIn() {
        return signInQbUser != null;
    }

}
