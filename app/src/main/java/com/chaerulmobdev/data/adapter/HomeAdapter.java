package com.chaerulmobdev.data.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaerulmobdev.data.adapter.data.WorkData;
import com.workfree.R;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<WorkData> array;

    public HomeAdapter(Context context, ArrayList<WorkData> array) {
        this.context = context;
        this.array = array;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_custom, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        WorkData data = array.get(i);
        if (data != null) {
            viewHolder.setTitle(data.getName());
            viewHolder.setImageGlide(data.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_work);
            image = itemView.findViewById(R.id.image_work);
        }

        public void setTitle(String titleName) {
            title.setText(titleName);
        }

        public void setImageGlide(String url) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.placeholder_image) // Gambar default saat loading
                    .error(R.drawable.ic_img_error) // Gambar default saat terjadi error
                    .into(image);
        }
    }
}
