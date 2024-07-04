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
import com.trinitydevelopers.realgemsadmin.adapter.CategoriesAdapter.categories_ViewHolder
import com.trinitydevelopers.realgemsadmin.pojos.Gems

class ProductAdapter (val context: Context,
                      private var gemsList: List<Gems>,
                      private val itemClickListener: (Gems) -> Unit
) : RecyclerView.Adapter<ProductAdapter.product_ViewHolder>() {
    class product_ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView)  {
        val image=itemView.findViewById<ImageView>(R.id.product_img)
        val name=itemView.findViewById<TextView>(R.id.product_name)
        val carats=itemView.findViewById<TextView>(R.id.product_carats)
        val composition=itemView.findViewById<TextView>(R.id.product_composition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): product_ViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.viewholder_product,parent,false)
        return product_ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gemsList.size
    }

    override fun onBindViewHolder(holder: product_ViewHolder, position: Int) {
        val gem = gemsList[position]
        holder.name.text=gem.nameId
        holder.carats.text=gem.carats.toString()
        holder.composition.text=gem.compositionId
        holder.name.isSelected=true
        Picasso.get().load(gem.imageUrls[0]).into(holder.image)
        holder.itemView.setOnClickListener {
            itemClickListener(gem)
        }
    }
    fun submitList(newCategories: List<Gems>) {
        gemsList = newCategories
        notifyDataSetChanged()
    }
}