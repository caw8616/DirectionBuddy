package com.example.catherine.directionbuddy

import android.Manifest
import android.content.ContentResolver
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.catherine.directionbuddy.entities.Contact
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_direction_buddy.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.jetbrains.anko.startActivityForResult


class DirectionBuddy : AppCompatActivity() {
    //718694433470-db2v2o8qtetn7lipk6lvlbnv010g8s21.apps.googleusercontent.com
    companion object {
        val RC_SIGN_IN = 1
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_CHECK_SETTINGS = 2
        const val PLACE_PICKER_REQUEST = 3
    }

    var mGoogleSignInClient: GoogleSignInClient? = null
    var signedInAccount: GoogleSignInAccount? = null
    var signedIn = false
    var userId = ""
    var username = ""
    var name = ""

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_list -> {
                addFragment(DirectionFragment.newInstance(userId, username))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                addFragment(MapFragment.newInstance(userId, username))
//                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }

    private fun addFragment(fragment: Fragment) {

        val currentFragment = supportFragmentManager
                .findFragmentByTag(fragment.javaClass.simpleName)
        if (currentFragment != null) {
            if (fragment.javaClass.simpleName != "DirectionFragment" ||
                    supportFragmentManager.findFragmentByTag("DetailFragment") == null) {
                supportFragmentManager
                        .popBackStackImmediate(fragment.javaClass.simpleName, 0) //pop the backstack to the fragment
            } else {
                supportFragmentManager
                        .popBackStackImmediate("DetailFragment", 0)
            }
        } else { //fragment doesn't already exist
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                    .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
                    .addToBackStack(fragment.javaClass.simpleName)
                    .commit()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction_buddy)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d("ACCOUNT", account.toString())

        if(account == null) {
            signedIn = false

        } else  {
            Log.d("ACCOUNT", account.id.toString())
            Log.d("ACCOUNT", account.email)

            signedInAccount = account
            signedIn = true
            userId =account.id!!.toString()
            username = account.email!!
            if(account.displayName != null) {
                name = account.displayName!!
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        if(signedIn === false) {
            displaySignInScreen()
        } else {
            val fragment = DirectionFragment.newInstance(userId, username)
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                    .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
                    .addToBackStack(fragment.javaClass.getSimpleName())
                    .commit()
        }
    }
    fun displaySignInScreen() {
        navigation.visibility = View.INVISIBLE
        sign_in_button.visibility = View.VISIBLE
        sign_in_button.setOnClickListener{
            signIn()
        }

    }
    fun removeSignInScreen() {
        navigation.visibility = View.VISIBLE
        sign_in_button.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.menu_logout-> if(signedIn === true)signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        mGoogleSignInClient!!.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void>() {
                if (it.isSuccessful) {
                    displaySignInScreen()
                    signedIn = false
                    signedInAccount = null
                    clearBackStack()
                }
            })
    }

    fun clearBackStack() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    override fun onBackPressed() {
        //prevent the app from exiting on back button and/or going to a blank screen
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN){
            var task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d("ACCOUNT",account.toString())
            // Signed in successfully, show authenticated UI.
            updateSignInStatus(account)
        } catch (e: ApiException) {
            updateSignInStatus(null)
        }

    }
    fun updateSignInStatus(account: GoogleSignInAccount?) {
        if(account == null) {
            signedIn = false
            signedInAccount = null
        } else {
            signedIn = true
            signedInAccount = account

            removeSignInScreen()
            userId = account.id!!.toString()
            username = account.email!!

            if (account.displayName != null){
                name = account.displayName!!
            }
            val fragment = DirectionFragment.newInstance(userId!!.toString(), username)
            supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
                    .replace(R.id.content, fragment, fragment.javaClass.getSimpleName())
                    .addToBackStack(fragment.javaClass.getSimpleName())
                    .commit()

        }
    }

    fun loadContacts() {
        Log.d("Contacts", "Loading Contacts...")
        var builder = ArrayList<Contact>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Contacts", "Permission Not Granted...")

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),
                    DirectionBuddy.PERMISSIONS_REQUEST_READ_CONTACTS)
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
        if (requestCode == DirectionBuddy.PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Contacts", "After Request permission granted...")
                loadContacts()
            } else {
                Log.d("Contacts", "After Request permission not granted...")
                //  toast("Permission must be granted in order to display contacts information")
            }
        }
    }
    private fun getContacts(): ArrayList<Contact> {
        Log.d("Contacts", "Getting Contacts...")

//        val builder = ArrayList<Contact>()
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null)
//        Log.d("Contacts", "Curser count..."+cursor.count)
        val contacts = ArrayList<Contact>()

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
//                Log.d("Contacts", "Contact Display Name: "+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts)))
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
                    val cursorPhone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                    if(cursorPhone.count > 0) {
                        while (cursorPhone.moveToNext()) {
                            val phoneNumValue = cursorPhone.getString(
                                    cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            phoneNums.add(phoneNumValue)
                        }
                    }
                    contact.phoneNumber = phoneNums
                    cursorPhone.close()
                }
                val cursorAddress = contentResolver.query(
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

                contacts.add(contact)
            }
        } else {
//               toast("No contacts available!")
        }
        cursor.close()
        Log.d("Contacts", contacts.toString())
        return contacts
    }
}




