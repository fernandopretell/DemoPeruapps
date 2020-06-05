package com.fpretell.demoperuapps.ui.phone_authentication

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.fpretell.demoperuapps.ui.main.MainActivity
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.base.BaseActivity
import com.fpretell.demoperuapps.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_phone_authentication.*
import kotlinx.android.synthetic.main.dialog_validacion_exitosa.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class PhoneAuthenticationActivity : BaseActivity(){


    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String? = null
    private var credentials: PhoneAuthCredential? = null
    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_authentication)

        val telefono = "+51"+ intent.getStringExtra("telefono")

        verificationCallbacks()

        login(telefono,mCallbacks)

        etCodigoSeguridad.requestFocus()

        btnIngresarAlHome.setOnClickListener {

            try{
                credentials = PhoneAuthProvider.getCredential(storedVerificationId ?: "", etCodigoSeguridad.text.toString().trim())

                showLoading(true)
                credentials?.let { it1 -> signInWithPhoneAuthCredential(it1) }
            }catch (e: Exception){
                Toast.makeText(this,e.message.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificationCallbacks(){
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Timber.i( "onVerificationCompleted:$credential")

                val code: String? = credential.getSmsCode()

                code.let {

                    etCodigoSeguridad.setText(it)

                    btnIngresarAlHome.disabled = true

                    credentials = credential

                    credentials?.let { it1 -> signInWithPhoneAuthCredential(it1) }
                }

            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                showLoading(false)
                Timber.w(e, "onVerificationFailed")

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Timber.i("Invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Timber.i("The SMS quota for the project has been exceeded")
                }

                // Show a message and update the UI
                // ...
            }

            override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
            ) {

                /*El código de verificación de SMS se ha enviado al número de teléfono proporcionado, nosotros
                ahora necesita pedirle al usuario que ingrese el código y luego construir una credencial
                        combinando el código con una identificación de verificación.*/

                Timber.i("onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them latevr
                storedVerificationId = verificationId
                resendToken = token

                //credential = PhoneAuthProvider.getCredential(verificationId, code)

                // ...
            }
        }
    }

    fun login(
            phoneNumber: String,
            callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this@PhoneAuthenticationActivity, // Activity (for callback binding)
                callbacks) // OnVerificationStateChangedCallbacks
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.e("signInWithCredential:success")

                        val userPhone = task.result?.user?.phoneNumber
                        val uid = task.result?.user?.uid
                        val displayName = task.result?.user?.displayName

                        //sinchServiceInterface?.startClient(uid + "-" + displayName)

                        lifecycleScope.launch(Dispatchers.IO){
                            uid?.let { userPhone?.let { it1 -> getUidFirestore(it, it1) } }
                        }


                        // ...
                    } else {
                        showLoading(false)
                        // Sign in failed, display a message and update the UI
                        Timber.e("""signInWithCredential:failure${task.exception}""")
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
    }

    suspend fun getUidFirestore(uid: String,userPhone: String){
        val ifExistsUid = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(uid).get().await()
        if(ifExistsUid.exists()){
            withContext(Dispatchers.Main){
                showDialogValidacionExitosa(this@PhoneAuthenticationActivity,false,uid)
                Timber.i("Si existe user")
            }
        }else{
            setUserDataOnFirestore(uid, userPhone)
            Timber.i("No existe user")
        }
    }

    private suspend fun setUserDataOnFirestore(uid: String?, phone: String?) {

        val hashMap = hashMapOf(
                "phone" to phone,
                "uid" to uid

        )

        try {
            FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(uid?:Constants.UID_NULL).set(hashMap).await()
            withContext(Dispatchers.Main){
                showDialogValidacionExitosa(this@PhoneAuthenticationActivity,true,uid?:Constants.UID_NULL)
            }
        }catch (e: FirebaseFirestoreException){
            Timber.e("Error writing document")
            showLoading(false)
        }



        /*uid?.let {
            FirebaseDatabase.getInstance().getReference("usuarios_v2").child(it)
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        showDialogValidacionExitosa(this@PhoneAuthenticationActivity,true,uid)
                    }.addOnFailureListener { e ->
                        Timber.e("Error writing document")
                        showLoading(false)
                    }
        }*/
    }

    fun showDialogValidacionExitosa(ctx: Context, first_login: Boolean,uid: String) {

        val dialog = Dialog(ctx)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_validacion_exitosa)
        dialog.btnConfirmarValidacionExitosa.setOnClickListener {


                val intent = Intent(ctx, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

        }
        dialog.show()
    }
}
