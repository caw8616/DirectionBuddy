package com.example.catherine.directionbuddy

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import com.example.catherine.directionbuddy.entities.Contact
import kotlinx.android.synthetic.main.add_direction.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread

class AddDirectionDialog : DialogFragment(),  AdapterView.OnItemSelectedListener {

    private lateinit var errorField : TextView
    private lateinit var nameField : EditText
    private lateinit var addressField : EditText
    private lateinit var cityField : EditText
    private lateinit var stateField : EditText
    private lateinit var zipField : EditText
    private lateinit var categoryField : EditText
    var contactNames = ArrayList<String>()
    var contacts = ArrayList<Contact>()
    var selectedContact:Contact? = null
    private lateinit var spinner: Spinner
    var listener: OnDialogFinishedListener? = null
    private lateinit var categorySpinner: Spinner
    val categories = arrayListOf<String>("Select a Category", "Family", "Friend", "Work", "School", "Doctor", "Store", "Other")
    var selectedCategory:String? = null

    companion object {
        @JvmStatic
        fun newInstance(_contacts: ArrayList<Contact>) =
                AddDirectionDialog().apply {
                    contacts = _contacts
                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        contactNames.add("Select a Contact")
        contacts.forEach {
            contactNames.add(it.name)
        }
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater
        var view = inflater.inflate(R.layout.add_direction, null)
        //need these here, can use kotlinx extentions in onCreateDialog
        errorField = view.findViewById(R.id.errorText)

        nameField = view.findViewById(R.id.nameEditText)
        addressField = view.findViewById(R.id.addressEditText)
        cityField = view.findViewById(R.id.cityEditText)
        stateField = view.findViewById(R.id.stateEditText)
        zipField = view.findViewById(R.id.zipEditText)

//        categoryField = view.findViewById(R.id.categoryEditText)

        categorySpinner = view.findViewById(R.id.categoryEditText) as Spinner
        val catAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = catAdapter
        categorySpinner.setSelection(0)

        categorySpinner.setOnItemSelectedListener(this)



//        contactField = view.findViewById(R.id.contactEditText)
        spinner = view.findViewById(R.id.contactSpinner) as Spinner
        val adapter = ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, contactNames)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.setOnItemSelectedListener(this)

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                    // save the info
                    doAsync {
                        //should do validation here!!!!!!
                       uiThread {
//                        var string = "THERE WAS AN ERROR WITH YOUR INPUT!!!!!!!!!!!"
//                        it.errorField.text = string
//                        Toast.makeText(context, string, Toast.LENGTH_LONG)
//                        Log.d("VALIDATE",it.dialog.)
                            listener?.onDialogFinished(nameField.text.toString(),
                                    addressField.text.toString(), cityField.text.toString(),
                                    stateField.text.toString(), zipField.text.toString(),
                                    selectedCategory, selectedContact)
                        }

                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                    this@AddDirectionDialog.dialog.cancel()
                })

        return builder.create()

    }
    override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
        when (v.id) {
            R.id.categoryEditText->{
                if (position == 0) {
                    selectedCategory = null
                } else {

                    selectedCategory = categories.get(position)
                }
            }
            R.id.contactSpinner->{
                if (position == 0) {
                    selectedContact = null
                } else {
                    Log.d("SPINNER", contacts[position - 1].toString())

                    selectedContact = contacts[position - 1]
                }
            }
        }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    interface OnDialogFinishedListener {
        fun onDialogFinished(name: String, address: String, city: String, state: String, zip: String, category: String?, contact: Contact?)
    }
}