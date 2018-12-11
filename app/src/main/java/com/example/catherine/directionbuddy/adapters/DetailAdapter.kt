package com.example.catherine.directionbuddy.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.catherine.directionbuddy.DetailFragment
import com.example.catherine.directionbuddy.DirectionFragment
import com.example.catherine.directionbuddy.R
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import kotlinx.android.synthetic.main.card_layout.view.*


class DetailAdapter(private var fragment: DetailFragment, private var mData: List<Direction>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_DIRECTIONS = 1


    fun addAll(directions: List<Direction>) {
        mData = directions
        notifyDataSetChanged()
    }

    //****
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
            return VIEW_TYPE_DIRECTIONS
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        //***** Probably should show customer name someplace....
        val v: View
        val vh: RecyclerView.ViewHolder
        v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.fragment_detail, viewGroup, false)
            vh = ViewHolder(v)


        return vh
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        val viewType = getItemViewType(i)
        if (viewType == VIEW_TYPE_EMPTY) {
            //Don't need to do anything
        } else {
            val vh = viewHolder as DirectionAdapter.ViewHolder
            val direction = mData[i]
            vh.directionName.text = direction.name
//            vh.directionAddress.text = direction.address
//            vh.directionCity.text = direction.city
//            vh.directionState.text = direction.state
//            vh.directionZip.text = direction.zip

            vh.directionId = direction.id

        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var directionName: TextView
        var directionId: Int? = null
        init {
            directionName = itemView.directionName
        }
    }

    inner class ViewHolderEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)  {

    }

}