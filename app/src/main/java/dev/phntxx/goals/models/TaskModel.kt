package dev.phntxx.goals.models

class TaskModel {

    var title: String? = null
    var description: String? = null

    var critical: Boolean? = null

    var location: String? = null
    var funds: DoubleArray? = null
    var key: String? = null

    constructor() {} // Needed for Firebase

    constructor(name: String?, description: String?, critical: Boolean?, location: String?, funds: DoubleArray?) {
        this.title = name
        this.description = description
        this.critical = critical
        this.location = location
        this.funds = funds
    }

    fun toMap(): Map<String, *> {
        return mapOf(
            "title" to this.title,
            "description" to this.description,
            "critical" to this.critical,
            "location" to this.location,
            "funds" to this.funds
        )
    }
}