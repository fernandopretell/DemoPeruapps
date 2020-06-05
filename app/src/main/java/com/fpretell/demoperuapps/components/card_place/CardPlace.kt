package com.fpretell.demoperuapps.components.card_place

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.fpretell.demoperuapps.R
import com.google.gson.Gson
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.card_place.view.*


class  CardPlace:LinearLayout{


    val bitmap = arrayOfNulls<Bitmap>(1)

    private var placeholderProduct: Drawable? = ContextCompat.getDrawable(context,
        R.drawable.placeholder_product
    )


    // CONSTRUCTOR
    constructor(context: Context): super(context) {
        LinearLayout.inflate(context,
            R.layout.card_place, this)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        LinearLayout.inflate(context,
            R.layout.card_place, this)
        setupAttrs(context, attrs)
    }

    private fun setupAttrs(context: Context, attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs,
            R.styleable.CardPlace, 0, 0)
        try {

        } finally {
            ta.recycle()
        }
    }

    fun setValues(
        nameUser: String,
        datetime: String,
        placeDescription: String,
        urlImageProduct: String
    ) {
        val phProduct = placeholderProduct
        phProduct?.let { loadImage(carouselView, it, urlImageProduct) }

        tvNameUser.text = nameUser
        tvFechaPublicacion.text = datetime
        tvPlaceDescription.text = placeDescription
    }

    private fun loadImage(
        carouselView: CarouselView,
            ph: Drawable,
            urls: String
    ) {

        val imagenesList = Gson().fromJson(urls, Array<String>::class.java).toList()

        carouselView.setPageCount(imagenesList.size)
        val imageListener: ImageListener = object : ImageListener {

            @Override
            override fun setImageForPosition(position: Int, imageView: ImageView) {

                imageView.scaleType = ImageView.ScaleType.CENTER


                Glide.with(context)
                    .load(imagenesList[position])
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.placeholder_product))
                    .into(imageView)


            }
        }
        carouselView.setImageListener(imageListener)

    }
}


