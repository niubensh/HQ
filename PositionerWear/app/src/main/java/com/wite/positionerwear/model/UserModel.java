package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/26.
 * 姓名|生日|血型|病史信息|药物信息|过敏反应|紧急联系人信息
 * 张三
 * 19800101
 * O
 * 病史
 * 药物
 * 过敏
 * 紧急联系人
 */

public class UserModel {

    private String Name;
    private String Gender;
    private String Birthday;
    private String Blood;
    private String Medical;

    public UserModel(String name, String gender, String birthday, String blood, String medical, String drug, String anaphylaxis, String emergency) {
        Name = name;
        Gender = gender;
        Birthday = birthday;
        Blood = blood;
        Medical = medical;
        Drug = drug;
        Anaphylaxis = anaphylaxis;
        this.emergency = emergency;
    }

    public String getGender() {

        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    private String Drug;
    private String Anaphylaxis;
    private String emergency;


    public UserModel(String name, String birthday, String blood, String medical, String drug, String anaphylaxis, String emergency) {
        Name = name;
        Birthday = birthday;
        Blood = blood;
        Medical = medical;
        Drug = drug;
        Anaphylaxis = anaphylaxis;
        this.emergency = emergency;
    }

    public UserModel() {

    }

    public String getName() {

        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getBlood() {
        return Blood;
    }

    public void setBlood(String blood) {
        Blood = blood;
    }

    public String getMedical() {
        return Medical;
    }

    public void setMedical(String medical) {
        Medical = medical;
    }

    public String getDrug() {
        return Drug;
    }

    public void setDrug(String drug) {
        Drug = drug;
    }

    public String getAnaphylaxis() {
        return Anaphylaxis;
    }

    public void setAnaphylaxis(String anaphylaxis) {
        Anaphylaxis = anaphylaxis;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }


}
