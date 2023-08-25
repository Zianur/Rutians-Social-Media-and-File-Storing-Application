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

public class AdapterMemePost extends RecyclerView.Adapter<AdapterMemePost.ViewHolder> {

    private Context context;
    private ArrayList<MemePostModule> memePostModuleArrayList;

    OnMemePostClickListener onMemePostClickListener;

    public AdapterMemePost(Context context, ArrayList<MemePostModule> memePostModuleArrayList) {
        this.context = context;
        this.memePostModuleArrayList = memePostModuleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view  = layoutInflater.inflate(R.layout.memepost_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MemePostModule memePostModule = memePostModuleArrayList.get(position);

        holder.date.setText(memePostModule.getDate());
        holder.time.setText(memePostModule.getTime());
        holder.username.setText(memePostModule.getUserfullname());
        holder.email.setText(memePostModule.getEmail());
        holder.description.setText(memePostModule.getDescription());
        Picasso.get()
                .load(memePostModule.getProfileimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.profileimage);
        Picasso.get()
                .load(memePostModule.getMemeimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.memeimage);





    }

    @Override
    public int getItemCount() {
        return memePostModuleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        CircleImageView profileimage;
        ImageView memeimage;
        TextView username, email, date, time, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.memepost_user_name);
            email = itemView.findViewById(R.id.memepost_user_email);
            date = itemView.findViewById(R.id.memepost_date);
            time = itemView.findViewById(R.id.memepost_time);
            profileimage = itemView.findViewById(R.id.memepost_profile_image);
            description = itemView.findViewById(R.id.memepost_description);
            memeimage = itemView.findViewById(R.id.memepost_image);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View v) {

            if (onMemePostClickListener != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    onMemePostClickListener.OnMemePostClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem editMeme = menu.add(Menu.NONE,1,1,"Edit Post");
            MenuItem deleteMeme = menu.add(Menu.NONE,2,2,"Delete Post");

            editMeme.setOnMenuItemClickListener(this);
            deleteMeme.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (onMemePostClickListener != null)
            {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    switch (item.getItemId())
                    {
                        case 1:
                            onMemePostClickListener.OnMemeEditClick(position);
                            return true;
                        case 2:
                            onMemePostClickListener.OnMemeDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public interface OnMemePostClickListener{

        void OnMemePostClick(int position);

        void OnMemeEditClick(int position);

        void OnMemeDeleteClick(int position);

    }

    public void setOnMemePostClickListener(OnMemePostClickListener listener)
    {
        onMemePostClickListener = listener;
    }
}
