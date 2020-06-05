package com.fpretell.demoperuapps.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.fpretell.demoperuapps.ui.main.MainActivity
import com.fpretell.demoperuapps.base.BaseActivity
import com.fpretell.demoperuapps.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import timber.log.Timber

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class SplashActivity : BaseActivity() {

    var mAuthListenr: FirebaseAuth.AuthStateListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuthListenr = FirebaseAuth.AuthStateListener { auth ->

            Handler().postDelayed({

                val user = auth.currentUser


                if (user != null) {

                    Timber.i("sesion iniciada  con telefono : ${user.phoneNumber}")
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()


                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Timber.i("secion cerrada 1 ")
                }


            }, 2000)

        }
    }

    companion object {

        private val TAG = "SplashActivity"
    }

    //seteamos nuestro listener al Api cuando se inicia el activity
    override fun onStart() {
        super.onStart()
        mAuthListenr?.let { FirebaseAuth.getInstance().addAuthStateListener(it) }
    }


    // quitamos el listener cuando se sale del activity
    override fun onStop() {
        super.onStop()
        if (mAuthListenr != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListenr!!)
        }
    }

}
