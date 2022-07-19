package com.tunahankalayci.garlicapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendsAdapter(val context: Context, val friendList: ArrayList<Friends>):
    RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.friend_layout,parent,false)
        return FriendsViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val currentFriend = friendList[position]

        holder.userName.text = currentFriend.name
        holder.userEmail.text = currentFriend.email
        Picasso.get().load(currentFriend.profileImageUrl).into(holder.imageView)
        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatlogActivity::class.java)
            intent.putExtra("name",currentFriend.name)
            intent.putExtra("uid",currentFriend.uid)
            context.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }
    class FriendsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userName = itemView.findViewById<TextView>(R.id.friends_name)
        val userEmail = itemView.findViewById<TextView>(R.id.friends_email)
        val imageView = itemView.findViewById<CircleImageView>(R.id.friends_image)
    }
}