package com.tunahankalayci.garlicapp

import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class FriendList : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var profileImage: CircleImageView
    private lateinit var toolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        supportActionBar?.title = "Arkadaş Ekle"
        mDbRef = FirebaseDatabase.getInstance().getReference().child("user")
        mAuth = FirebaseAuth.getInstance()
        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        userRecyclerView = findViewById(R.id.user_recycler_view)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        mDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children) {

                    val currentUser = postSnapshot.getValue(Users::class.java)

                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}