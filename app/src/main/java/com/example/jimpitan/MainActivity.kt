package com.example.jimpitan

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jimpitan.adapter.RestaurantAdapter
import com.example.jimpitan.model.Restaurant
import com.example.jimpitan.util.RestaurantUtil
import com.example.jimpitan.viewmodel.MainActivityViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(),View.OnClickListener, FilterDialogFragment.FilterListener,
    RestaurantAdapter.OnRestaurantSelectedListener {

    lateinit var providers : List<AuthUI.IdpConfig>
    val TAG = "MainActivity"
    val RC_SIGN_IN = 9001
    val LIMIT : Long = 50

    lateinit var mAdapter: RestaurantAdapter

    lateinit var mFirestore: FirebaseFirestore
    lateinit var mQuery: Query
    lateinit var mFilterDialog: FilterDialogFragment

    private lateinit var mViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inisialisasi AuthUI
        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        //set support toolbar
        setSupportActionBar(toolbar)

        // View model
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        // Enable Firestore logging
//        FirebaseFirestore.setLoggingEnabled(true)

        // Initialize Firestore and the main RecyclerView
        initFirestore()
        initRecyclerView()

        // Filter Dialog
        mFilterDialog = FilterDialogFragment()

//        showSignInOptions()

    }

    //menampilkan menu option di toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (FirebaseAuth.getInstance().currentUser == null) {
            showSignInOptions()
            return
        }

        // Apply filters
        onFilter(mViewModel.mFilters)

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter.stopListening()
        }
    }

//    private fun startSignIn() {
//        startActivityForResult(
//            AuthUI.getInstance().createSignInIntentBuilder()
//                .setAvailableProviders(providers)
//                .build(), RC_SIGN_IN
//        )
//
//        mViewModel.mIsSigningIn = true
//    }

    private fun initFirestore() {
        mFirestore = FirebaseFirestore.getInstance()

        // Dapatkan 50 restoran dengan nilai tertinggi
        mQuery = mFirestore.collection("restaurants")
            .orderBy("avgRating", Query.Direction.DESCENDING)
            .limit(LIMIT)
    }

    private fun initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView")
        }

        mAdapter = object : RestaurantAdapter(mQuery, this) {
            override fun onDataChanged() {
                // Show/hide content if the query returns empty.
                if (itemCount === 0) {
                    recycler_restaurants.visibility = View.GONE
                    view_empty.visibility = View.VISIBLE
                } else {
                    recycler_restaurants.visibility = View.VISIBLE
                    view_empty.visibility = View.GONE
                }
            }

            override fun onError(e: FirebaseFirestoreException?) {
                // Show a snackbar on errors
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Error: check logs for info.", Snackbar.LENGTH_LONG
                ).show()
            }
        }

        recycler_restaurants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                AuthUI.getInstance().signOut(this)
                showSignInOptions()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAddItemsClicked() {
        // Dapatkan referensi tentang koleksi restoran


        // Dapatkan referensi tentang koleksi restoran
        val restaurants = mFirestore.collection("restaurants")

        for (i in 0..9) {
            //Dapatkan POJO Restoran secara acak
            val restaurant: Restaurant? = RestaurantUtil().getRandom(this)
            // Tambahkan dokumen baru ke koleksi restoran
            if (restaurant != null) {
                restaurants.add(restaurant)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(
                    this,
                    "phone: " + user!!.phoneNumber + " /email: " + user.email,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

//    fun shouldStartSignIn(): Boolean {
//        return !mViewModel.mIsSigningIn && FirebaseAuth.getInstance().currentUser == null
//    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), RC_SIGN_IN
        )
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    override fun onFilter(filters: Filters?) {
        // Buat kueri dasar kueri
        var query: Query = mFirestore.collection("restaurants")

        // Category (equality filter)
        if (filters!!.hasCategory()) {
            query = query.whereEqualTo("category", filters.category)
        }

        // City (equality filter)
        if (filters!!.hasCity()) {
            query = query.whereEqualTo("city", filters.city)
        }

        // Price (equality filter)
        if (filters!!.hasPrice()) {
            query = query.whereEqualTo("price", filters.price)
        }

        // Sort by (orderBy with direction)
        if (filters!!.hasSortBy()) {
            query = query.orderBy(filters.sortBy!!, filters.sortDirection!!)
        }

        // Limit items
        query = query.limit(LIMIT.toLong())
        // Update the query
        mQuery = query
        mAdapter.setQuery(query)

        // Set header
        text_current_search.text = Html.fromHtml(filters!!.getSearchDescription(this))
        text_current_sort_by.text = filters!!.getOrderDescription(this)

        // Save filters
        mViewModel.mFilters = filters
    }

    override fun onRestaurantSelected(restaurant: DocumentSnapshot?) {
    }
}