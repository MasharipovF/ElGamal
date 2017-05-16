package com.example.farrukh.elgamal;


/**
 * Created by developer on 06.05.2017.
 */

public class FileEntity {
    String fileUid;
    String name;
    long size;
    String realAbsolyutPath;

    public FileEntity() {

    }

    public FileEntity(String fileUid, String name, long size) {
        this.fileUid = fileUid;
        this.name = name;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileUid() {
        return fileUid;
    }

    public void setFileUid(String fileUid) {
        this.fileUid = fileUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealAbsolyutPath() {
        return realAbsolyutPath;
    }

    public void setRealAbsolyutPath(String realAbsolyutPath) {
        this.realAbsolyutPath = realAbsolyutPath;
    }
}
