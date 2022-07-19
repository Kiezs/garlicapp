package com.tunahankalayci.garlicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class ViewFriendRealActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUser: FirebaseUser
    private lateinit var mUserRef: DatabaseReference
    private lateinit var requestRef: DatabaseReference
    private lateinit var friendRef: DatabaseReference
    private lateinit var crUserName: String
    private lateinit var crUserEmail: String
    private lateinit var crUserProfileImageUrl: String
    private lateinit var profileImageUrl: String
    private lateinit var userName: String
    private lateinit var userEmail: String
    private lateinit var profileImage: CircleImageView
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var btnPerform: Button
    private lateinit var btnDecline: Button
    private lateinit var currentState: String
    private lateinit var useruid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_friend_real)
        useruid = intent.getStringExtra("uid").toString()
        userName = intent.getStringExtra("name").toString()

        Toast.makeText(this, useruid, Toast.LENGTH_SHORT).show()

        currentState = "not_friends"
        mUserRef = FirebaseDatabase.getInstance().reference.child("user")
        requestRef = FirebaseDatabase.getInstance().reference.child("requests")
        friendRef = FirebaseDatabase.getInstance().reference.child("friends")
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        profileImage = findViewById(R.id.profile_image_friend)
        name = findViewById(R.id.user_name)
        email = findViewById(R.id.user_email)
        btnDecline = findViewById(R.id.btn_request_dcl)
        btnPerform = findViewById(R.id.btn_request)
        btnDecline.visibility = View.GONE
        btnDecline.isEnabled = false



        loadUser()
        supportActionBar?.title = userName
        if(mUser.uid != useruid) {
            btnPerform.setOnClickListener {
                btnPerform.isEnabled = false
                if (currentState == "not_friends"){
                    sendFriendRequest()
                }
                if (currentState == "request_received"){
                    addFriend()
                }
            }
            btnDecline.setOnClickListener{
                if (currentState == "friends"){
                    unFriend()
                }
                if (currentState == "request_sent"){
                    cancelFriendRequest()
                }

            }
        }

    }

    private fun unFriend() {
        friendRef.child(mUser.uid).child(useruid).removeValue().addOnCompleteListener(){task->
            if (task.isSuccessful){
                friendRef.child(useruid).child(mUser.uid).removeValue().addOnCompleteListener(){task->
                    if (task.isSuccessful){
                        currentState="not_friends"
                        btnPerform.isEnabled=true
                        btnPerform.visibility= View.VISIBLE
                        btnPerform.text = "istek gönder"

                        btnDecline.isEnabled= false
                        btnDecline.visibility= View.GONE
                    }
                }
            }
        }
    }

    private fun addFriend() {
        val callForDate = Calendar.getInstance()
        val currentDate: String = "dd-mmmm-yyyy"
        val saveCurrentDate = currentDate.format(callForDate.time)

        friendRef.child(mUser.uid).child(useruid).setValue(Friends(userName,userEmail,useruid,profileImageUrl)).addOnCompleteListener() {task->
            if (task.isSuccessful){
                friendRef.child(useruid).child(mUser.uid).setValue(Friends(crUserName,crUserEmail,mUser.uid,crUserProfileImageUrl)).addOnCompleteListener(){task->
                    if (task.isSuccessful){
                        requestRef.child(mUser.uid).child(useruid).removeValue().addOnCompleteListener(){ task ->
                            if (task.isSuccessful){
                                requestRef.child(useruid).child(mUser.uid).removeValue().addOnCompleteListener(){ task->
                                    if (task.isSuccessful){
                                        btnPerform.isEnabled = false
                                        currentState = "friends"
                                        btnPerform.visibility = View.GONE

                                        btnDecline.visibility = View.VISIBLE
                                        btnDecline.isEnabled = true
                                        btnDecline.text = "arkadaşlıktan çıkar"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cancelFriendRequest() {
        requestRef.child(mUser.uid).child(useruid).removeValue().addOnCompleteListener(){ task ->
            if (task.isSuccessful){
                requestRef.child(useruid).child(mUser.uid).removeValue().addOnCompleteListener(){ task->
                    if (task.isSuccessful){
                        btnPerform.isEnabled = true
                        currentState = "not_friends"
                        btnPerform.text = "istek gönder"
                        btnPerform.visibility = View.VISIBLE

                        btnDecline.visibility = View.GONE
                        btnDecline.isEnabled = false
                    }
                }
            }
        }
    }

    private fun maintainceOfButtons() {
        requestRef.child(mUser.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(useruid)){
                    val request_type = snapshot.child(useruid).child("request_type").getValue().toString()
                    if (request_type == "sent"){
                        currentState = "request_sent"
                        btnPerform.visibility = View.GONE
                        btnPerform.isEnabled = false

                        btnDecline.text = "isteği iptal et"
                        btnDecline.visibility = View.VISIBLE
                        btnDecline.isEnabled = true

                    }
                    else if(request_type == "received"){
                        currentState = "request_received"
                        btnPerform.visibility = View.VISIBLE
                        btnPerform.isEnabled = true
                        btnPerform.text = "isteği kabul et"

                        btnDecline.visibility = View.VISIBLE
                        btnDecline.isEnabled = true
                        btnDecline.text = "isteği reddet"
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        friendRef.child(mUser.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(useruid)){
                    btnPerform.isEnabled = false
                    currentState = "friends"
                    btnPerform.visibility = View.GONE

                    btnDecline.visibility = View.VISIBLE
                    btnDecline.isEnabled = true
                    btnDecline.text = "arkadaşlıktan çıkar"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun sendFriendRequest() {
        requestRef.child(mUser.uid).child(useruid).child("request_type").setValue("sent").addOnCompleteListener(){ task ->
            if (task.isSuccessful){
                requestRef.child(useruid).child(mUser.uid).child("request_type").setValue("received").addOnCompleteListener(){ task->
                    if (task.isSuccessful){
                        currentState = "request_sent"
                        btnPerform.visibility = View.GONE
                        btnPerform.isEnabled = false

                        btnDecline.text = "isteği iptal et"
                        btnDecline.visibility = View.VISIBLE
                        btnDecline.isEnabled = true
                    }
                }
            }
        }
    }


    private fun loadUser() {
        mUserRef.child(useruid).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    profileImageUrl= snapshot.child("profileImageUrl").value.toString()
                    userName= snapshot.child("name").value.toString()
                    userEmail= snapshot.child("email").value.toString()

                    Picasso.get().load(profileImageUrl).into(profileImage)
                    name.text = userName
                    email.text = userEmail
                    maintainceOfButtons()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        mUserRef.child(mUser.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    crUserName = snapshot.child("name").value.toString()
                    crUserEmail = snapshot.child("email").value.toString()
                    crUserProfileImageUrl = snapshot.child("profileImageUrl").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}