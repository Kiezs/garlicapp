package com.tunahankalayci.garlicapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatlogActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mMessageRef: DatabaseReference

    private var receiverRoom: String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        val name = intent.getStringExtra("name").toString()
        val receiverUid = intent.getStringExtra("uid").toString()
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mMessageRef = FirebaseDatabase.getInstance().getReference()

        supportActionBar?.title = name


        receiverRoom = senderUid + receiverUid
        senderRoom = receiverUid + senderUid

        chatRecyclerView = findViewById(R.id.recyclerview_chatlog)
        messageBox = findViewById(R.id.mesage_send)
        sendButton = findViewById(R.id.message_send_btn)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mMessageRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })





        sendButton.setOnClickListener {

            val message = messageBox.text.toString()
            val messageObject = Message(message,senderUid)
            mMessageRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject)
                    .addOnCompleteListener(){task->
                    if (task.isSuccessful){
                        mMessageRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                }
            messageBox.setText("")
        }


    }
}