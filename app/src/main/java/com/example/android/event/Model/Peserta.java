package com.example.android.event.Model;

/**
 * Created by asus on 27/05/2017.
 */

public class Peserta {
    private String PhotoUrlPeserta,NamaPeserta;

    public Peserta(){}

    public Peserta(String photoUrlPeserta,String namaPeserta){

        this.PhotoUrlPeserta = photoUrlPeserta;
        this.NamaPeserta = namaPeserta;

        }

        public String getPhotoUrlPeserta(){return PhotoUrlPeserta;}

        public void setPhotoUrlPeserta(String photoUrlPeserta){this.PhotoUrlPeserta = photoUrlPeserta;}

        public String getNamaPeserta(){return NamaPeserta;}

        public void setNamaPeserta(String namaPeserta){this.NamaPeserta = namaPeserta;}

}
