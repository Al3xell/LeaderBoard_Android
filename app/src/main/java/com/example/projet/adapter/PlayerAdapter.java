package com.example.projet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.TeamModel;
import com.example.projet.UserModel;
import com.firebase.ui.auth.data.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder>{

    public ArrayList<UserModel> usersList;

    private final PlayerAdapter.RecyclerViewClickListener listener;

    public PlayerAdapter(ArrayList<UserModel> usersList,PlayerAdapter.RecyclerViewClickListener listener) {
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.player_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.display(usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView playerImage;
        private final TextView namePlayer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playerImage  = itemView.findViewById(R.id.imagePlayer);
            namePlayer   = itemView.findViewById(R.id.namePlayerPlayer);
            itemView.setOnClickListener(this);

        }
        @SuppressLint("SetTextI18n")
        public void display(UserModel userModel) {
            namePlayer.setText(userModel.getFirstName()+" "+userModel.getLastName());
            if(userModel.getUri().equals("default")) {
                playerImage.setImageResource(R.drawable.tournament);
            }
            else {
                Picasso.get().load(userModel.getUri()).into(playerImage);
            }
        }
        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }
}
