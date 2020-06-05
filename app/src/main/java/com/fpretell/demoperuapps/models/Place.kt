package com.fpretell.demoperuapps.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Place(
    @PropertyName("id")
    var id: String = "",
    @PropertyName("placeDescription")
    var placeDescription: String = "",
    @PropertyName("imagesPlaces")
    var imagesPlaces: String = "",
    @PropertyName("createdAt")
    var createdAt: Timestamp? = null,
    @PropertyName("titlePlace")
    var titlePlace: String = ""
)