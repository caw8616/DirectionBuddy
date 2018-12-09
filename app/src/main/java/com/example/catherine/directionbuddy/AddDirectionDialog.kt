package com.example.catherine.directionbuddy

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.catherine.directionbuddy.entities.Contact
import kotlinx.android.synthetic.main.add_direction.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class AddDirectionDialog : DialogFragment() {

    private lateinit var nameField : EditText
    private lateinit var addressField : EditText
    private lateinit var cityField : EditText
    private lateinit var stateField : EditText
    private lateinit var zipField : EditText
    private lateinit var categoryField : EditText
    private lateinit var contactField : EditText
    private var contacts = ArrayList<Contact>()



    public var listener: OnDialogFinishedListener? = null

    companion object {

        @JvmStatic
        fun newInstance(_contacts: ArrayList<Contact>) =
                AddDirectionDialog().apply {
                    contacts = _contacts
                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater
        var view = inflater.inflate(R.layout.add_direction, null)
        //need these here, can use kotlinx extentions in onCreateDialog
        nameField = view.findViewById(R.id.nameEditText)
        addressField = view.findViewById(R.id.addressEditText)
        cityField = view.findViewById(R.id.cityEditText)
        stateField = view.findViewById(R.id.stateEditText)
        zipField = view.findViewById(R.id.zipEditText)
        categoryField = view.findViewById(R.id.categoryEditText)
//        contactField = view.findViewById(R.id.contactEditText)
        Log.d("Dialog", contacts.toString())



        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                    // save the info
                    doAsync {
                        //should do validation here!!!!!!

                        uiThread {
                            listener?.onDialogFinished(nameField.text.toString(),
                                    addressField.text.toString(), cityField.text.toString(),
                                    stateField.text.toString(), zipField.text.toString(),
                                    categoryField.text.toString())
                        }
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                    this@AddDirectionDialog.getDialog().cancel()
                })

        return builder.create()

    }



    interface OnDialogFinishedListener {
        fun onDialogFinished(name: String, address: String, city: String, state: String, zip: String, category: String)
    }
}