package justcodeenterprise.com.learnactivity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //1 You initialize the activity’s properties, which include an empty mutable list of tasks and an adapter initialized using by lazy.
    private val taskList = mutableListOf<String>()
    private val adapter by lazy { makeAdapter(taskList) }

    private val ADD_TASK_REQUEST = 1

    private val tickReceiver by lazy { makeBroadcastReceiver() }

    private val PREFS_TASKS = "prefs_tasks"
    private val KEY_TASKS_LIST = "tasks_list"

    companion object {
        private const val LOG_TAG = "MainActivityLog"

        private fun getCurrentTimeStamp(): String {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val now = Date()
            return simpleDateFormat.format(now)
        }
    }

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
        //taskListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> }
        taskListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            taskSelected(position)
        }


        //Retrieve save data
        val savedList = getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).getString(KEY_TASKS_LIST, null)
        if (savedList != null) {
            val items = savedList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            taskList.addAll(items)
        }
    }

    override fun onResume() {
        // 1
        super.onResume()
        // 2 You update the date and time TextView with the current time stamp, because the broadcast receiver is not currently registered.
        dateTimeTextView.text = getCurrentTimeStamp()
        // 3 You then register the broadcast receiver in onResume(). This ensures it will receive the broadcasts for ACTION_TIME_TICK.
        // These are sent every minute after the time changes.
        registerReceiver(tickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
    }

    override fun onPause() {
        // 4
        super.onPause()
        // 5
        try {
            unregisterReceiver(tickReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e(MainActivity.LOG_TAG, "Time tick Receiver not registered", e)
        }
    }

    //Persist task
    override fun onStop() {
        super.onStop()

        // Save all data which you want to persist.
        val savedList = StringBuilder()
        for (task in taskList) {
            savedList.append(task)
            savedList.append(",")
        }

        getSharedPreferences(PREFS_TASKS, Context.MODE_PRIVATE).edit()
            .putString(KEY_TASKS_LIST, savedList.toString()).apply()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }


    //after second task finish, retrieve data pass from second task
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


    //Here, you create a BroadcastReceiver that sets the date and time on the screen if it receives a time change broadcast
    // from the system.
    private fun makeBroadcastReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent?) {
                if (intent?.action == Intent.ACTION_TIME_TICK) {
                    dateTimeTextView.text = getCurrentTimeStamp()
                }
            }
        }
    }


    //Delete item
    private fun taskSelected(position: Int) {
        // 1
        AlertDialog.Builder(this)
            // 2
            .setTitle(R.string.alert_title)
            // 3
            .setMessage(taskList[position])
            .setPositiveButton(R.string.delete, { _, _ ->
                taskList.removeAt(position)
                adapter.notifyDataSetChanged()
            })
            .setNegativeButton(R.string.cancel, {
                    dialog, _ -> dialog.cancel()
            })
            // 4
            .create()
            // 5
            .show()
    }
}
