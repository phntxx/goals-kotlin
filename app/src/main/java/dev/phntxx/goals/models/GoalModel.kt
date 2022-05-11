package dev.phntxx.goals.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date


class GoalModel {
    var title: String? = null
    var uid: String? = null
    private var mTimestamp: Date? = null

    constructor() {} // Needed for Firebase

    constructor(name: String?, uid: String?) {
        this.title = name
        this.uid = uid
    }

    @get:ServerTimestamp
    var timestamp: Date?
        get() = mTimestamp
        set(timestamp) {
            mTimestamp = timestamp
        }
}