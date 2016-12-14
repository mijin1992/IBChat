package com.brasco.simwechat.contact;

/**
 * Created by Mikhail on 12/14/2016.
 */

public class Contact {

    private String m_UserId = "";
    private String m_UserName = "";
    private int m_Gender = 0;
    private int m_Region = 0;
    private String m_WhatsUp = "";

    public Contact() {
    }

    public Contact(String userId) {
        m_UserId = userId;
    }

    public Contact(String userId, String userName) {
        m_UserId = userId;
        m_UserName = userName;
    }

    public String getUserId() {
        return m_UserId;
    }

    public void setUserId(String userid) {
        m_UserId = userid;
    }

    public String getUserName() {
        return m_UserName;
    }

    public void setUserName(String username) {
        m_UserName = username;
    }

    public int getGender() {
        return 0;
    }

    public void setGender(int gender) {
        m_Gender = gender;
    }

    public int getRegion() {
        return m_Region;
    }

    public void setRegion(int region) {
        m_Region = region;
    }

    public String getWhatsUp() {
        return m_WhatsUp;
    }

    public void setWhatsUp(String whatsUp) {
        m_WhatsUp = whatsUp;
    }
}
