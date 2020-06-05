package com.fpretell.demoperuapps.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.base.BaseActivity
import com.fpretell.demoperuapps.ui.login.LoginActivity
import com.fpretell.demoperuapps.ui.new_post.CreatePostActivity
import com.fpretell.demoperuapps.viewmodels.PostsViewModel
import com.fpretell.peruapps.base.State
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import timber.log.Timber
import java.util.Collections.reverse

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class MainActivity : BaseActivity() {

    private lateinit var mAdapter: PlacesAdapter
    private lateinit var viewModel: PostsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = PlacesAdapter(this)
        viewModel = ViewModelProviders.of(this).get(PostsViewModel::class.java)
        setSupportActionBar(toolbar)

        val lim = LinearLayoutManager(this)
        recyclerView.layoutManager = lim
        recyclerView.adapter = mAdapter
        recyclerView.setHasFixedSize(true)
        loadPubs()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fabPublicacionNueva.visibility == View.VISIBLE) {
                    fabPublicacionNueva.hide()
                } else if (dy < 0 && fabPublicacionNueva.visibility != View.VISIBLE) {
                    fabPublicacionNueva.show()
                }
            }
        })

        fabPublicacionNueva.setOnClickListener {

            val intent = Intent(this@MainActivity,CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cerrar_sesion -> {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("cerrarSesion", true)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                FirebaseAuth.getInstance().signOut()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadPubs() {

        viewModel.getAllPosts().observe(this, androidx.lifecycle.Observer{ result ->
            when(result){
                is State.Loading -> progressBarListPosts.visibility = View.VISIBLE
                is State.Success -> {

                    if(result.data.size > 0) {
                        reverse(result.data)
                        mAdapter.updateData(result.data)
                        progressBarListPosts.visibility = View.GONE
                        tv_sin_posts?.visibility = View.GONE
                    }else{
                        progressBarListPosts.visibility = View.GONE
                        tv_sin_posts?.visibility = View.VISIBLE
                    }
                }
                is State.Failed -> {
                    Timber.e(result.message)
                }
            }

        })
    }

}
