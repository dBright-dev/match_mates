package com.dbright.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class EventsAdapter(
    private val events: List<Event>,
    private val onInterestedClick: (Event) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvEventTitle)
        val tvDetails: TextView = view.findViewById(R.id.tvEventDetails)
        val tvDesc: TextView = view.findViewById(R.id.tvEventDescription)
        val btnJoin: MaterialButton = view.findViewById(R.id.btnJoinEvent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.tvTitle.text = event.title
        holder.tvDetails.text = "${event.type} • ${event.date} • ${event.location}"
        holder.tvDesc.text = event.description
        
        holder.btnJoin.setOnClickListener { onInterestedClick(event) }
    }

    override fun getItemCount() = events.size
}
