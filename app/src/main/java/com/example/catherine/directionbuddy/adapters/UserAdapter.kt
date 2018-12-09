package com.example.catherine.directionbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.catherine.directionbuddy.R
import com.example.catherine.directionbuddy.entities.User
import kotlinx.android.synthetic.main.user_layout.view.*

//new *** added listener parameter to constructor
class UserAdapter(private var mData: List<User>, private var listener: ItemClickedListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_USER = 1

    fun addAll(users: List<User>) {
        mData = users
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if(mData.size == 0){
            return 1
        }else {
            return mData.size
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (mData.size == 0) {
            return VIEW_TYPE_EMPTY
        } else {
            return VIEW_TYPE_USER
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        val v: View
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_TYPE_USER) {
            v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.user_layout, viewGroup, false)
            vh = ViewHolder(v)
        } else {
            v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.empty_layout, viewGroup, false)
            vh = ViewHolderEmpty(v)
        }

        return vh
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val viewType = getItemViewType(i)
        if (viewType == VIEW_TYPE_EMPTY) {
            //Don't need to do anything
        } else {
            val vh = viewHolder as ViewHolder
            val user = mData[i]
            vh.username.text = user.username

            //new ***
//            vh.userId = user.id
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        var username: TextView

        //new ***
        var userId: Int? = null

        init {
            username = itemView.usernameField


            //new ***
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(customer: View?) {
                    listener.onItemClicked(userId!!,username.text.toString())
                }
            })
        }
    }

    inner class ViewHolderEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)  {

    }

    //new ***
    interface ItemClickedListener  {
        fun onItemClicked(userId: Int, username: String)
    }
}