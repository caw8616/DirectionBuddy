package com.example.catherine.directionbuddy

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
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
class DirectionFragment : Fragment(),
        AddDirectionDialog.OnDialogFinishedListener,
        DirectionAdapter.ItemClickedListener {

    var mAdapter: DirectionAdapter? = null
    private var recyclerView : RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var fab: FloatingActionButton? = null

    var directionsViewModel : AllDirectionsViewModel? =  null

    private var userId: String = ""
    private var username: String = ""
    var contacts = ArrayList<Contact>()


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
        loadContacts()
        directionsViewModel = AllDirectionsViewModel(activity?.application!!, userId!!)

        mAdapter = DirectionAdapter(this,
                mutableListOf<Direction>(), this)


        directionsViewModel!!.getAllDirections().observe(this, Observer {
            Log.d("DIRECTIONS", it.toString())
            mAdapter!!.addAll(it ?: emptyList())
        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "MyDirections"
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
        fun newInstance(userId: String, username :String ) =
                DirectionFragment().apply {
                    arguments = Bundle().apply {
                        putString(USER_ID, userId)
                        putString(USERNAME, username)
                    }
                }
    }

    override fun onDialogFinished(name: String, address: String, city: String, state: String, zip: String, category: String, contact: Contact?) {
        doAsync {

            val direction = Direction(
                    id = null,
                    name = name,
                    address = address,
                    city = city,
                    state = state,
                    zip = zip,
                    contact = if(contact != null)contact.id else null,
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
    //new *** added
    override fun onItemClicked(directionId: Int, directionName: String) {
        val fragment = DetailFragment.newInstance(userId,directionId, directionName)
        activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                .add(R.id.content, fragment, fragment.javaClass.getSimpleName())
                .addToBackStack(fragment.javaClass.getSimpleName())
                .commit()

    }
    private fun loadContacts() {
        Log.d("Contacts", "Loading Contacts...")
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity!!.checkSelfPermission(
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Contacts", "Permission Not Granted...")

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    DetailFragment.PERMISSIONS_REQUEST_READ_CONTACTS)
            //callback onRequestPermissionsResult
        } else {
            Log.d("Contacts", "Permission Granted...")

            builder = getContacts()
//            listContacts.text = builder.toString()
//            Log.d("Contacts", builder.toString())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == DetailFragment.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Contacts", "After Request permission granted...")

                loadContacts()
            } else {
                Log.d("Contacts", "After Request permission not granted...")

                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }

    private fun getContacts(): StringBuilder {
        Log.d("Contacts", "Getting Contacts...")

        val builder = StringBuilder()
        val resolver: ContentResolver = activity!!.contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null)
//        Log.d("Contacts", "Curser count..."+cursor.count)
        val cont = ArrayList<Contact>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
//                Log.d("Contacts", "Contact Display Name: "+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts)))
//                ContactsContract.Contacts.
                val contact = Contact()

                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                contact.id = id
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                contact.name = name;
                val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()
//                Log.d("Contacts", "Contact: "+id+" "+name+" "+phoneNumber)

                if (phoneNumber > 0) {
                    val phoneNums = ArrayList<String>()
                    val cursorPhone = activity!!.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if(cursorPhone.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                    cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                                    phoneNumValue).append("\n\n")
//                            Log.e("Name ===>",phoneNumValue);
                            phoneNums.add(phoneNumValue)
                        }
                    }
                    contact.phoneNumber = phoneNums
                    cursorPhone.close()
                }


                val cursorAddress = activity!!.contentResolver.query(
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,
                        ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID+ "=?",  arrayOf(id), null)
//                Log.d("Contacts", "Curser Address count..."+cursorAddress.count)

                if (cursorAddress.count > 0) {
                    while (cursorAddress.moveToNext()) {
                        val address = cursorAddress.getString(cursorAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
                        contact.address = address
                    }
                }
                cursorAddress.close()

                cont.add(contact)
            }
        } else {
//               toast("No contacts available!")
        }
        cursor.close()
        Log.d("Contacts", contacts.toString())
        contacts = cont
        return builder
    }

// ****
}