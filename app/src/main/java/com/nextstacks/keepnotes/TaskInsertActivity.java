package com.nextstacks.keepnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TaskInsertActivity extends AppCompatActivity {

    private EditText mEtTaskTitle;
    private LinearLayout mLlInsertTasks;
    private LinearLayout mLlAddListItem;
    private Button mBtnAddTask;

    private DBHelper dbHelper;

    private int taskID = 0;

    private int taskDetailID;

    private ArrayList<TaskItem> taskItemList;

    public static final String BUNDLE_IS_UPDATE = "is_update";
    public static final String BUNDLE_TASK = "task";
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_insert);

        mLlAddListItem = findViewById(R.id.ll_add_item);
        mBtnAddTask = findViewById(R.id.btn_enter_data);

        mEtTaskTitle = findViewById(R.id.et_task_title);
        mLlInsertTasks = findViewById(R.id.ll_dynamic_tasks);

        mLlAddListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertTaskItem();
            }
        });

        mBtnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTaskEnterClicked();
            }
        });

        taskItemList = new ArrayList<>();
        dbHelper = new DBHelper(TaskInsertActivity.this);

        Bundle data = getIntent().getExtras();
        if (data != null) {
            isUpdate = data.getBoolean(BUNDLE_IS_UPDATE);
            TaskDetail updateDetail = (TaskDetail) data.getSerializable(BUNDLE_TASK);

            taskDetailID = updateDetail.id;
            mEtTaskTitle.setText(updateDetail.taskTitle);

            if (updateDetail != null) {
                ArrayList<TaskItem> taskItems = TaskItem.convertTaskStringToList(updateDetail.taskItemsArrayValue);

                for (int i = 0; i < taskItems.size(); i++) {
                    final TaskItem itemValue = taskItems.get(i);
                    View taskView = LayoutInflater.from(TaskInsertActivity.this).inflate(R.layout.cell_insert_item, null);
                    final EditText mEtTaskItem = taskView.findViewById(R.id.et_task_item);
                    final ImageView mIvTaskDone = taskView.findViewById(R.id.iv_task_done);

                    mIvTaskDone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            TaskItem taskItem = new TaskItem();
//                            taskItem.taskID = itemValue.taskID;
//                            taskItem.taskItem = mEtTaskItem.getText().toString();

                            for (TaskItem task : taskItemList){
                                if(task.taskID == itemValue.taskID){
                                    task.taskItem = mEtTaskItem.getText().toString();
                                }
                            }

                            mLlAddListItem.setEnabled(true);
                            mBtnAddTask.setEnabled(true);
                            mLlAddListItem.setAlpha(1.0f);
                            mBtnAddTask.setAlpha(1.0f);
                            mIvTaskDone.setVisibility(View.GONE);
                        }
                    });

                    mEtTaskItem.setText(itemValue.taskItem);


                    TaskItem updatedTaskItem = new TaskItem();
                    updatedTaskItem.taskID = itemValue.taskID;
                    updatedTaskItem.taskItem = itemValue.taskItem;
                    updatedTaskItem.isTaskDone = itemValue.isTaskDone;

                    taskItemList.add(updatedTaskItem);

                    taskID++;

                    mLlInsertTasks.addView(taskView);

                }
            }

            if (isUpdate) {
                mBtnAddTask.setText("Update Task");
            }
        }
    }


    private void insertTaskItem() {
        taskID++;

        mLlAddListItem.setEnabled(false);
        mLlAddListItem.setAlpha(0.5f);
        mBtnAddTask.setEnabled(false);
        mBtnAddTask.setAlpha(0.5f);


        View taskView = LayoutInflater.from(TaskInsertActivity.this).inflate(R.layout.cell_insert_item, null);
        final EditText mEtTaskItem = taskView.findViewById(R.id.et_task_item);
        final ImageView mIvTaskDone = taskView.findViewById(R.id.iv_task_done);

        mIvTaskDone.setVisibility(View.GONE);

        mEtTaskItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mIvTaskDone.setVisibility(editable.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        mIvTaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskItem taskItem = new TaskItem();
                taskItem.taskID = taskID;
                taskItem.taskItem = mEtTaskItem.getText().toString();

                taskItemList.add(taskItem);

                mLlAddListItem.setEnabled(true);
                mBtnAddTask.setEnabled(true);
                mLlAddListItem.setAlpha(1.0f);
                mBtnAddTask.setAlpha(1.0f);
                mIvTaskDone.setVisibility(View.GONE);
            }
        });

        mLlInsertTasks.addView(taskView);
    }


    private void onTaskEnterClicked() {
        String taskTitle = mEtTaskTitle.getText().toString();

        if (taskTitle.isEmpty() || taskItemList.size() == 0) {
            Toast.makeText(TaskInsertActivity.this, "Title or Items are empty", Toast.LENGTH_LONG).show();
            return;
        }


        String itemsArray = TaskItem.convertTaskListToString(taskItemList);

        if (!isUpdate) {
            dbHelper.insertDataToDatabase(dbHelper.getWritableDatabase(), taskTitle, itemsArray);
        } else {
            dbHelper.updateDataToDatabase(dbHelper.getWritableDatabase(), taskTitle, itemsArray, taskDetailID);
        }
        setResult(Activity.RESULT_OK);
        finish();
    }
}