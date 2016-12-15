package com.brasco.simwechat.quickblox.managers;

import android.text.TextUtils;
import android.util.Log;

import com.brasco.simwechat.quickblox.utils.chat.ChatHelper;
import com.brasco.simwechat.quickblox.utils.qb.QbUsersHolder;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class QbDialogUtils {
    private static final String TAG = QbDialogUtils.class.getSimpleName();

    public static QBChatDialog createDialog(List<QBUser> users) {
        QBUser currentUser = ChatHelper.getCurrentUser();
        users.remove(currentUser);

        QBChatDialog dialogToCreate = new QBChatDialog();
        dialogToCreate.setName(QbDialogUtils.createChatNameFromUserList(users));
        if (users.size() == 1) {
            dialogToCreate.setType(QBDialogType.PRIVATE);
        } else {
            dialogToCreate.setType(QBDialogType.GROUP);
        }
        dialogToCreate.setOccupantsIds(new ArrayList<>(Arrays.asList(QbDialogUtils.getUserIds(users))));
        return dialogToCreate;
    }

    public static List<QBUser> getAddedUsers(QBChatDialog dialog, List<QBUser> currentUsers) {
        return getAddedUsers(getQbUsersFromQbDialog(dialog), currentUsers);
    }

    public static List<QBUser> getAddedUsers(List<QBUser> previousUsers, List<QBUser> currentUsers) {
        List<QBUser> addedUsers = new ArrayList<>();
        for (QBUser currentUser : currentUsers) {
            boolean wasInChatBefore = false;
            for (QBUser previousUser : previousUsers) {
                if (currentUser.getId().equals(previousUser.getId())) {
                    wasInChatBefore = true;
                    break;
                }
            }
            if (!wasInChatBefore) {
                addedUsers.add(currentUser);
            }
        }

        QBUser currentUser = ChatHelper.getCurrentUser();
        addedUsers.remove(currentUser);

        return addedUsers;
    }

    public static List<QBUser> getRemovedUsers(QBChatDialog dialog, List<QBUser> currentUsers) {
        return getRemovedUsers(getQbUsersFromQbDialog(dialog), currentUsers);
    }

    public static List<QBUser> getRemovedUsers(List<QBUser> previousUsers, List<QBUser> currentUsers) {
        List<QBUser> removedUsers = new ArrayList<>();
        for (QBUser previousUser : previousUsers) {
            boolean isUserStillPresented = false;
            for (QBUser currentUser : currentUsers) {
                if (previousUser.getId().equals(currentUser.getId())) {
                    isUserStillPresented = true;
                    break;
                }
            }
            if (!isUserStillPresented) {
                removedUsers.add(previousUser);
            }
        }

        QBUser currentUser = ChatHelper.getCurrentUser();
        removedUsers.remove(currentUser);

        return removedUsers;
    }

    public static void logDialogUsers(QBChatDialog qbDialog) {
        Log.v(TAG, "Dialog " + getDialogName(qbDialog));
        logUsersByIds(qbDialog.getOccupants());
    }

    public static void logUsers(List<QBUser> users) {
        for (QBUser user : users) {
            Log.i(TAG, user.getId() + " " + user.getFullName());
        }
    }

    private static void logUsersByIds(List<Integer> users) {
        for (Integer id : users) {
            QBUser user = QbUsersHolder.getInstance().getUserById(id);
            Log.i(TAG, user.getId() + " " + user.getFullName());
        }
    }

    public static Integer getOpponentIdForPrivateDialog(QBChatDialog dialog) {
        Integer opponentId = -1;
        QBUser qbUser = ChatHelper.getCurrentUser();
        if (qbUser == null) {
            return opponentId;
        }

        Integer currentUserId = qbUser.getId();

        for (Integer userId : dialog.getOccupants()) {
            if (!userId.equals(currentUserId)) {
                opponentId = userId;
                break;
            }
        }
        return opponentId;
    }

    public static Integer[] getUserIds(List<QBUser> users) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (QBUser user : users) {
            ids.add(user.getId());
        }
        return ids.toArray(new Integer[ids.size()]);
    }

    public static String createChatNameFromUserList(List<QBUser> users) {
        String chatName = "";
        QBUser currentUser = ChatHelper.getCurrentUser();
        for (QBUser user : users) {
            if (user.getId().equals(currentUser.getId())) {
                continue;
            }

            String prefix = chatName.equals("") ? "" : ", ";
            chatName = chatName + prefix + user.getFullName();
        }
        return chatName;
    }

    public static String getDialogName(QBChatDialog dialog) {
        if (dialog.getType().equals(QBDialogType.GROUP)) {
            return dialog.getName();
        } else {
            // It's a private dialog, let's use opponent's name as chat name
            Integer opponentId = getOpponentIdForPrivateDialog(dialog);
            QBUser user = QbUsersHolder.getInstance().getUserById(opponentId);
            if (user != null) {
                return TextUtils.isEmpty(user.getFullName()) ? user.getLogin() : user.getFullName();
            } else {
                return dialog.getName();
            }
        }
    }

    private static List<QBUser> getQbUsersFromQbDialog(QBChatDialog dialog) {
        List<QBUser> previousDialogUsers = new ArrayList<>();
        for (Integer id : dialog.getOccupants()) {
            QBUser user = QbUsersHolder.getInstance().getUserById(id);
            if (user == null) {
                throw new RuntimeException("User from dialog is not in memory. This should never happen, or we are screwed");
            }
            previousDialogUsers.add(user);
        }
        return previousDialogUsers;
    }

    public static String getOccupantsIdsStringFromList(Collection<Integer> occupantIdsList) {
        return TextUtils.join(",", occupantIdsList);
    }

    public static List<Integer> getOccupantsIdsListFromString(String occupantIds) {
        List<Integer> occupantIdsList = new ArrayList<>();
        String[] occupantIdsArray = occupantIds.split(",");
        for (String occupantId : occupantIdsArray) {
            occupantIdsList.add(Integer.valueOf(occupantId));
        }
        return occupantIdsList;
    }

}
