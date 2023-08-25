package com.ruet.ruetians;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterLikesAndComments extends RecyclerView.Adapter<AdapterLikesAndComments.ViewHolder> {

    private Context context;
    private ArrayList<LikesAndCommentsModule> likesAndCommentsModuleArrayList;

    public AdapterLikesAndComments(Context context, ArrayList<LikesAndCommentsModule> likesAndCommentsModuleArrayList) {
        this.context = context;
        this.likesAndCommentsModuleArrayList = likesAndCommentsModuleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.all_comments_layout,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LikesAndCommentsModule likesAndCommentsModule = likesAndCommentsModuleArrayList.get(position);

        holder.userName.setText(likesAndCommentsModule.getUserfullname());
        holder.userEmail.setText(likesAndCommentsModule.getEmail());
        holder.comment.setText(likesAndCommentsModule.getComment());
        Picasso.get()
                .load(likesAndCommentsModule.getProfileimage())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.try_later)
                .into(holder.userProfileImage);
    }

    @Override
    public int getItemCount() {
        return likesAndCommentsModuleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userEmail, comment;
        CircleImageView userProfileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.comment_user_name);
            userEmail = itemView.findViewById(R.id.comment_user_email);
            comment = itemView.findViewById(R.id.comment_contents);
            userProfileImage = itemView.findViewById(R.id.comment_profile_image);
        }
    }
}
