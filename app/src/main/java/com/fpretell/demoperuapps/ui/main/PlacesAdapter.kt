package com.fpretell.demoperuapps.ui.main


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.components.card_place.CardPlace
import com.fpretell.demoperuapps.models.Place
import java.text.SimpleDateFormat
import java.util.*


class PlacesAdapter(private val ctx: Context) : RecyclerView.Adapter<PlacesAdapter.PublicacionesViewHolder>() {

    private val item: ArrayList<Place> = arrayListOf()

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): PublicacionesViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_place, viewGroup, false)
        return PublicacionesViewHolder(v)
    }

    override fun onBindViewHolder(publicacionesViewHolder: PublicacionesViewHolder, position: Int) {

        val place = item[position]
        val sfd = SimpleDateFormat("dd 'de' MMMM yyyy',' HH:mm", Locale.getDefault())
        val date = sfd.format(place.createdAt?.toDate()!!)
        place.titlePlace.let { publicacionesViewHolder.cardPlace.setValues(it,date,place.placeDescription,place.imagesPlaces) }
    }

    fun updateData(newData: List<Place>) {
        item.clear()
        item.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return item.size
    }

    inner class PublicacionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val cardPlace: CardPlace

        init {
            cardPlace = itemView.findViewById(R.id.cardPlace)
        }
    }
}
