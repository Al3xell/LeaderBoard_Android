package com.example.projet.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.R;
import com.example.projet.TournamentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TournamentSearch extends RecyclerView.Adapter<TournamentSearch.ViewHolder> implements Filterable {

    public ArrayList<TournamentModel> tournamentList;
    public ArrayList<TournamentModel> tournamentListAll;
    private final RecyclerViewClickListener listener;
    public ArrayList<TournamentModel> teamsList;

    public TournamentSearch(ArrayList<TournamentModel> listTournament, RecyclerViewClickListener listener){
        this.tournamentList = listTournament;
        this.tournamentListAll = new ArrayList<>(listTournament);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vertical_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.display(tournamentList.get(position));
    }


    @Override
    public int getItemCount() {
        return tournamentList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<TournamentModel> filteredTournament = new ArrayList<>();
            if(charSequence.toString().isEmpty()) {
                filteredTournament.addAll(tournamentListAll);
            }
            else {
                for(TournamentModel tournament : tournamentListAll) {
                    if(tournament.nameTournament.toLowerCase(Locale.ROOT).trim().contains(charSequence.toString().toLowerCase(Locale.ROOT).trim())) {
                        filteredTournament.add(tournament);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredTournament;
            return filterResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            tournamentList.clear();
            tournamentList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageTournament;
        TextView nameText, startText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageTournament = itemView.findViewById(R.id.imageTeam);
            nameText = itemView.findViewById(R.id.nameTeamTeam);
            startText = itemView.findViewById(R.id.numberTeamTeam);
            itemView.setOnClickListener(this);
        }

        public void display(TournamentModel tournament) {
            nameText.setText(tournament.nameTournament);
            startText.setText(tournament.startDate);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }
}
