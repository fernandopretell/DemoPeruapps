package com.fpretell.demoperuapps.viewmodels

import androidx.lifecycle.*
import com.fpretell.demoperuapps.models.Place
import com.fpretell.demoperuapps.repository.PostsRepository
import com.fpretell.peruapps.base.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
class PostsViewModel : ViewModel() {

    private val repository = PostsRepository()


    fun getAllPosts() = liveData<State<List<Place>>>(Dispatchers.IO){

        emit(State.loading())

        try{
            repository.getAllPosts().collect{
                emit(it)
            }

        }catch (e: Exception){
            emit(State.failed(e.message!!))
            Timber.e(e)
        }
    }


}