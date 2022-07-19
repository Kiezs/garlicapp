package com.tunahankalayci.garlicapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class SingUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSingUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var prflButton: Button
    private lateinit var slctImg: CircleImageView
    private var selectedPhotoUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sing_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        edtName = findViewById(R.id.edt_name_singup)
        edtEmail = findViewById(R.id.edt_email_singup)
        edtPassword = findViewById(R.id.edt_sifre_singup)
        btnSingUp = findViewById(R.id.btn_sing)
        prflButton = findViewById(R.id.profile_button)
        slctImg = findViewById(R.id.select_image)


        prflButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        btnSingUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()


            singin(name, email, password)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){

            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            slctImg.setImageBitmap(bitmap)
            prflButton.alpha = 0f

        }
    }
    private fun singin(name: String, email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadImageToDatabase(name, email,)
                    val intent = Intent(this@SingUp, MainActivity::class.java)
                    finish()
                    startActivity(intent)

                } else {
                    Toast.makeText(this@SingUp, "bir terslik var", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun uploadImageToDatabase(name: String, email: String) {
        if(selectedPhotoUri == null) return

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { Log.d("SignUp","Successfully uploaded image:${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("SingUp","$it")

                    addUserToDatabase(name, email, mAuth.currentUser?.uid!!,it.toString())
                }
            }



    }

    private fun addUserToDatabase(name: String, email: String, uid: String, profileImageUrl: String) {

        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(Users(name, email, uid, profileImageUrl))
    }
}