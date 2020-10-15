package com.example.jimpitan.viewmodel

import androidx.lifecycle.ViewModel
import com.example.jimpitan.Filters

class MainActivityViewModel : ViewModel() {


    var mIsSigningIn = false
    var mFilters: Filters

    var filters: Filters
        get() = mFilters
        set(mFilters) {
            this.mFilters = mFilters
        }

    init {
        mFilters = Filters.default
    }


}