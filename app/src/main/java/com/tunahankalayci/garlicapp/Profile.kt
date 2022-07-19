package com.tunahankalayci.garlicapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class Profile : AppCompatActivity() {

    private lateinit var profileImage: CircleImageView
    private lateinit var inputUserName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var updateBtn: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mUserRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.input_profile_pic)
        inputUserName = findViewById(R.id.input_user_name)
        inputEmail = findViewById(R.id.input_email)
        updateBtn = findViewById(R.id.btn_update)
        
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user")
        mUserRef.child(mUser.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val profileImageUrl = snapshot.child("profileImageUrl").getValue().toString()
                    val userName = snapshot.child("name").getValue().toString()
                    val userEmail = snapshot.child("email").getValue().toString()

                    Picasso.get().load(profileImageUrl).into(profileImage)
                    inputUserName.setText(userName)
                    inputEmail.setText(userEmail)
                }
                else{
                    Toast.makeText(this@Profile, "Veri çekerken hata oluştu", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}