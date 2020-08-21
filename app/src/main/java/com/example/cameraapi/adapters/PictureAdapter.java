package com.example.cameraapi.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cameraapi.R;
import com.example.cameraapi.ShowActivity;
import com.example.cameraapi.models.PictureItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**Adapter to set the items in recycler view**/
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    Context context;
    List<PictureItem> pictureItems;

    public PictureAdapter(Context context, List<PictureItem> pictureItems) {
        this.context = context;
        this.pictureItems = pictureItems;
    }


    @NonNull
    @Override
    public PictureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new PictureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PictureAdapter.ViewHolder holder, int position) {
           PictureItem p = pictureItems.get(position);
          holder.textView.setText(p.getName());
        Picasso.get().load(p.getUri()).fit().placeholder(R.drawable.loading).into(holder.imageView);


        if(p.isUploadChecker()==true){
            holder.linearLayout.setVisibility(View.VISIBLE);
        }else{
            holder.linearLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return pictureItems.size();
    }

    //get particular image item
    public PictureItem getItemAt(int position) {return pictureItems.get(position);}

    //Update list method
    public void updateList(List<PictureItem> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallback(this.pictureItems, newList));
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_file);
            textView=itemView.findViewById(R.id.img_name);
            linearLayout=itemView.findViewById(R.id.upload_sign);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PictureItem pictureItem = pictureItems.get(getAdapterPosition());
                    Intent intent = new Intent(context, ShowActivity.class);
                    intent.putExtra("image",pictureItem.getUri().toString());
                    context.startActivity(intent);
                }
            });
        }
    }
}
