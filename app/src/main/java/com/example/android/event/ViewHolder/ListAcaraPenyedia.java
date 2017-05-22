package com.example.android.event.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.event.R;

/**
 * Created by asus on 14/05/2017.
 */

public class ListAcaraPenyedia extends RecyclerView.ViewHolder{
    public TextView mTvJudulAcara,mTvTanggal;
    public ImageView dotsEdit;


    public ListAcaraPenyedia(View itemView) {
        super(itemView);
        mTvJudulAcara =(TextView) itemView.findViewById(R.id.tv_judulAcaraPenyedia);
        mTvTanggal=(TextView)itemView.findViewById(R.id.tv_tanggalDanHariPenyedia);
        dotsEdit = (ImageView)itemView.findViewById(R.id.dot_editAcara);

    }
}
