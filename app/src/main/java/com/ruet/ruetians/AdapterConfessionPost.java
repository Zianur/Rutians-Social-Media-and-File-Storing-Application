package com.ruet.ruetians;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterConfessionPost extends RecyclerView.Adapter<AdapterConfessionPost.ViewHolder> {

    private Context context;
    private ArrayList<ConfessionPostModule>confessionPostModuleArrayList;

    OnConfessionPostClickListener confessionPostClickListener;

    public AdapterConfessionPost(Context context, ArrayList<ConfessionPostModule> confessionPostModuleArrayList) {
        this.context = context;
        this.confessionPostModuleArrayList = confessionPostModuleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.confession_post_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ConfessionPostModule confessionPostModule = confessionPostModuleArrayList.get(position);

        holder.date.setText(confessionPostModule.getDate());
        holder.time.setText(confessionPostModule.getTime());
        holder.description.setText(confessionPostModule.getDescription());
        Picasso.get()
                .load(confessionPostModule.getConfessionimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.confessionImage);



    }

    @Override
    public int getItemCount() {
        return confessionPostModuleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView date, time, description;
        ImageView confessionImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.confessionpost_date);
            time = itemView.findViewById(R.id.confessionpost_time);
            description = itemView.findViewById(R.id.confessionpost_description);
            confessionImage = itemView.findViewById(R.id.confessionpost_image);

            itemView.setOnClickListener(this); //adding onclick for only click
            itemView.setOnCreateContextMenuListener(this); //adding menu for click and hold

        }

        @Override
        public void onClick(View v) {

            if (confessionPostClickListener != null)
            {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION)
                {
                    confessionPostClickListener.OnConfessionPostClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem editPost = menu.add(Menu.NONE,1,1,"Edit Post");
            MenuItem deletePost = menu.add(Menu.NONE,2,2,"Delete Post");

            editPost.setOnMenuItemClickListener(this);
            deletePost.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (confessionPostClickListener != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {
                        case 1:
                            confessionPostClickListener.OnConfessionPostEditClick(position);
                            return true;
                        case 2:
                            confessionPostClickListener.OnConfessionPostDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }


    public interface OnConfessionPostClickListener
    {
        void OnConfessionPostClick(int position); // for just click

        void OnConfessionPostEditClick(int position); //for click menu

        void OnConfessionPostDeleteClick(int position); ////for click menu

    }

    public void setOnConfessionPostClickListener(OnConfessionPostClickListener listener) // for setting in main activity
    {
        confessionPostClickListener = listener;
    }
}
