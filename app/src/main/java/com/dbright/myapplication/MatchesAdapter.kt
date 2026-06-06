package com.dbright.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

class MatchesAdapter(
    private val currentUser: UserProfile,
    private val matches: List<UserProfile>
) : RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {

    private val engine = CompatibilityEngine()

    class MatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: android.widget.ImageView = view.findViewById(R.id.ivProfile)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvOccupation: TextView = view.findViewById(R.id.tvOccupation)
        val tvScore: TextView = view.findViewById(R.id.tvScoreBadge)
        val btnChat: MaterialButton = view.findViewById(R.id.btnChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_match, parent, false)
        return MatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]
        val context = holder.itemView.context

        holder.tvName.text = match.name
        holder.tvOccupation.text = match.occupation
        
        val result = engine.calculateDetailedCompatibility(currentUser, match)
        holder.tvScore.text = "${result.totalScore}% Match"
        
        // Color-coded compatibility according to Usage Guidelines
        val colorRes = when {
            result.totalScore >= 80 -> R.color.match_strong   // Emerald Green
            result.totalScore >= 50 -> R.color.match_moderate // Gold
            else -> R.color.match_weak                         // Rose Quartz
        }
        holder.tvScore.setTextColor(ContextCompat.getColor(context, colorRes))

        Glide.with(context)
            .load(match.profileImageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivProfile)

        holder.btnChat.setOnClickListener {
            Toast.makeText(context, "Opening chat with ${match.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = matches.size
}
