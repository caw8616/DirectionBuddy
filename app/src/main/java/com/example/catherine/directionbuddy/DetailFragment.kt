package com.example.catherine.directionbuddy

import android.Manifest
import android.Manifest.permission_group.CONTACTS
import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.catherine.directionbuddy.adapters.DetailAdapter
import com.example.catherine.directionbuddy.adapters.DirectionAdapter
import com.example.catherine.directionbuddy.entities.Contact
import com.example.catherine.directionbuddy.entities.Direction
import com.example.catherine.directionbuddy.viewmodels.AllDirectionsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.fragment_detail.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.support.v4.app.ActivityCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_ID = "userId"
private const val DIRECTION_ID = "directionId"
private const val DIRECTION_NAME = "directionName"


class DetailFragment : Fragment(),
        EditDirectionDialog.OnDialogFinishedListener ,
        DetailAdapter.ItemClickedListener ,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener{



    private var fab: FloatingActionButton? = null


        // TODO: Rename and change types of parameters
    private var userId: String? = null
    private var directionId: Int? = null
    private var directionName: String? = null
    private var myDirection: Direction? = null

    private lateinit var map: GoogleMap


    //    private var listener: OnFragmentInteractionListener? = null
    var contacts = ArrayList<Contact>()
    var directionsViewModel : AllDirectionsViewModel? =  null

    var name = ""

    companion object {
        val PERMISSIONS_REQUEST_READ_CONTACTS = 100

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userId: String, directionId: Int, directionName: String) =
                DetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(USER_ID, userId)
                        putInt(DIRECTION_ID, directionId)
                        putString(DIRECTION_NAME, directionName)
                    }
                }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            userId = it.getString(USER_ID)
            directionId = it.getInt(DIRECTION_ID)
            directionName = it.getString(DIRECTION_NAME)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "MyDirections"
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        loadContacts()
        directionsViewModel = AllDirectionsViewModel(activity?.application!!, userId!!)

        directionsViewModel!!.getDiectionById(directionId!!).observe(this, Observer {
            Log.d("DIRECTIONS", it.toString())
            var direction = it!![0]
            myDirection = direction

            nameField.text = direction.name
            addressField.text = direction.address
            cityField.text = direction.city
            stateField.text = direction.state
            zipField.text = direction.zip
            categoryField.text = direction.category
            if(direction.contact != null) {
                contacts.forEach{
                    if (it.id == direction.contact) {
                        contactField.text = it.name
                        if(it.picUri != null) {
                            imageView.setImageBitmap(it.picUri)
                        }
                    }
                }
            }
        })

        fab = view.findViewById<FloatingActionButton>(R.id.fabDetail)

        fab!!.setOnClickListener {
            val editDirectionDialog = EditDirectionDialog.newInstance(contacts, myDirection!!)
            editDirectionDialog.listener = this
            editDirectionDialog.show(fragmentManager!!, "editDirection")
        }
//*** swipe to delete
//        setRecyclerViewItemTouchListener()
//***
        return view
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_detail, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        directionsViewModel = AllDirectionsViewModel(activity?.application!!, userId!!)
        directionsViewModel!!.getDiectionById(directionId!!).observe(this, Observer {
            it!!.forEach {
                var address = getLatLong(it)
                if(address != null) {
                    val currentLatLng = LatLng(address.latitude, address.longitude)
                    placeDirectionMarkerOnMap(currentLatLng, it.name)
                }

            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener(this)
    }
    private fun placeDirectionMarkerOnMap(location: LatLng, title:String) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        markerOptions.title(title)

        map.addMarker(markerOptions)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12.0f))

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }
    private fun getLatLong(direction: Direction): Address? {
        // 1
        val geocoder = Geocoder(context)
        val addresses: List<Address>?
        var address: Address? = null

        try {
            // 2
            addresses = geocoder.getFromLocationName(direction.getAddressString(), 1)
//            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            Log.d("DIRECTIONS", addresses.toString())

            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
//                addressText+=address.getAddressLine(0)
////                for (i in 0 until address.maxAddressLineIndex) {
////                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
////                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return address
    }


    private fun loadContacts() {
        Log.d("Contacts", "Loading Contacts...")
        var builder = StringBuilder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity!!.checkSelfPermission(
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Contacts", "Permission Not Granted...")

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS)
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
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
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
                if (cursorAddress.count > 0) {
                    while (cursorAddress.moveToNext()) {
                        val address = cursorAddress.getString(cursorAddress.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
                        contact.address = address
                    }
                }
                cursorAddress.close()

                contact.picUri = getContactPhoto(id)


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
    fun getContactPhoto(contactId:String): Bitmap? {
        var photo: Bitmap? = null
        try {
            val inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context!!.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId.toLong()))

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream)
            }

            inputStream!!.close()

        }  catch (e: Exception) {
            Log.e("Error", e.toString())
        }
        return photo
    }
    override fun onDialogFinished(id: Int, name: String, address: String, city: String, state: String, zip: String, category: String, contact: Contact?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemClicked(directionId: Int, directionName: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
