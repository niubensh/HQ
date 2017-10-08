package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/26.
 */

public class MissCallInfo {
    private  String callLogID;
    private String callName;
    private String callNumber;
    private String callType;
    private String callDate;

    public String getIsCallNew() {
        return isCallNew;
    }

    public void setIsCallNew(String isCallNew) {
        this.isCallNew = isCallNew;
    }

    public MissCallInfo(String isCallNew) {
        this.isCallNew = isCallNew;
    }

    public MissCallInfo(String callLogID, String callName, String callNumber, String callType, String callDate, String isCallNew) {
        this.callLogID = callLogID;
        this.callName = callName;
        this.callNumber = callNumber;
        this.callType = callType;
        this.callDate = callDate;
        this.isCallNew = isCallNew;
    }

    private String isCallNew;

    public MissCallInfo() {

    }

    public MissCallInfo(String callName, String callNumber, String callType, String callDate) {

        this.callName = callName;
        this.callNumber = callNumber;
        this.callType = callType;
        this.callDate = callDate;
    }



    public String getCallLogID() {
        return callLogID;
    }

    public void setCallLogID(String callLogID) {
        this.callLogID = callLogID;
    }
    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

}
