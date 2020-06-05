package com.fpretell.demoperuapps.ui.new_post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.util.PhotoFullPopupWindow
import java.util.*

class ImagenesNewPostAdapter(val ctx: Context): RecyclerView.Adapter<ImagenesNewPostAdapter.ImagenesCotizacionViewHolder>() {

    private val item: ArrayList<String> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenesCotizacionViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_imagen_publicacion_market, parent, false)
        return ImagenesCotizacionViewHolder(v)
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ImagenesCotizacionViewHolder, position: Int) {
        val imagen:String = item.get(position)
        Glide.with(ctx)
            .load(imagen)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder_product))
            .into(holder.ivImagen)

        holder.icDelete.setOnClickListener {
            item.removeAt(position)
            notifyDataSetChanged()
        }

        holder.ivImagen.setOnClickListener {
            PhotoFullPopupWindow(ctx, R.layout.popup_photo_full, it, imagen, null)
        }
    }

    inner class ImagenesCotizacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val icDelete: ImageView
        val ivImagen: ImageView

        init {

            icDelete = itemView.findViewById(R.id.ivDeleteFotoCotizacion)
            ivImagen = itemView.findViewById(R.id.ivFotoCotizacion)
        }
    }

    fun updateData(newData: List<String>) {
        item.clear()
        item.addAll(newData)
        notifyDataSetChanged()
    }


}