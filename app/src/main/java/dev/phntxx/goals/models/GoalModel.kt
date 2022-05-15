package dev.phntxx.goals.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp


class GoalModel {
    var title: String? = null
    var imageUUID: String? = null
    var uid: String? = null
    var tasks: Array<TaskModel>? = null

    @ServerTimestamp
    var timestamp: Timestamp? = null

    // Needed for Firebase
    constructor() {}

    constructor(name: String?, uid: String?) {
        this.title = name
        this.uid = uid
    }

    constructor(name: String?, imageUUID: String?, uid: String?, timestamp: Timestamp?, tasks: Array<TaskModel>?) {
        this.title = name
        this.imageUUID = imageUUID
        this.uid = uid
        this.timestamp = timestamp
        this.tasks = tasks
    }

    fun toMap(): Map<String, *> {
        return mapOf(
            "title" to this.title,
            "imageUUID" to this.imageUUID,
            "uid" to this.uid,
            "timestamp" to this.timestamp,
            "tasks" to this.tasks
        )
    }

    companion object {
        fun fromDocumentSnapshot(snapshot: DocumentSnapshot): GoalModel {
            val title = snapshot.get("title") as String?
            val imageUUID = snapshot.get("imageUUID") as String?
            val uid = snapshot.get("uid") as String?
            val timestamp = snapshot.get("timestamp") as Timestamp?
            val tasks = snapshot.get("tasks") as Array<TaskModel>?

            return GoalModel(title, imageUUID, uid, timestamp, tasks)
        }
    }
}