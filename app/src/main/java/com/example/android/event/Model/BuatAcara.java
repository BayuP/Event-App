package com.example.android.event.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by asus on 02/05/2017.
 */

public class BuatAcara {

    private String uid,PenyediaAcara,Judul,Deskripsi,Waktu,Tempat,Organisasi,PhotoAcara;
   private int Kapasitas;

    public BuatAcara(){}

    public BuatAcara(String uid,String PenyediaAcara,String Judul,String Deskripsi,String Waktu,String Tempat,String Organisasi,int Kapasitas ,String photoacara){
        this.uid =uid;
        this.PenyediaAcara = PenyediaAcara;
        this.Judul = Judul;
        this.Deskripsi = Deskripsi;
        this.Waktu = Waktu;
        this.Tempat = Tempat;
        this.Organisasi = Organisasi;
        this.Kapasitas = Kapasitas;
        this.PhotoAcara = photoacara;


    }
    public Map<String,Object> toMap(){
        HashMap<String,Object>map = new HashMap<>();
        map.put("uid",uid);
        map.put("Penyedia",PenyediaAcara);
        map.put("Judul",Judul);
        map.put("Deskripsi",Deskripsi);
        map.put("Waktu",Waktu);
        map.put("Tempat",Tempat);
        map.put("Organisasi",Organisasi);
        map.put("Kapasitas",Kapasitas);
        map.put("PhotoAcara",PhotoAcara);
        return map;
    }
    public String getuid(){return  this.uid;}

    public void setuid(String uid){this.uid = uid;}

    public String getPenyediaAcara(){return  this.PenyediaAcara;}

    public void setPenyediaAcara(String PenyediaAcara){this.PenyediaAcara = PenyediaAcara;}

    public String getJudul(){ return this.Judul;}

    public void setJudul(String Judul){this.Judul = Judul;}

    public String getDeskripsi(){return this.Deskripsi;}

    public void setDeskripsi(String Deskripsi){this.Deskripsi = Deskripsi;}

    public String getTempat(){return this.Tempat;}

    public void setTempat(String Tempat){this.Tempat = Tempat;}

    public String getWaktu(){return this.Waktu;}

    public void setWaktu(String Waktu){this.Waktu = Waktu;}

    public String getOrganisasi(){return this.Organisasi;}

    public void setOrganisasi(String Organisasi){this.Organisasi = Organisasi;}

    public String getPhotoAcara(){return this.PhotoAcara;}

   public void setPhotoAcara(String PhotoAcara){this.PhotoAcara = PhotoAcara;}

    public int getKapasitas(){return this.Kapasitas;}

   public void  setKapasitas(int Kapasitas){this.Kapasitas = Kapasitas;}


}
