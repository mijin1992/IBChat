package com.brasco.simwechat.message;

/**
 * Created by Mikhail on 12/14/2016.
 */

public class Message {
    private String m_Text = "";
    private String m_From = "";
    private String m_To = "";
    private long m_Time = 0;
    private boolean m_isRead = false;

    public Message() {
    }

    public Message(String from, String to, String text, long time) {
        m_From = from;
        m_To = to;
        m_Text = text;
        m_Time = time;
    }

    public String getText() {
        return m_Text;
    }

    public void setText(String text) {
        m_Text = text;
    }

    public String getFrom() {
        return m_From;
    }

    public void setFrom(String from) {
        m_From = from;
    }

    public String getTo() {
        return m_To;
    }

    public void setTo(String to) {
        m_To = to;
    }

    public long getTime() {
        return m_Time;
    }

    public void setTime(long time) {
        m_Time = time;
    }

    public boolean isReadMessage() {
        return m_isRead;
    }

    public void readMessage() {
        m_isRead = true;
    }
}
