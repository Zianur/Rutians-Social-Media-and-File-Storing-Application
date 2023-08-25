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

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCommonRoomPost extends RecyclerView.Adapter<AdapterCommonRoomPost.ViewHolder> {

    private Context context;
    private ArrayList<CommonRoomPostModule> commonRoomPostModuleArrayList;

    OnCommonRoomPostClickListener commonRoomPostClickListener;

    public AdapterCommonRoomPost(Context context, ArrayList<CommonRoomPostModule> commonRoomPostModuleArrayList) {
        this.context = context;
        this.commonRoomPostModuleArrayList = commonRoomPostModuleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view  = layoutInflater.inflate(R.layout.common_post_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CommonRoomPostModule commonRoomPostModule = commonRoomPostModuleArrayList.get(position);

        holder.date.setText(commonRoomPostModule.getDate());
        holder.time.setText(commonRoomPostModule.getTime());
        holder.username.setText(commonRoomPostModule.getUserfullname());
        holder.email.setText(commonRoomPostModule.getEmail());
        holder.description.setText(commonRoomPostModule.getDescription());

        Picasso.get()
                .load(commonRoomPostModule.getProfileimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.profileimage);
        Picasso.get()
                .load(commonRoomPostModule.getPostimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.postImage);

    }

    @Override
    public int getItemCount() {
        return commonRoomPostModuleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        CircleImageView profileimage;
        ImageView postImage;
        TextView username, email, date, time, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.common_room_post_user_name);
            email = itemView.findViewById(R.id.common_room_post_user_email);
            date = itemView.findViewById(R.id.common_room_post_date);
            time = itemView.findViewById(R.id.common_room_post_time);
            profileimage = itemView.findViewById(R.id.common_room_post_profile_image);
            description = itemView.findViewById(R.id.common_room_post_description);
            postImage = itemView.findViewById(R.id.common_room_post_image);

            itemView.setOnClickListener(this); //adding onclick for only click
            itemView.setOnCreateContextMenuListener(this); //adding menu for click and hold

        }

        @Override
        public void onClick(View v) {

            if (commonRoomPostClickListener != null)
            {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION)
                {
                    commonRoomPostClickListener.OnCommonRoomPostClick(position);
                }
            }

        }

        // setting how will the menu look
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem editPost = menu.add(Menu.NONE,1,1,"Edit Post");
            MenuItem deletePost = menu.add(Menu.NONE,2,2,"Delete Post");

            editPost.setOnMenuItemClickListener(this);
            deletePost.setOnMenuItemClickListener(this);

        }

        //setting the clicks on menu
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (commonRoomPostClickListener != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {
                        case 1:
                            commonRoomPostClickListener.OnCommonRoomPostEditClick(position);
                            return true;
                        case 2:
                            commonRoomPostClickListener.OnCommonRoomPostDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnCommonRoomPostClickListener
    {
        void OnCommonRoomPostClick(int position); // for just click

        void OnCommonRoomPostEditClick(int position); //for click menu

        void OnCommonRoomPostDeleteClick(int position); ////for click menu

    }

    public void setOnCommonRoomPostClickListener(OnCommonRoomPostClickListener listener) // for setting in main activity
    {
        commonRoomPostClickListener = listener;
    }

}
