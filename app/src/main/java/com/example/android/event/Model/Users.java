package com.example.android.event.Model;

/**
 * Created by asus on 09/05/2017.
 */

public class Users {
    private String Photourl,Nama;
    public boolean Penyedia;

    public Users(){}

    public Users(String photoUrl,String nama,boolean penyedia){
        this.Photourl = photoUrl;
        this.Nama = nama;
        this.Penyedia = penyedia;

    }

    public String getPhotourl(){
        return Photourl;
    }

    public void setPhotourl(String photourl){
        this.Photourl = photourl;
    }

    public String getNama(){ return Nama; }

    public void setNama(String nama){
        this.Nama =nama;
    }


}

