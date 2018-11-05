package justcodeenterprise.com.learnactivity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class TaskDescriptionActivity : AppCompatActivity() {

    //1. Used the Kotlin companion object for the class to define attributes common across the class, similar to static members in Java.
    companion object {
        val EXTRA_TASK_DESCRIPTION = "task"
    }

    //2 Overriden the onCreate() lifecycle method to set the content view for the activity from the layout file.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_description)
    }

    // 3 Added an empty click handler that will be used to finish the activity.
    fun doneClicked(view: View) {

    }
}
