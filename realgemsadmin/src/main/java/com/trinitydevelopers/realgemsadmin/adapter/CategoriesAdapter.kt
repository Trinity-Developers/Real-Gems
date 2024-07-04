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

class CategoriesAdapter (val context: Context,
                         private var gemsList: List<Gems>,
                         private val onItemClick: (String) -> Unit

) : RecyclerView.Adapter<CategoriesAdapter.categories_ViewHolder>() {
    class categories_ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.categories_name)
        val image=itemView.findViewById<ImageView>(R.id.categories_img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): categories_ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.viewholder_categories,parent,false)
        return categories_ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gemsList.size
    }

    override fun onBindViewHolder(holder: categories_ViewHolder, position: Int) {

        val gem = gemsList[position]
        holder.name.text=gem.nameId
        holder.name.isSelected=true
        Picasso.get().load(gem.imageUrls[0]).into(holder.image)

        holder.itemView.setOnClickListener {
            gem.nameId?.let { nameId ->
                onItemClick(nameId)
            }
    }
    }
    fun submitList(newCategories: List<Gems>) {
        gemsList = newCategories
        notifyDataSetChanged()
    }
}
