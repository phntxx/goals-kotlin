package dev.phntxx.goals.models

class GoalModel {
    var title: String? = null
    var imageUUID: String? = null
    var uid: String? = null
    var tasks: HashMap<String, *>? = null

    // Needed for Firebase
    constructor() {}

    constructor(name: String?, imageUUID: String?, uid: String?) {
        this.title = name
        this.imageUUID = imageUUID
        this.uid = uid
    }

    fun toMap(): Map<String, *> {
        return mapOf(
            "title" to this.title,
            "imageUUID" to this.imageUUID,
            "uid" to this.uid,
            "tasks" to this.tasks
        )
    }
}