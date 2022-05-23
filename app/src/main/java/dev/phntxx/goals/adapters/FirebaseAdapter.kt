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
                onSuccessListener.onSuccess(GoalModel.fromDataSnapshot(it))
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

    fun updateGoal(goalId: String, goal: GoalModel, onSuccessListener: OnSuccessListener<Void> = emptyOnSuccessListener, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .updateChildren(goal.toMap())
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

    fun addTasktoGoal(goalId: String, task: TaskModel, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        val pushKey = ref.push().key ?: ""
        task.key = pushKey

        ref
            .child(goalId)
            .child("tasks")
            .child(pushKey)
            .setValue(task)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    fun removeTaskFromGoal(goalId: String, task: TaskModel, onSuccessListener: OnSuccessListener<Void> = emptyOnSuccessListener, onFailureListener: OnFailureListener = defaultFailureListener) {
        ref
            .child(goalId)
            .child("tasks")
            .child(task.key ?: "")
            .removeValue()
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

        Log.d(TAG, query.toString())

        val options = FirebaseRecyclerOptions.Builder<TaskModel>()
            .setQuery(query, TaskModel::class.java)
            .build()

        return TaskAdapter(options)
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