package com.nextstacks.keepnotes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity implements TaskItemsAdapter.TaskDoneListener {


    private int INTENT_REQUEST_CODE = 3123;

    private DBHelper dbHelper;
    private RecyclerView mRcTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mRcTasks = findViewById(R.id.rc_tasks);
        mRcTasks.setLayoutManager(new GridLayoutManager(ViewActivity.this, 2));

        dbHelper = new DBHelper(ViewActivity.this);

        loadDataToDatabase();
    }


    public void onAddNewTaskClicked(View view) {
        startActivityForResult(new Intent(ViewActivity.this, TaskInsertActivity.class), INTENT_REQUEST_CODE);
    }

    private void loadDataToDatabase() {
        ArrayList<TaskDetail> taskDetails = new ArrayList<>();
        taskDetails = dbHelper.getDataFromDatabase(dbHelper.getReadableDatabase());

        TaskItemsAdapter adapter = new TaskItemsAdapter(ViewActivity.this, taskDetails);
        adapter.setListener(this);
        mRcTasks.setAdapter(adapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                loadDataToDatabase();
            }
        }
    }

    @Override
    public void onTaskItemUpdate(TaskDetail taskDetail, TaskItem taskItem, boolean isChecked) {
        ArrayList<TaskItem> clickedTaskItemList = TaskItem.convertTaskStringToList(taskDetail.taskItemsArrayValue);

        for (TaskItem updatedTaskItem : clickedTaskItemList) {
            if (updatedTaskItem.taskID == taskItem.taskID) {
                updatedTaskItem.isTaskDone = isChecked;
                break;
            }
        }
        String updateItemsArray = TaskItem.convertTaskListToString(clickedTaskItemList);

        dbHelper.updateDataToDatabase(dbHelper.getWritableDatabase(), taskDetail.taskTitle, updateItemsArray, taskDetail.id);

        mRcTasks.setAdapter(null);

        loadDataToDatabase();
    }

    @Override
    public void onUpdateClicked(TaskDetail taskDetail) {


        Intent updateIntent = new Intent(ViewActivity.this, TaskInsertActivity.class);
        updateIntent.putExtra(TaskInsertActivity.BUNDLE_IS_UPDATE, true);
        updateIntent.putExtra(TaskInsertActivity.BUNDLE_TASK, taskDetail);
        startActivityForResult(updateIntent, INTENT_REQUEST_CODE);
    }

    @Override
    public void onDeleteClicked(TaskDetail taskDetail) {

    }
}