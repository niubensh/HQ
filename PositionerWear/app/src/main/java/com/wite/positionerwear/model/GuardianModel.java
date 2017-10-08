package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/26.
 * 6654111|133xxxxxxxx|D3590D54|0:ID(用户唯一标识ID)|电话|昵称(名称使用UNICODE编码直接下发byte)|关系
 */

public class GuardianModel {

    private  int id;
    private  String GuardianPhone;
    private  String GuardianName;



    public GuardianModel(int id, String guardianPhone, String guardianName) {
        this.id = id;
        GuardianPhone = guardianPhone;
        GuardianName = guardianName;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGuardianPhone() {
        return GuardianPhone;
    }

    public void setGuardianPhone(String guardianPhone) {
        GuardianPhone = guardianPhone;
    }

    public String getGuardianName() {
        return GuardianName;
    }

    public void setGuardianName(String guardianName) {
        GuardianName = guardianName;
    }

    public GuardianModel() {

    }


}
