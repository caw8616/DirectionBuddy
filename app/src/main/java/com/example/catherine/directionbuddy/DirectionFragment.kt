package com.example.catherine.directionbuddy

import android.arch.lifecycle.Observer
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.catherine.directionbuddy.adapters.DirectionAdapter
import com.example.catherine.directionbuddy.entities.Contact
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_ID = "user_id"
private const val USERNAME = "username"


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DirectionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DirectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class DirectionFragment : Fragment(),  AddDirectionDialog.OnDialogFinishedListener {

    var mAdapter: DirectionAdapter? = null
    private var recyclerView : RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var fab: FloatingActionButton? = null

    var directionsViewModel : AllDirectionsViewModel? =  null

    private var userId: String = ""
    private var username: String = ""
    private var contacts = ArrayList<Contact>()


    //swipe to delete
    private var icon: Drawable? = null
    private var background: ColorDrawable? = null
//***

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
            username = it.getString(USERNAME)
//            contacts = it.getParcelableArrayList<Contact>(CONTACTS)
        }
        directionsViewModel = AllDirectionsViewModel(activity?.application!!, userId!!)

        mAdapter = DirectionAdapter(this,
                mutableListOf<Direction>())


        directionsViewModel!!.getAllDirections().observe(this, Observer {
            Log.d("DIRECTIONS", it.toString())
            mAdapter!!.addAll(it ?: emptyList())
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Direction: "+ username
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_direction, container, false)
        recyclerView = view.findViewById(R.id.recycler_viewDirection) as RecyclerView
        layoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = mAdapter



        fab = view.findViewById<FloatingActionButton>(R.id.fabDirection)

        fab!!.setOnClickListener {
            val addDirectionDialog = AddDirectionDialog.newInstance(contacts)
            addDirectionDialog.listener = this
            addDirectionDialog.show(fragmentManager!!, "addDirection")
        }
//*** swipe to delete
        setRecyclerViewItemTouchListener()
//***
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(userId: String, username :String, _contacts: ArrayList<Contact> ) =
                DirectionFragment().apply {
                    arguments = Bundle().apply {
                        putString(USER_ID, userId)
                        putString(USERNAME, username)
                        contacts = _contacts
                    }
                }
    }

    override fun onDialogFinished(name: String, address: String, city: String, state: String, zip: String, category: String) {
        doAsync {

            val direction = Direction(
                    id = null,
                    name = name,
                    address = address,
                    city = city,
                    state = state,
                    zip = zip,
                    contact = null,
                    category = category,
                    user_id = userId!!)

            directionsViewModel!!.insertDirection(direction = direction)

            uiThread {
                Toast.makeText(context,"Direction Added", Toast.LENGTH_SHORT)
                        .show()

            }
        }
    }
    //swipe to delete code
    private fun setRecyclerViewItemTouchListener() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            init {
                icon = ContextCompat.getDrawable(activity!!,
                        R.drawable.ic_delete)
                background = ColorDrawable (Color.RED)
            }

            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                return false //don't want to do anything here

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                mAdapter!!.removeItem(position, directionsViewModel!!)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20
                val iconMargin = (itemView.height - icon!!.getIntrinsicHeight()) / 2
                val iconTop = itemView.top + (itemView.height - icon!!.getIntrinsicHeight()) / 2
                val iconBottom = iconTop + icon!!.getIntrinsicHeight()

                if (dX > 0) { // Swiping to the right
                    val iconLeft = itemView.left + iconMargin + icon!!.getIntrinsicWidth()
                    val iconRight = itemView.left + iconMargin
                    icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background!!.setBounds(itemView.left, itemView.top,
                            itemView.left + dX.toInt() + backgroundCornerOffset,
                            itemView.bottom)
                } else if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - icon!!.getIntrinsicWidth()
                    val iconRight = itemView.right - iconMargin
                    icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    background!!.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset,
                            itemView.top, itemView.right, itemView.bottom)
                } else { // view is unSwiped
                    background!!.setBounds(0, 0, 0, 0)
                }

                background!!.draw(c)
                icon!!.draw(c)

            }
        }

        //initialize and attach
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    } //setRecyclerViewItemTouchListener


// ****
}