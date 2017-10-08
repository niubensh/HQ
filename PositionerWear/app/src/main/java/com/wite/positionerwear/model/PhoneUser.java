package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/6.
 */

public class PhoneUser {
    public PhoneUser() {
    }


    public PhoneUser(int _id, String name, String letter, String intime, String phonenum) {
         this._id=_id;
        this.name = name;
        this.letter = letter;
        this.intime = intime;
        this.phonenum = phonenum;
    }
  public PhoneUser(String name, String letter, String intime, String phonenum) {

        this.name = name;
        this.letter = letter;
        this.intime = intime;
        this.phonenum = phonenum;
    }


    public void set_id(int _id) {
        this._id = _id;
    }

    private int   _id;

    public int get_id() {
        return _id;
    }

    private String name;
    private String letter;
    private String intime;
    private String phonenum;


    public String getName() {
        return name;
    }

    public String getLetter() {
        return letter;
    }

    public String getIntime() {
        return intime;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }


}
