package com.example.android.event.ViewHolder;

import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.event.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by asus on 24/05/2017.
 */

public class ListKehadiranPeserta extends RecyclerView.ViewHolder{

    public CircleImageView mCiPeserta;
    public TextView mTvNamaPeserta;
    public ImageView mIvHapusPeserta;

    public ListKehadiranPeserta(View itemView) {
        super(itemView);

        mCiPeserta = (CircleImageView) itemView.findViewById(R.id.ic_fotoPesertaDetailAcara);
        mTvNamaPeserta = (TextView)itemView.findViewById(R.id.tv_namaPeserta);
        mIvHapusPeserta = (ImageView)itemView.findViewById(R.id.iv_hapusPeserta);
    }
}
