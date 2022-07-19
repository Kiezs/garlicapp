package com.tunahankalayci.garlicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mUserRef: DatabaseReference
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFriendRef: DatabaseReference
    private lateinit var adapter: FriendsAdapter
    private lateinit var friendList: ArrayList<Friends>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        friendList = ArrayList()
        mFriendRef = FirebaseDatabase.getInstance().getReference().child("friends")
        mUserRef = FirebaseDatabase.getInstance().getReference().child("user")
        adapter = FriendsAdapter(this,friendList)
        recyclerView = findViewById(R.id.friends_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        mFriendRef.child(mUser.uid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                friendList.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(Friends::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid){
                        friendList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })




        /*mFriendRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children){

                    val UserUid = postSnapshot.getValue().toString()
                    mUserRef.child(UserUid).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            friendList.clear()
                            for (postSnapshot in snapshot.children){

                                val currentUser = postSnapshot.getValue(Friends::class.java)

                                    friendList.add(currentUser!!)

                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })*/







    }


}