package dev.phntxx.goals.models

enum class Status(val value: Int?) {
    IN_PROGRESS(null),
    COMPLETED(2),
    FAILED(3)
}

class TaskModel {

    var title: String? = null
    var description: String? = null

    var critical: Boolean? = null

    var location: String? = null
    var funds: List<Double>? = null
    var key: String? = null
    var status: Int? = Status.IN_PROGRESS.value

    constructor() {} // Needed for Firebase

    constructor(name: String?, description: String?, critical: Boolean?, location: String?, funds: List<Double>?, status: Int?) {
        this.title = name
        this.description = description
        this.critical = critical
        this.location = location
        this.funds = funds
        this.status = status
    }

    fun toMap(): Map<String, *> {
        return mapOf(
            "title" to this.title,
            "description" to this.description,
            "critical" to this.critical,
            "location" to this.location,
            "funds" to this.funds,
            "status" to this.status
        )
    }
}