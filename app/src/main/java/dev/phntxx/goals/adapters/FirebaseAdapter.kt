package dev.phntxx.goals.adapters

import android.net.Uri
import android.util.Log
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dev.phntxx.goals.models.GoalModel
import java.util.*

class FirebaseAdapter {

    private var auth = Firebase.auth
    private var database = Firebase.firestore
    private var storage = FirebaseStorage.getInstance()

    var user = auth.currentUser
    var userId = user!!.uid

    fun getGoal(goalId: String, onSuccessListener: OnSuccessListener<GoalModel>, onFailureListener: OnFailureListener = defaultFailureListener) {
        database
            .collection("goals")
            .document(goalId)
            .get()
            .addOnSuccessListener {
                onSuccessListener.onSuccess(GoalModel.fromDocumentSnapshot(it))
            }
            .addOnFailureListener(onFailureListener)
    }

    fun createGoal(goal: GoalModel, onSuccessListener: OnSuccessListener<String>, onFailureListener: OnFailureListener = defaultFailureListener) {
        database
            .collection("goals")
            .add(goal)
            .addOnSuccessListener{
                onSuccessListener.onSuccess(it.id)
            }
            .addOnFailureListener(onFailureListener)
    }

    fun updateGoal(goalId: String, goal: GoalModel, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        database
            .collection("goals")
            .document(goalId)
            .update(goal.toMap())
            .addOnFailureListener(onFailureListener)
            .addOnSuccessListener(onSuccessListener)
    }

    fun deleteGoal(goalId: String, onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener = defaultFailureListener) {
        getGoal(goalId, {
            if (it.imageUUID != null) deleteGoalImage(it.imageUUID!!)
        })

        database
            .collection("goals")
            .document(goalId)
            .delete()
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
        val query = database
            .collection("goals")
            .whereEqualTo("uid", userId)

        val options = FirestoreRecyclerOptions.Builder<GoalModel>()
            .setQuery(query, GoalModel::class.java)
            .build()

        return GoalAdapter(options)
    }

    fun getStorageRef(uuid: String): StorageReference {
        return storage.getReference("images").child(uuid)
    }

    fun signOut() {
        auth.signOut()
    }

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