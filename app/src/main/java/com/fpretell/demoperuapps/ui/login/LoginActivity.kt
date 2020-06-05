package com.fpretell.demoperuapps.ui.login

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat

import android.os.Bundle

import android.widget.Toast
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.base.BaseActivity
import com.fpretell.demoperuapps.ui.phone_authentication.PhoneAuthenticationActivity

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class LoginActivity : BaseActivity(){

    private val MY_PERMISSIONS_REQUEST_LOCATION = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        verifyStoragePermissions(this)

        etTelefonoLogin.requestFocus()

        btnLogin.setOnClickListener {

            val telefono = etTelefonoLogin.text.toString()

            if(telefono.length == 9){
                val intent = Intent(this, PhoneAuthenticationActivity::class.java)
                intent.putExtra("telefono", etTelefonoLogin.text.toString())
                //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }else Toast.makeText(this@LoginActivity,"Verifica tu numero de teléfono",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Gracias por darnos los permisos.", Toast.LENGTH_LONG).show()
            } else {
                //runtime_permissions();

            }
        }
    }

    private fun verifyStoragePermissions(activity: Activity): Boolean {
        val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.ACCESS_FINE_LOCATION)
        val REQUEST_EXTERNAL_STORAGE = 1
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
            return false
        } else {
            return true
        }
    }

    companion object {
        private val TAG = LoginActivity::class.java.simpleName
    }

}
