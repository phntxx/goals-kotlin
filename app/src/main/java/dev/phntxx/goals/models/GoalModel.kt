package dev.phntxx.goals.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date


class GoalModel {
    var title: String? = null
    var uid: String? = null

    @ServerTimestamp
    var timestamp: Timestamp? = null

    // Needed for Firebase
    constructor() {}

    constructor(name: String?, uid: String?) {
        this.title = name
        this.uid = uid
    }

    constructor(name: String?, uid: String?, timestamp: Timestamp?) {
        this.title = name
        this.uid = uid
        this.timestamp = timestamp
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): GoalModel {
            val title = snapshot.get("title") as String?
            val uid = snapshot.get("uid") as String?
            val timestamp = snapshot.get("timestamp") as Timestamp?

            return GoalModel(title, uid, timestamp)
        }
    }
}