package com.example.expressmarket.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expressmarket.R;

public class AdapterProductUser {

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;
        private TextView discountedNoteTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            discountedNoteTv = itemView.findViewById(R.id.discountedNoteTv);

        }
    }
}
