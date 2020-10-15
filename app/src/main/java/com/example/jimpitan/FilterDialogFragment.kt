package com.example.jimpitan

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.jimpitan.model.Restaurant
import com.google.firebase.firestore.Query

class FilterDialogFragment : DialogFragment(), View.OnClickListener {

    interface FilterListener {
        fun onFilter(filters: Filters?)
    }

    lateinit var mRootView: View
    lateinit var mCategorySpinner: Spinner
    lateinit var mCitySpinner: Spinner
    lateinit var mSortSpinner: Spinner
    lateinit var mPriceSpinner: Spinner
    lateinit var mFilterListener: FilterListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false)
        mCategorySpinner = mRootView.findViewById(R.id.spinner_category)
        mCitySpinner = mRootView.findViewById(R.id.spinner_city)
        mSortSpinner = mRootView.findViewById(R.id.spinner_sort)
        mPriceSpinner = mRootView.findViewById(R.id.spinner_price)
        mRootView.findViewById<View>(R.id.button_search).setOnClickListener(this)
        mRootView.findViewById<View>(R.id.button_cancel).setOnClickListener(this)
        return mRootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FilterListener) {
            mFilterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_search -> onSearchClicked()
            R.id.button_cancel -> onCancelClicked()
        }
    }

    fun onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener!!.onFilter(filters)
        }
        dismiss()
    }

    fun onCancelClicked() {
        dismiss()
    }

    private val selectedCategory: String?
        private get() {
            val selected = mCategorySpinner!!.selectedItem as String
            return if (getString(R.string.value_any_category) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedCity: String?
        private get() {
            val selected = mCitySpinner!!.selectedItem as String
            return if (getString(R.string.value_any_city) == selected) {
                null
            } else {
                selected
            }
        }
    private val selectedPrice: Int
        private get() {
            val selected = mPriceSpinner!!.selectedItem as String
            return if (selected == getString(R.string.price_1)) {
                1
            } else if (selected == getString(R.string.price_2)) {
                2
            } else if (selected == getString(R.string.price_3)) {
                3
            } else {
                -1
            }
        }
    private val selectedSortBy: String?
        private get() {
            val selected = mSortSpinner!!.selectedItem as String
            if (getString(R.string.sort_by_rating) == selected) {
                return Restaurant.FIELD_AVG_RATING
            }
            if (getString(R.string.sort_by_price) == selected) {
                return Restaurant.FIELD_PRICE
            }
            return if (getString(R.string.sort_by_popularity) == selected) {
                Restaurant.FIELD_POPULARITY
            } else null
        }
    private val sortDirection: Query.Direction?
        private get() {
            val selected = mSortSpinner!!.selectedItem as String
            if (getString(R.string.sort_by_rating) == selected) {
                return Query.Direction.DESCENDING
            }
            if (getString(R.string.sort_by_price) == selected) {
                return Query.Direction.ASCENDING
            }
            return if (getString(R.string.sort_by_popularity) == selected) {
                Query.Direction.DESCENDING
            } else null
        }

    fun resetFilters() {
        if (mRootView != null) {
            mCategorySpinner!!.setSelection(0)
            mCitySpinner!!.setSelection(0)
            mPriceSpinner!!.setSelection(0)
            mSortSpinner!!.setSelection(0)
        }
    }

    val filters: Filters
        get() {
            val filters = Filters()
            if (mRootView != null) {
                filters.run {
                    selectedCategory
                    selectedCity
                    selectedPrice
                    selectedSortBy
                    sortDirection
                }
            }
            return filters
        }

    companion object {
        const val TAG = "FilterDialog"
    }
}