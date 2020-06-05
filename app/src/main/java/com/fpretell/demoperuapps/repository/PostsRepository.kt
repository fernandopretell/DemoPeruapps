package com.fpretell.demoperuapps.repository

import com.fpretell.demoperuapps.models.Place
import com.fpretell.demoperuapps.util.Constants
import com.fpretell.peruapps.base.State
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class PostsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllPosts(): Flow<State.Success<List<Place>>> = callbackFlow {

        var lista: List<Place>

        val snapshotSuscription = firestore.collection(Constants.COLLECTION_POST)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (!querySnapshot!!.isEmpty && !querySnapshot.metadata.hasPendingWrites()) {
                    lista = arrayListOf()
                    lista = querySnapshot.toObjects(Place::class.java)
                    /*for (snap in querySnapshot.documents) {
                        val lastMessages = snap.toObject(Place::class.java)
                        (lista as ArrayList<Place>).add(lastMessages!!)

                    }*/
                    offer(State.success(lista))
                } else if (querySnapshot.isEmpty) {
                    lista = emptyList<Place>()
                    offer(State.success(lista))
                }
            }

        awaitClose { snapshotSuscription.remove() }

    }


}