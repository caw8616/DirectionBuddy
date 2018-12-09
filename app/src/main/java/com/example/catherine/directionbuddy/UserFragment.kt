package com.example.catherine.directionbuddy

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.catherine.directionbuddy.adapters.UserAdapter
import com.example.catherine.directionbuddy.entities.User
import com.example.catherine.directionbuddy.viewmodels.AllUsersViewModel
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread



/**
 * A simple [Fragment] subclass.
 *
 */
class UserFragment : Fragment(),
        AddUserDialog.OnDialogFinishedListener,
        UserAdapter.ItemClickedListener {
    override fun onItemClicked(userId: Int, username: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    var mAdapter: UserAdapter? = null
    private var recyclerView : RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    private var fab: FloatingActionButton? = null

    private var usersViewModel : AllUsersViewModel? =  null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usersViewModel = AllUsersViewModel(activity?.application!!)

        //new *** added listener parameter to constructor
        mAdapter = UserAdapter(
                mutableListOf<User>(), this)


        usersViewModel!!.getAllUsers().observe(this, Observer {
            mAdapter!!.addAll(it ?: emptyList())
        })
//        ViewModelProviders.of(this)
//                .get(AllCustomersViewModel::class.java)
//                .getAllCustomers()
//                .observe(this, Observer<List<Customer>>{ list ->
//                        // figure out whats been added/deleted/updated
//                        // given the new list and comparing against the
//                        // list kept by the adapter; notifying the adapter
//                        // of changes and/or changing the list kept by the adapter.
//                        //in our case, just going to set the list
//                    //there is a DiffUtil now that may help
//                        mAdapter!!.addAll(list!!.toList())
//                })

        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

        (activity as AppCompatActivity).supportActionBar?.title = "Users"

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
//        layoutManager = RecyclerView.(context)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = mAdapter



        fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab!!.setOnClickListener {
            val addUsersDialog = AddUserDialog.newInstance()
                addUsersDialog.listener = this
                addUsersDialog.show(fragmentManager!!,"addUser")

            }

//        doAsync {
//            //Execute all the long running tasks here
//            //val database = RoomExampleDatabase.getInstance(context = context!!)
//            val customers = customersViewModel!!.getAllCustomers()
//
//            uiThread {
//                //Update the UI thread here
//                System.out.println(customers.value)
//                mAdapter!!.addAll(customers.value!!)
//            }
//        }

        return view
    }

    override fun onDialogFinished(user: User) {
        doAsync {
            //val database = RoomExampleDatabase.getInstance(context = context!!)
            //database.customerDao().insert(customer)
            //val customers = database.customerDao().all //need to retrieve them all
            //so can update adapter, if db going to be
            //large might want to have another way of
            //doing this by if insert successful
            //just update the adapter list directly
            //and then call notifydatasetchanged.
            usersViewModel!!.insertUser(user)

                uiThread {
                    Toast.makeText(context,"User Added", Toast.LENGTH_SHORT)
                            .show()
                    //mAdapter!!.addAll(customers) //update the ui do we need with LiveData

                }
            }
        }


        companion object {
            @JvmStatic
            fun newInstance() =
                    UserFragment().apply {

                    }
        }

        //new *** added
        fun onItemClicked(userId: String, username: String) {
//            val fragment = DirectionFragment.newInstance(userId,username, )
//            activity!!.supportFragmentManager
//                    .beginTransaction()
//                    .setCustomAnimations(R.anim.design_bottom_sheet_slide_in, R.anim.design_bottom_sheet_slide_out)
//                    .add(R.id.content, fragment, fragment.javaClass.getSimpleName())
//                    .addToBackStack(fragment.javaClass.getSimpleName())
//                    .commit()

        }


    }
