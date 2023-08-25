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

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder> {

    private Context context;
    private ArrayList<SearchModule>searchModuleArrayList;

    public AdapterSearch(Context context, ArrayList<SearchModule> searchModuleArrayList) {
        this.context = context;
        this.searchModuleArrayList = searchModuleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view  = layoutInflater.inflate(R.layout.all_user_display,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SearchModule searchModule = searchModuleArrayList.get(position);

        holder.userName.setText(searchModule.getUserfullname());
        holder.userEmail.setText(searchModule.getEmail());
        holder.userRoll.setText("Roll - " + searchModule.getRoll());
        holder.userSeries.setText("Series - " + searchModule.getSeries());
        holder.aboutUser.setText(searchModule.getAboutuser());
        holder.userPhoneNumber.setText("Tel - " + searchModule.getPhonenumber());
        Picasso.get()
                .load(searchModule.getProfileimage())
                .placeholder(R.drawable.profile)
                .error(R.drawable.try_later)
                .fit()
                .into(holder.searchProfileImage);

    }

    @Override
    public int getItemCount() {
        return searchModuleArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

         CircleImageView searchProfileImage;
         TextView userName, userEmail, userPhoneNumber, userRoll, userSeries, aboutUser;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            searchProfileImage = itemView.findViewById(R.id.search_profile_image);
            userName = itemView.findViewById(R.id.search_user_name);
            userEmail = itemView.findViewById(R.id.search_user_email);
            userPhoneNumber = itemView.findViewById(R.id.search_user_phone_number);
            userRoll = itemView.findViewById(R.id.search_user_roll);
            userSeries = itemView.findViewById(R.id.search_user_series);
            aboutUser = itemView.findViewById(R.id.search_about_user);

        }
    }
}
