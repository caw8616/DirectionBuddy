package com.example.catherine.directionbuddy


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.catherine.directionbuddy.entities.Contact
import com.example.catherine.directionbuddy.entities.Direction
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.Locale.filter


/**
 * A simple [Fragment] subclass.
 *
 */
class EditDirectionDialog : DialogFragment(), AdapterView.OnItemSelectedListener {


    private lateinit var nameField : EditText
    private lateinit var addressField : EditText
    private lateinit var cityField : EditText
    private lateinit var stateField : EditText
    private lateinit var zipField : EditText
    private lateinit var categoryField : EditText
    var contactNames = ArrayList<String>()
    var contacts = ArrayList<Contact>()
    var selectedContact: Contact? = null
    var direction: Direction? = null
    private lateinit var spinner: Spinner
    var listener: OnDialogFinishedListener? = null
    private lateinit var categorySpinner: Spinner

    val categories = arrayListOf<String>("Select a Category", "Family", "Friend", "Work", "School", "Doctor", "Store", "Other")
    var selectedCategory:String? = null

    companion object {

        @JvmStatic
        fun newInstance(_contacts: ArrayList<Contact>, _direction: Direction) =
                EditDirectionDialog().apply {
                    contacts = _contacts
                    direction = _direction
                }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.d("EDIT", direction.toString())
        contactNames.add("Select a Contact")
        contacts.forEach {
            contactNames.add(it.name)
        }
        val builder = AlertDialog.Builder(activity!!)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater
        var view = inflater.inflate(R.layout.fragment_edit_direction_dialog, null)
        //need these here, can use kotlinx extentions in onCreateDialog
        nameField = view.findViewById(R.id.nameEditText)
        addressField = view.findViewById(R.id.addressEditText)
        cityField = view.findViewById(R.id.cityEditText)
        stateField = view.findViewById(R.id.stateEditText)
        zipField = view.findViewById(R.id.zipEditText)
        categoryField = view.findViewById(R.id.categoryEditText)
//        contactField = view.findViewById(R.id.contactEditText)
        spinner = view.findViewById(R.id.contactSpinner) as Spinner
        val adapter = ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, contactNames)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        nameField.setText(direction!!.name, TextView.BufferType.EDITABLE);
        addressField.setText(direction!!.address, TextView.BufferType.EDITABLE);
        cityField.setText(direction!!.city, TextView.BufferType.EDITABLE);
        stateField.setText(direction!!.state, TextView.BufferType.EDITABLE);
        zipField.setText(direction!!.zip, TextView.BufferType.EDITABLE);
//        categoryField.setText(direction!!.category, TextView.BufferType.EDITABLE);


        categorySpinner = view.findViewById(R.id.categoryEditText) as Spinner
        val catAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, categories)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = catAdapter
        if (direction!!.category == null) {
            categorySpinner.setSelection(0)
        } else {
            categorySpinner.setSelection(categories.indexOf(direction!!.category))

        }

        categorySpinner.setOnItemSelectedListener(this)


        if(direction!!.contact == null) {
            spinner.setSelection(0)
        } else {
            contacts.forEach {
                if(it.id == direction!!.contact) {
                    spinner.setSelection(contacts.indexOf(it)+1)
                    selectedContact = it
                }
                contactNames.add(it.name)
            }
        }
        spinner.setOnItemSelectedListener(this)

        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Save", DialogInterface.OnClickListener { _, _ ->
                    // save the info
                    doAsync {
                        //should do validation here!!!!!!
                        uiThread {
                            listener?.onDialogFinished(direction!!.id!!, nameField.text.toString(),
                                    addressField.text.toString(), cityField.text.toString(),
                                    stateField.text.toString(), zipField.text.toString(),
                                    selectedCategory, selectedContact)
                        }
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
                    this@EditDirectionDialog.dialog.cancel()
                })

        return builder.create()

    }

    override fun onItemSelected(parent: AdapterView<*>, v: View, position: Int, id: Long) {
        Log.d("SPINNER",position.toString())
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
//        if(v.id == R.id.contactSpinner) {
//
//            if (position == 0) {
//                selectedContact = null
//            } else {
//                Log.d("SPINNER", contacts[position - 1].toString())
//
//                selectedContact = contacts[position - 1]
//            }
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    interface OnDialogFinishedListener {
        fun onDialogFinished(id: Int, name: String, address: String, city: String, state: String, zip: String, category: String?, contact: Contact?)
    }

}
