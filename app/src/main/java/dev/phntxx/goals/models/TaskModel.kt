package dev.phntxx.goals.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import java.util.*


class TaskModel {

    var title: String? = null
    var description: String? = null

    var critical: Boolean? = null

    var location: String? = null
    var funds: DoubleArray? = null

    var uid: String? = null

    @ServerTimestamp
    var timestamp: Timestamp? = null

    constructor() {} // Needed for Firebase

    constructor(name: String?, description: String?, critical: Boolean?, location: String?, funds: DoubleArray?, uid: String?) {
        this.title = name
        this.description = description
        this.critical = critical
        this.location = location
        this.funds = funds
        this.uid = uid
    }

    constructor(name: String?, description: String?, critical: Boolean?, location: String?, funds: DoubleArray?, uid: String?, timestamp: Timestamp?) {
        this.title = name
        this.description = description
        this.critical = critical
        this.location = location
        this.funds = funds
        this.uid = uid
        this.timestamp = timestamp
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): TaskModel {
            val title = snapshot.get("title") as String?
            val description = snapshot.get("description") as String?
            val critical = snapshot.get("critical") as Boolean?
            val location = snapshot.get("location") as String?
            val funds = snapshot.get("funds") as DoubleArray?
            val uid = snapshot.get("uid") as String?
            val timestamp = snapshot.get("timestamp") as Timestamp?

            return TaskModel(title, description, critical, location, funds, uid, timestamp)
        }
    }
}