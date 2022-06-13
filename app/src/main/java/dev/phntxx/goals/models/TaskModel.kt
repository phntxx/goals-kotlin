package dev.phntxx.goals.models

enum class Status(val value: Int) {
    IN_PROGRESS(0),
    COMPLETED(1),
    FAILED(2);

    companion object {
        fun fromValue(value: Int): Status {
            val mapping = values().associateBy(Status::value)
            return mapping[value]!!
        }
    }
}

class TaskModel {

    var title: String? = null
    var description: String? = null

    var critical: Boolean? = null

    var location: List<Double>? = null
    var funds: List<Double>? = null
    var status: Int = Status.IN_PROGRESS.value

    constructor() {} // Needed for Firebase

    constructor(name: String?, description: String?, critical: Boolean?, location: List<Double>?, funds: List<Double>?, status: Int?) {
        this.title = name
        this.description = description
        this.critical = critical
        this.location = location
        this.funds = funds
        this.status = status ?: Status.IN_PROGRESS.value
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