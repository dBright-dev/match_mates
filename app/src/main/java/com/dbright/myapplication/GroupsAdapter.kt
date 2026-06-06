package com.dbright.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class GroupsAdapter(
    private val groups: List<InterestGroup>,
    private val onJoinClick: (InterestGroup) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvGroupName)
        val tvCategory: TextView = view.findViewById(R.id.tvGroupCategory)
        val tvDesc: TextView = view.findViewById(R.id.tvGroupDescription)
        val tvMembers: TextView = view.findViewById(R.id.tvMemberCount)
        val btnJoin: MaterialButton = view.findViewById(R.id.btnJoinGroup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.tvName.text = group.name
        holder.tvCategory.text = group.category
        holder.tvDesc.text = group.description
        holder.tvMembers.text = "${group.memberIds.size} members"
        
        holder.btnJoin.setOnClickListener { onJoinClick(group) }
    }

    override fun getItemCount() = groups.size
}
