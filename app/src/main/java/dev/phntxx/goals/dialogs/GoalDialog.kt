package dev.phntxx.goals.dialogs

import android.content.Context
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import dev.phntxx.goals.R

class GoalDialog(private val title: String, private val action: (String) -> Unit) : View.OnClickListener {

    private fun showGoalDialog(context: Context) {

        val goalTitleInput = EditText(context)
        goalTitleInput.inputType = InputType.TYPE_CLASS_TEXT

        val alertDialog = AlertDialog.Builder(context)
            .setTitle(this.title)
            .setView(goalTitleInput)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val goalTitle = goalTitleInput.text.toString()

                if (goalTitle.isEmpty()) {
                    goalTitleInput.error = context.getString(R.string.goal_title_length)

                } else {
                    this.action(goalTitle)
                    dialog.dismiss()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        alertDialog.show()
    }

    override fun onClick(p0: View?) {
        this.showGoalDialog(p0!!.context)
    }
}