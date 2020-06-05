package com.fpretell.demoperuapps.viewmodels

import androidx.lifecycle.ViewModel
import com.fpretell.demoperuapps.models.Place
import com.fpretell.demoperuapps.repository.CreatePostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CreatePostViewModel : ViewModel() {

    private val repository = CreatePostRepository()

    fun setPost(place: Place) = repository.setCreatePostRepository(place)
}