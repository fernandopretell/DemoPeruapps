package com.fpretell.demoperuapps.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.util.LoadingView
import com.fpretell.demoperuapps.util.removeFromParent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.android.support.DaggerAppCompatActivity


abstract class BaseActivity: DaggerAppCompatActivity(){

    private var isReceiverRegistered = false
    private val loadingView by lazy { LoadingView(this) }

    lateinit var firestore: FirebaseFirestore
    lateinit var firebaseAuth: FirebaseAuth
    protected lateinit var uid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransitionEnter()
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        if(firebaseAuth.currentUser != null) uid = firebaseAuth.currentUser?.uid!!

        //registerReceiver(ConnectivityReceiver(), IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    fun showProgressDialog(message: String) {
        loadingView.setMessage(message)
        if (loadingView.parent == null) {
            addContentView(loadingView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        }
    }

    fun showLoading(show: Boolean) {
        if (show) showProgressDialog() else dismissProgressDialog()
    }

    fun showProgressDialog() = showProgressDialog(R.string.copy_loading)

    fun dismissProgressDialog() = loadingView.removeFromParent()

    fun showProgressDialog(@StringRes idMessage: Int) = showProgressDialog(getString(idMessage))

    /*override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
        isReceiverRegistered = true
    }*/

    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }



    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.animator.slide_from_right, R.animator.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.animator.slide_from_left, R.animator.slide_to_right)
    }

}