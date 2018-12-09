package com.example.catherine.directionbuddy.adapters

import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.catherine.directionbuddy.DirectionFragment
import com.example.catherine.directionbuddy.R
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import kotlinx.android.synthetic.main.card_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class DirectionAdapter(private var fragment: DirectionFragment, private var mData: List<Direction>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_EMPTY = 0
    private val VIEW_TYPE_INVOICE = 1

    private var mRecentlyDeletedDirection : Direction? = null
    private var mRecentlyDeletedPosition : Int? = null

    fun addAll(directions: List<Direction>) {
        mData = directions
        notifyDataSetChanged()
    }
    //swipe to remove
    fun removeItem(item: Int, directionsViewModel: AllDirectionsViewModel) {
        doAsync {

            mRecentlyDeletedPosition = item
            mRecentlyDeletedDirection = mData[item]

            directionsViewModel!!.deleteDirection(mData[item])

            uiThread {
                //notifyItemRemoved(item) //don't need because of postValue
                showUndoSnackbar()
            }
        }
    }

    private fun showUndoSnackbar() {
        val view = fragment.activity!!.findViewById<CoordinatorLayout>(R.id.invoiceCoordinatorLayout)
        //should be a string resource
        val snackbar = Snackbar.make(view, "Do you want to undo the delete?",
                Snackbar.LENGTH_LONG)
        //should be a string resource
        snackbar.setAction("Undo") {
            undoDelete()
        }
        snackbar.show()
    }

    private fun undoDelete() {
        doAsync {
            fragment.directionsViewModel!!.insertDirection(mRecentlyDeletedDirection!!)

        }
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
            return VIEW_TYPE_INVOICE
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        //***** Probably should show customer name someplace....
        val v: View
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_TYPE_INVOICE) {
            v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.card_layout, viewGroup, false)
            vh = ViewHolder(v)
        } else {
            v = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.empty_direction_layout, viewGroup, false)
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
            val direction = mData[i]
            vh.directionName.text = direction.name
            vh.directionAddress.text = direction.address
            vh.directionCity.text = direction.city
            vh.directionState.text = direction.state
            vh.directionZip.text = direction.zip

        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        var directionName: TextView
        var directionAddress: TextView
        var directionCity: TextView
        var directionState: TextView
        var directionZip: TextView

        init {
            directionName = itemView.directionName
            directionAddress = itemView.directionAddress
            directionCity = itemView.directionCity
            directionState = itemView.directionState
            directionZip = itemView.directionZip
        }
    }

    inner class ViewHolderEmpty(itemView: View) : RecyclerView.ViewHolder(itemView)  {

    }

}
