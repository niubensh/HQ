package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/28.
 */
//IWBPCD,6654111,D3590D54,080835,3,XXXXXXXXXXXXXXXX#
public class MessageModel {
    private int GuardianModel_id;
    private String Name;
    private int Type;
    private String TextMessage;
    private String MessageInTime;
    private int isread;

    public MessageModel(int guardianModel_id, String name, int type, String textMessage, String messageInTime, int isread) {
        GuardianModel_id = guardianModel_id;
        Name = name;
        Type = type;
        TextMessage = textMessage;
        MessageInTime = messageInTime;
        this.isread = isread;
    }

    public int getIsread() {

        return isread;
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    public MessageModel(int guardianModel_id, String name, int type, String textMessage, String messageInTime) {
        GuardianModel_id = guardianModel_id;
        Name = name;
        Type = type;
        TextMessage = textMessage;
        MessageInTime = messageInTime;
    }

    public String getMessageInTime() {

        return MessageInTime;
    }

    public void setMessageInTime(String messageInTime) {
        MessageInTime = messageInTime;
    }


    public MessageModel(int guardianModel_id, String name, int type, String textMessage) {
        GuardianModel_id = guardianModel_id;
        Name = name;
        Type = type;
        TextMessage = textMessage;
    }

    public MessageModel() {

    }

    public int getGuardianModel_id() {

        return GuardianModel_id;
    }

    public void setGuardianModel_id(int guardianModel_id) {
        GuardianModel_id = guardianModel_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getTextMessage() {
        return TextMessage;
    }

    public void setTextMessage(String textMessage) {
        TextMessage = textMessage;
    }
}
