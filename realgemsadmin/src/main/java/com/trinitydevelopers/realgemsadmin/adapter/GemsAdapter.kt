package com.trinitydevelopers.realgemsadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class GemsAdapter(val context: Context, private val gemsList: List<Gems>): RecyclerView.Adapter<GemsAdapter.GemsViewHolder>() {
    class GemsViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val name=itemView.findViewById<TextView>(R.id.name)
        val cut=itemView.findViewById<TextView>(R.id.cut)
        val origin=itemView.findViewById<TextView>(R.id.origin)
        val shape=itemView.findViewById<TextView>(R.id.shape)
        val types=itemView.findViewById<TextView>(R.id.type)
        val composition=itemView.findViewById<TextView>(R.id.composition)
        val treatment=itemView.findViewById<TextView>(R.id.treatment)
        val color=itemView.findViewById<TextView>(R.id.color)
        val carats=itemView.findViewById<TextView>(R.id.carats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GemsViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.viewholder_gems,parent,false)
        return GemsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gemsList.size
    }

    override fun onBindViewHolder(holder: GemsViewHolder, position: Int) {
        holder.name.text=gemsList[position].name
        holder.cut.text=gemsList[position].cut
        holder.origin.text=gemsList[position].origin
        holder.shape.text=gemsList[position].shape
        holder.types.text=gemsList[position].type
        holder.composition.text=gemsList[position].composition
        holder.treatment.text=gemsList[position].treatment
        holder.color.text=gemsList[position].color
        holder.carats.text=gemsList[position].carats
    }
}