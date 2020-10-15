package com.example.jimpitan.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Rating {
    var userId: String? = null
    var userName: String? = null
    var rating = 0.0
    var text: String? = null

    @ServerTimestamp
    var timestamp: Date? = null

    constructor() {}
    constructor(user: FirebaseUser, rating: Double, text: String?) {
        userId = user.uid
        userName = user.displayName
        if (TextUtils.isEmpty(userName)) {
            userName = user.email
        }
        this.rating = rating
        this.text = text
    }
}