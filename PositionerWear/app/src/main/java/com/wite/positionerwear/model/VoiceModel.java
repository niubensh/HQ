package com.wite.positionerwear.model;

/**
 * Created by Administrator on 2017/9/27.
 */

public class VoiceModel {

    private int GuardianModel_id;
    private String Name;
    private String VoiceFile;

    public VoiceModel(int guardianModel_id, String name, String voiceFile, int isread) {
        GuardianModel_id = guardianModel_id;
        Name = name;
        VoiceFile = voiceFile;
        this.isread = isread;
    }

    public int getIsread() {

        return isread;
    }

    public void setIsread(int isread) {
        this.isread = isread;
    }

    private int isread;

    public VoiceModel(int guardianModel_id, String name, String voiceFile) {
        GuardianModel_id = guardianModel_id;
        Name = name;
        VoiceFile = voiceFile;
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

    public String getVoiceFile() {
        return VoiceFile;
    }

    public void setVoiceFile(String voiceFile) {
        VoiceFile = voiceFile;
    }

    public VoiceModel() {

    }


}
