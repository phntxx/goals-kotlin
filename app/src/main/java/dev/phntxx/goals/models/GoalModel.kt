package dev.phntxx.goals.models


import com.google.firebase.database.DataSnapshot



class GoalModel {
    var title: String? = null
    var imageUUID: String? = null
    var uid: String? = null
    var tasks: HashMap<String, *>? = null

    // Needed for Firebase
    constructor() {}

    constructor(name: String?, imageUUID: String?, uid: String?, tasks: HashMap<String, *>?) {
        this.title = name
        this.imageUUID = imageUUID
        this.uid = uid
        this.tasks = tasks
    }

    fun toMap(): Map<String, *> {
        return mapOf(
            "title" to this.title,
            "imageUUID" to this.imageUUID,
            "uid" to this.uid,
            "tasks" to this.tasks
        )
    }

    companion object {

        fun fromDataSnapshot(snapshot: DataSnapshot): GoalModel {
            val title = snapshot.child("title").value as String?
            val imageUUID = snapshot.child("imageUUID").value as String?
            val uid = snapshot.child("uid").value as String?
            val tasks = snapshot.child("tasks").value as HashMap<String, *>?

            return GoalModel(title, imageUUID, uid, tasks)
        }
    }
}