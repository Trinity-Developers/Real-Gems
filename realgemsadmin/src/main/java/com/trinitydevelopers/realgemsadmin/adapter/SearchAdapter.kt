package com.trinitydevelopers.realgemsadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.trinitydevelopers.realgemsadmin.R
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class SearchAdapter (val context: Context, private val gemsList: List<Gems>): RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    class SearchViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        val name=itemView.findViewById<TextView>(R.id.name)
        val cut=itemView.findViewById<TextView>(R.id.cut)
        val origin=itemView.findViewById<TextView>(R.id.origin)
        val shape=itemView.findViewById<TextView>(R.id.shape)
        val composition=itemView.findViewById<TextView>(R.id.composition)
        val treatment=itemView.findViewById<TextView>(R.id.treatment)
        val color=itemView.findViewById<TextView>(R.id.color)
        val carats=itemView.findViewById<TextView>(R.id.carats)
        val imageViews: List<ImageView> = listOf(
            itemView.findViewById(R.id.exploreImage1),
            itemView.findViewById(R.id.exploreImage2),
            itemView.findViewById(R.id.exploreImage3),
            itemView.findViewById(R.id.exploreImage4)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.viewholder_gems,parent,false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gemsList.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val gem = gemsList[position]
        holder.name.text = gem.nameId
        holder.cut.text = gem.cutId
        holder.origin.text = gem.origin
        holder.shape.text = gem.shapeId
        holder.composition.text = gem.compositionId
        holder.treatment.text = gem.treatmentId
        holder.color.text = gem.color
        holder.carats.text = gem.carats.toString()

        gem.imageUrls.let { urls ->
            urls.forEachIndexed { index, url ->
                if (index < holder.imageViews.size) {
                    Picasso.get().load(url).into(holder.imageViews[index])
                }
            }
        }
    }
}