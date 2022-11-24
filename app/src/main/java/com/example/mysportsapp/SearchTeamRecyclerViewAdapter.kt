package com.example.mysportsapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mysportsapp.databinding.SingleTeamBinding

// Takes onClick parameter for handling Team Clicks
public class SearchTeamRecyclerViewAdapter(val onClick: (Team) -> Unit): RecyclerView.Adapter<SearchTeamRecyclerViewAdapter.ViewHolder>() {
    var teams = listOf<Team>()

    // Inner class ktx-bindings for Viewholder based on R.layout.single_team
    inner class ViewHolder(val binding: SingleTeamBinding): RecyclerView.ViewHolder(binding.root)

    // Create View for Recycler from SingleTeamBinding and R.layout.single_team
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = SingleTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    // Applies Team data and onClick to SingleTeam View
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val team = teams[position]
            holder.itemView.setOnClickListener { onClick(team) }
            with(team) {
                binding.teamName.text = name
                binding.sport.text = sport
                binding.league.text = league
                Glide.with(holder.itemView.context)
                    .load(logo)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(binding.teamImage)

            }
        }
    }

    override fun getItemCount(): Int {
       return teams.size
    }

}