package com.example.android.event.ViewHolder;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.event.R;

/**
 * Created by asus on 16/05/2017.
 */

public class ListAcaraPeserta extends RecyclerView.ViewHolder {
    public TextView mTvJudulAcaraPeserta,mTvOrganisasiPeserta,mTvTanggalPeserta,mTvKapasitasPeserta;
    public ImageView mIvPosterAcaraPeserta;



    public ListAcaraPeserta(View itemView) {
        super(itemView);
        mTvJudulAcaraPeserta = (TextView) itemView.findViewById(R.id.tv_judulAcaraPeserta);
        mTvOrganisasiPeserta = (TextView) itemView.findViewById(R.id.tv_penyelenggaraPeserta);
        mTvTanggalPeserta = (TextView) itemView.findViewById(R.id.tv_tanggalDanHariPeserta);
        mTvKapasitasPeserta = (TextView) itemView.findViewById(R.id.tv_kapasitasPeserta);
        mIvPosterAcaraPeserta = (ImageView) itemView.findViewById(R.id.iv_posterAcaraPeserta);
    }
}
