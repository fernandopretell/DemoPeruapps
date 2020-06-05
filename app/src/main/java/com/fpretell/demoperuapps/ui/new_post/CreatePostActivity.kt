package com.fpretell.demoperuapps.ui.new_post

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.fpretell.demoperuapps.R
import com.fpretell.demoperuapps.base.BaseActivity
import com.fpretell.demoperuapps.ui.main.MainActivity
import com.fpretell.demoperuapps.util.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class CreatePostActivity : BaseActivity(){

    private lateinit var  images : ArrayList<Image>
    private lateinit var adapter: ImagenesNewPostAdapter
    private lateinit var listStringImages: ArrayList<String>
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    var gson: Gson? = null
    var array = arrayListOf<File>()
    var sizeList: Int? =null
    var urlsList = arrayListOf<String>()
    private var totalSizeList: Int? = null
    private var countImageUpload = 0
    var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        pDialog = ProgressDialog(this)
        pDialog.let { it?.setMessage("Espere por favor...") }
        pDialog.let { it?.setCancelable(false) }
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child(Constants.STORAGE_IMAGES_POST)
        initRecyclerView(this)
        images = arrayListOf()

        frameLayout2.setOnClickListener {
            ImagePicker.create(this)
                .limit(2)
                .toolbarImageTitle("Selecciona sus imagenes")
                .showCamera(true)
                .imageDirectory("Camera")
                .multi()
                .toolbarDoneButtonText("Usar fotos")
                .theme(R.style.AppTheme_Toolbar_picker_images)
                .origin(images)
                .start(1)
        }

        btnCrearPublicacionMarket.setOnClickListener {
            val nombreUser = etTitlePlace.text.toString()
            val descriptionPlace = etDescripcionPlace.text.toString()
            if (nombreUser.equals("")) {
                Toast.makeText(this@CreatePostActivity, "Debes colocar un nombre", Toast.LENGTH_SHORT).show()
            }else if (descriptionPlace.equals("")) {
                Toast.makeText(this@CreatePostActivity, "Debes colocar una descripción", Toast.LENGTH_SHORT).show()
            }else if (adapter.itemCount == 0) {
                Toast.makeText(this@CreatePostActivity, "Debes seleccionar imagenes", Toast.LENGTH_SHORT).show()
            }else{

                showLoading(true)
                lifecycleScope.launch(Dispatchers.IO) {
                    subirImagenes(uid)
                }
            }
        }
    }

    private suspend fun setearImagenes(imagenes: String) {

        gson = Gson()

        val imagenesList: List<Image> = Gson().fromJson(imagenes, Array<Image>::class.java).toList()

        array = ArrayList()

        for (path in imagenesList) {
            array.add(Compressor.compress(this@CreatePostActivity, File(path.path)){
                default(width = 360, format = Bitmap.CompressFormat.WEBP,quality = 50)
            })
        }

        sizeList = array.size
        totalSizeList = sizeList
    }

    private suspend fun subirImagenes(uid: String){

        val idPub = UUID.randomUUID().toString()

        val ref = storageReference.child(UUID.randomUUID().toString())


        if(sizeList == 0){

            val hashMap = hashMapOf(
                "id" to idPub,
                "placeDescription" to etDescripcionPlace.text.toString(),
                "imagesPlaces" to gson?.toJson(urlsList),
                "createdAt" to FieldValue.serverTimestamp(),
                "titlePlace" to etTitlePlace.text.toString()
            )

            firestore.collection(Constants.COLLECTION_POST).document(idPub).set(hashMap).addOnSuccessListener {
                showLoading(false)
                val intent = Intent(this@CreatePostActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }else{
            //uploading the image //
            val uploadTask = ref.putFile(Uri.fromFile(array.get(array.size - sizeList!!)))

            withContext(Dispatchers.Main) {
                uploadTask.addOnProgressListener {
                    if(it.bytesTransferred == it.totalByteCount){
                        countImageUpload++
                    }

                    pDialog?.setMessage("Subiendo publicación $countImageUpload/$totalSizeList")

                }
            }

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    urlsList.add(downloadUri.toString())
                    sizeList = sizeList?.dec()
                    lifecycleScope.launch(Dispatchers.IO) {
                        subirImagenes(uid)
                    }

                }
            }
        }
    }

    private fun initRecyclerView(ctx: Context) {
        val lim = LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL,false)
        adapter = ImagenesNewPostAdapter(ctx)
        rvImagenesPublicacionMarket.layoutManager = lim
        rvImagenesPublicacionMarket.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == RESULT_OK) {

            images = ImagePicker.getImages(data) as ArrayList<Image>

            listStringImages = arrayListOf()
            images.forEach {
                val Imagen:String =it.path
                listStringImages.add(Imagen)
            }
            adapter.updateData(listStringImages)
            rvImagenesPublicacionMarket.visibility = View.VISIBLE
            frameLayout2.visibility = View.INVISIBLE
            //setearImagenes(Gson().toJson(images))
            lifecycleScope.launch(Dispatchers.Main) {
                setearImagenes(Gson().toJson(images))
            }

            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
