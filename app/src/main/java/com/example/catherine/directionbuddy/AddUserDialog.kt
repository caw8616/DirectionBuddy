package com.example.catherine.directionbuddy

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.catherine.directionbuddy.entities.User
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class AddUserDialog : DialogFragment() {

    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText


    public var listener: OnDialogFinishedListener? = null

    companion object {

        @JvmStatic
        fun newInstance() =
                AddUserDialog().apply {

                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater
        var view = inflater.inflate(R.layout.add_user, null)
        //need these here, can use kotlinx extentions in onCreateDialog
        usernameField = view.findViewById(R.id.usernameEditText)
        passwordField = view.findViewById(R.id.passwordEditText)


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                    // save the info
                    doAsync {
                        //should do validation here!!!!!!
                        val user = User(id = 1,
                                username = usernameField.text.toString(),
                                name = passwordField.text.toString())
                        uiThread {
                            listener?.onDialogFinished(user)
                        }
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                    this@AddUserDialog.getDialog().cancel()
                })
        return builder.create()

    }

    interface OnDialogFinishedListener {
        fun onDialogFinished(user: User)
    }
}