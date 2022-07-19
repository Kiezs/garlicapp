package com.tunahankalayci.garlicapp

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var fab: FloatingActionButton
    private lateinit var mUser: FirebaseUser
    private lateinit var mUserRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mUserRef = FirebaseDatabase.getInstance().getReference()
        fab = findViewById(R.id.start_message)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, FriendsActivity::class.java)
            startActivity(intent)
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.log_out){
            mAuth.signOut()
            finish()
            return true
        }
        if (item.itemId == R.id.add_friends_menu){
            val intent = Intent(this@MainActivity, FriendList::class.java)
            startActivity(intent)
        }
        if (item.itemId == R.id.profile_menu){
            val intent = Intent(this@MainActivity, Profile::class.java)
            startActivity(intent)
        }
        if (item.itemId == R.id.friends_menu){
            val intent = Intent(this@MainActivity, FriendsActivity::class.java)
            startActivity(intent)
        }
        return true
    }



    /*override fun onStart() {
        super.onStart()
        if(mUser==null){
            intent = Intent(this@MainActivity, LogIn::class.java)
            startActivity(intent)
        }
        else {
            mUserRef.child("user").child(mUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            prflImg = snapshot.child("profıleImageUrl").getValue().toString()
                            userName = snapshot.child("name").getValue().toString()
                            email = snapshot.child("email").getValue().toString()
                            Picasso.get().load(prflImg).into(profileImg)
                            userNameHeader.text = userName
                            emailHeader.text = email

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })
        }
    }*/


}