package justcodeenterprise.com.learnactivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //1 You initialize the activity’s properties, which include an empty mutable list of tasks and an adapter initialized using by lazy.
    private val taskList = mutableListOf<String>()
    private val adapter by lazy { makeAdapter(taskList) }

    private val ADD_TASK_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        //2. You call onCreate() on the superclass
        super.onCreate(savedInstanceState)

        //3. You set the content view of your activity with the corresponding layout file resource.
        setContentView(R.layout.activity_main)

        //4 Here you set up the adapter for taskListView. The reference to taskListView is initialized using Kotlin Android Extensions,
        //This replaces findViewById() calls and the need for other view-binding libraries.
        taskListView.adapter = adapter


        //5 You add an empty OnItemClickListener() to the ListView to capture the user’s taps on individual list entries.
        // The listener is a Kotlin lambda.
        taskListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 1 You check the requestCode to ensure the activity result is indeed for your add task request you started with
        // TaskDescriptionActivity.
        if (requestCode == ADD_TASK_REQUEST) {
            // 2 You make sure the resultCode is RESULT_OK — the standard activity result for a successful operation.
            if (resultCode == Activity.RESULT_OK) {
                // 3 Here you extract the task description from the result intent and, after a null check with the let function,
                // add it to your list.
                val task = data?.getStringExtra(TaskDescriptionActivity.EXTRA_TASK_DESCRIPTION)
                task?.let {
                    taskList.add(task)
                    // 4 Finally, you call notifyDataSetChanged() on your list adapter. In turn, it notifies the ListView
                    // about changes in your data model so it can trigger a refresh of its view.
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    //6 An empty on-click method for the “ADD A TASK” button, designated by the activity_main.xml layout.
    fun addTaskClicked(view: View) {

        //When the user taps the “ADD A TASK” button, the Android OS calls addTaskClicked(). Here you create an Intent
        // to launch the TaskDescriptionActivity from MainActivity.

        val intent = Intent(this, TaskDescriptionActivity::class.java)
        startActivityForResult(intent, ADD_TASK_REQUEST)

    }

    //7 A private function that initializes the adapter for the list view. Here you are using the Kotlin = syntax
    // for a single-expression function.
    private fun makeAdapter(list: List<String>): ArrayAdapter<String> =
        ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
}
