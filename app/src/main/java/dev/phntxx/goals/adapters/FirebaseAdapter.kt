package dev.phntxx.goals.adapters

import android.net.Uri
import android.util.Log
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dev.phntxx.goals.models.GoalModel
import dev.phntxx.goals.models.Status
import dev.phntxx.goals.models.TaskModel
import java.util.*

class FirebaseAdapter {

    private var auth = Firebase.auth
    private var database = Firebase.database
    private var ref = database.getReference("goals")
    private var storage = FirebaseStorage.getInstance()

    var user = auth.currentUser
    var userId = user?.uid

    fun getGoal(goalId: String, onSuccessListener: OnSuccessListener<GoalModel>, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .get()
            .addOnSuccessListener {
                val goal: GoalModel = it.getValue(GoalModel::class.java)!!
                onSuccessListener.onSuccess(goal)
            }
            .addOnFailureListener(onFailureListener)
    }

    fun getTask(goalId: String, taskId: String, onSuccessListener: OnSuccessListener<TaskModel>, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .child("tasks")
            .child(taskId)
            .get()
            .addOnSuccessListener {
                val task: TaskModel = it.getValue(TaskModel::class.java)!!
                onSuccessListener.onSuccess(task)
            }
            .addOnFailureListener(onFailureListener)
    }

    private fun increment(map: MutableMap<String, Int>, field: String) {
        map[field] = map.getValue(field) + 1
    }

    fun getTaskStatistics(goalId: String, onSuccessListener: OnSuccessListener<Map<String, Int>>, onFailureListener: OnFailureListener = defaultFailureListener) {

        ref
            .child(goalId)
            .child("tasks")
            .get()
            .addOnSuccessListener {

                val stats = mutableMapOf(
                    "total" to it.childrenCount.toInt(),
                    "total_noncritical" to 0,
                    "total_critical" to 0,
                    "in_progress" to 0,
                    "in_progress_critical" to 0,
                    "completed" to 0,
                    "completed_critical" to 0,
                    "failed" to 0,
                    "failed_critical" to 0
                )

                it.children.forEach { child ->
                    val task = child.getValue(TaskModel::class.java)!!
                    val status = Status.fromValue(task.status)

                    if (task.critical == true) {
                        increment(stats, "total_critical")

                        if (status == Status.IN_PROGRESS) increment(stats, "in_progress_critical")
                        if (status == Status.COMPLETED) increment(stats, "completed_critical")
                        if (status == Status.FAILED) increment(stats, "failed_critical")
                    } else {
                        increment(stats, "total_noncritical")

                        if (status == Status.IN_PROGRESS) increment(stats, "in_progress")
                        if (status == Status.COMPLETED) increment(stats, "completed")
                        if (status == Status.FAILED) increment(stats, "failed")
                    }
                }

                onSuccessListener.onSuccess(stats.toMap())
            }
            .addOnFailureListener(onFailureListener)
    }

    fun createGoal(goal: GoalModel, onSuccessListener: OnSuccessListener<String>, onFailureListener: OnFailureListener = defaultFailureListener) {
        val pushKey = ref.push().key ?: ""

        ref
            .child(pushKey)
            .setValue(goal)
            .addOnSuccessListener {
                onSuccessListener.onSuccess(pushKey)
            }
            .addOnFailureListener(onFailureListener)
    }

    fun createTask(goalId: String, task: TaskModel, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        val pushKey = ref.push().key ?: ""

        ref
            .child(goalId)
            .child("tasks")
            .child(pushKey)
            .setValue(task)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun updateGoal(goalId: String, goal: GoalModel, onSuccessListener: OnSuccessListener<Void> = emptyOnSuccessListener, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .updateChildren(goal.toMap())
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun updateTaskStatus(goalId: String, taskId: String, status: Int, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .child("tasks")
            .child(taskId)
            .child("status")
            .setValue(status)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteGoal(goalId: String, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        getGoal(goalId, {
            if (it.imageUUID != null) deleteGoalImage(it.imageUUID!!)
        })

        ref
            .child(goalId)
            .removeValue()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteTask(goalId: String, taskId: String, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .child("tasks")
            .child(taskId)
            .removeValue()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun uploadGoalImage(uri: Uri, onSuccessListener: OnSuccessListener<String>, onProgressListener: OnProgressListener<UploadTask.TaskSnapshot>, onFailureListener: OnFailureListener = defaultFailureListener) {
        val uuid = UUID.randomUUID().toString()
        val ref = getStorageRef(uuid)

        ref
            .putFile(uri)
            .addOnSuccessListener {
                onSuccessListener.onSuccess(uuid)
            }
            .addOnProgressListener(onProgressListener)
            .addOnFailureListener(onFailureListener)
    }

    fun updateGoalImage(uuid: String, uri: Uri, onSuccessListener: OnSuccessListener<String>, onProgressListener: OnProgressListener<UploadTask.TaskSnapshot>, onFailureListener: OnFailureListener = defaultFailureListener) {
        val ref = getStorageRef(uuid)

        ref
            .putFile(uri)
            .addOnSuccessListener {
                onSuccessListener.onSuccess(uuid)
            }
            .addOnProgressListener(onProgressListener)
            .addOnFailureListener(onFailureListener)
    }

    fun deleteGoalImage(uuid: String, onSuccessListener: OnSuccessListener<Void> = emptyOnSuccessListener, onFailureListener: OnFailureListener = defaultFailureListener) {
        val ref = getStorageRef(uuid)

        ref
            .delete()
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun buildGoalAdapter(): GoalAdapter {
        val query = ref
            .orderByChild("uid")
            .equalTo(userId)

        val options = FirebaseRecyclerOptions.Builder<GoalModel>()
            .setQuery(query, GoalModel::class.java)
            .build()

        return GoalAdapter(options)
    }

    fun buildTaskAdapter(goalId: String): TaskAdapter {
        val query = ref
            .child(goalId)
            .child("tasks")

        val options = FirebaseRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java)
            .build()

        return TaskAdapter(options, goalId)
    }

    fun getStorageRef(uuid: String): StorageReference {
        return storage.getReference("images").child(uuid)
    }

    fun signOut() = auth.signOut()

    private val emptyOnSuccessListener = OnSuccessListener<Void> {
        Log.d(TAG, "Success!")
    }

    private val defaultFailureListener = OnFailureListener {
        Log.e(TAG, it.stackTraceToString())
    }

    companion object {
        const val TAG = "FirebaseAdapter"
    }
}