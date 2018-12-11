package com.example.catherine.directionbuddy

import android.Manifest
import android.Manifest.permission_group.CONTACTS
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
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
import android.support.annotation.NonNull
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import com.example.catherine.directionbuddy.DetailFragment.Companion.PERMISSIONS_REQUEST_READ_CONTACTS
import com.example.catherine.directionbuddy.R.id.menu_logout
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.jetbrains.anko.startActivityForResult


class DirectionBuddy : AppCompatActivity() {

    //718694433470-db2v2o8qtetn7lipk6lvlbnv010g8s21.apps.googleusercontent.com
    companion object {
        val RC_SIGN_IN = 1
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

}




