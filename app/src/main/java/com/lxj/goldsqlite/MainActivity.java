package com.lxj.goldsqlite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation;
import com.lxj.gold.sqlite_core.dao.crud.provider.SQLiteObserver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.*;

/**
 * created by lixinjie on 2019/9/4
 */
public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private TextView mProcessInfoTv;
    private TextView mInfoTv;

    private EditText mOldNameEt;
    private EditText mNewNameEt;
    private EditText mDeleteEt;

    private Button mUpdateBt;
    private Button mDeleteBt;
    private Button mInsertStudentBt;
//    private Button mInsertTeacherBt;
    private Button mHandoverProcessBt;

    private ListView mStudentsLv;
    private ListViewAdapter mListViewAdapter;

    SQLiteObserver observer1 = new SQLiteObserver(null, OPERATION_DELETE) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听所有删除操作：" + "表名: " + tableName + "; 操作： " + operationType);
            Toast.makeText(MainActivity.this, "监听所有删除操作：" + "表名: " + tableName + "; 操作： " + operationType, Toast.LENGTH_SHORT).show();
        }
    };

    SQLiteObserver observer2 = new SQLiteObserver("Student", OPERATION_INSERT | OPERATION_DELETE | OPERATION_UPDATE) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听学生表增、删、改操作：" + "表名: " + tableName + "; 操作： " + operationType);
//            查询并更新数据
            queryDataAndUpdateListView();
        }
    };

    SQLiteObserver observer3 = new SQLiteObserver("Teacher", OPERATION_INSERT) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听老师表插入操作：" + "表名: " + tableName + "; 操作： " + operationType);
            Toast.makeText(MainActivity.this, "监听老师表插入操作：" + "表名: " + tableName + "; 操作： " + operationType, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoldSQLite.INSTANCE.resgisterObserver(observer1);
        GoldSQLite.INSTANCE.resgisterObserver(observer2);
        GoldSQLite.INSTANCE.resgisterObserver(observer3);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 15);
            }
        }

//        初始化View
        initView();
//        初始化按钮点击事件
        initListener();
//        初始化数据
        initData();
//        初始化ListView
        initListView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        mProcessInfoTv = findViewById(R.id.tv_process_info);
        mProcessInfoTv.setText(String.format(getString(R.string.process_info), android.os.Process.myPid()));
        mInfoTv = findViewById(R.id.tv_info);

        mInsertStudentBt = findViewById(R.id.bt_insert_student);
//        mInsertTeacherBt = findViewById(R.id.bt_insert_teacher);
        mUpdateBt = findViewById(R.id.bt_update);
        mDeleteBt = findViewById(R.id.bt_delete);
        mHandoverProcessBt = findViewById(R.id.bt_handover_process);

        mOldNameEt = findViewById(R.id.et_old_name);
        mNewNameEt = findViewById(R.id.et_new_name);
        mDeleteEt = findViewById(R.id.et_delete);

        mStudentsLv = findViewById(R.id.lv_data_list);
    }

    /**
     * 初始化事件点击监听
     */
    private void initListener(){
//        切换进程进入另外一个进程的Activity
        mHandoverProcessBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

//        新插入一个学生
        mInsertStudentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListViewAdapter != null && mListViewAdapter.getDataList() != null){
                    final Student student = new Student();
                    student.setName("小" + mListViewAdapter.getDataList().size());
                    student.setId(mListViewAdapter.getDataList().size());
                    student.setClassz("小学3年3班");
                    student.setSex("男");
                    student.setGrade(58);

                    GoldSQLite.INSTANCE.getInsertOperation("demo1", "Student")
                        .insert(student)
                        .build()
                        .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                            @Override
                            public void onResultReturn(@NotNull Integer result) {
                                Log.d(TAG, "插入行ID(学生表)：" + result);
                                mInfoTv.setText(String.format(getString(R.string.operation_info), "插入学生:" + "小" + mListViewAdapter.getDataList().size()));
                                Toast.makeText(MainActivity.this, "插入行：" + result, Toast.LENGTH_SHORT).show();
                            }
                    });
                }
            }
        });

//        根据名字删除一个学生
        mDeleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                获取输入的名称
                final String deleteName = mDeleteEt.getText().toString();
                if(!TextUtils.isEmpty(deleteName)){
                    GoldSQLite.INSTANCE.getDeleteOperation("demo1", "Student")
                            .where("name", deleteName)
                            .build()
                            .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                                @Override
                                public void onResultReturn(@NotNull Integer result) {
                                    Log.d(TAG, "删除行数：" + result);
                                    if(result > 0){
                                        mInfoTv.setText(String.format(getString(R.string.operation_info), "学生" + deleteName + "已删除"));
                                    }else {
                                        mInfoTv.setText(String.format(getString(R.string.operation_info), "学生:" + deleteName + "删除失败"));
                                    }
                                    Toast.makeText(MainActivity.this, "删除行数：" + result, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

//        更新学生名称
        mUpdateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String oldName = mOldNameEt.getText().toString().trim();
                final String newName = mNewNameEt.getText().toString().trim();
                if(!TextUtils.isEmpty(oldName) && !TextUtils.isEmpty(newName)){
                    GoldSQLite.INSTANCE.getUpdateOperation("demo1", "Student")
                            .where("name", oldName)
                            .update("name", newName)
                            .build()
                            .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                                @Override
                                public void onResultReturn(@NotNull Integer result) {
                                    Log.d(TAG, "更新行数：" + result);
                                    if(result > 0){
                                        mInfoTv.setText(String.format(getString(R.string.operation_info), "学生" + oldName + "已更名为: " + newName));
                                    }else {
                                        mInfoTv.setText(String.format(getString(R.string.operation_info), "学生:" + oldName + "更名失败"));
                                    }
                                    Toast.makeText(MainActivity.this, "更新行数：" + result, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        List<Student> students = new ArrayList<>();
        for(int i = 0; i < 8 ;i++){
            Student student1 = new Student();
            student1.setId(i);
            student1.setName("小" + i);
            if(i % 2 == 0){
                student1.setSex("女");
            }else {
                student1.setSex("男");
            }
            student1.setGrade(60 + i);
            students.add(student1);
        }
//        多行插入
        GoldSQLite.INSTANCE.getInsertOperation("demo1", "Student")
                .bulkInsert(students)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                    @Override
                    public void onResultReturn(@NotNull Integer result) {
                        Log.d(TAG, "插入行数：" + result);
                        mInfoTv.setText(String.format(getString(R.string.operation_info), "初始化数据，插入行数:" + result));
                    }
                });
    }

    /**
     * 初始化ListView
     */
    private void initListView(){
        mListViewAdapter = new ListViewAdapter(MainActivity.this);
        mStudentsLv.setAdapter(mListViewAdapter);
        queryDataAndUpdateListView();
    }

    /**
     * 查询数据并更新列表
     */
    private void queryDataAndUpdateListView(){
        GoldSQLite.INSTANCE.getQueryOperation("demo1", Student.class)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Object>>() {
                    @Override
                    public void onResultReturn(@NotNull List<Object> result) {

                        if(!result.isEmpty()){
                            Log.d(TAG, "查询行数：" + result.size());
                            List<Student> mResultList = new ArrayList<>();
                            for(Object object: result){
                                if(object instanceof Student){
                                    mResultList.add((Student)object);
                                }
                            }
                            mListViewAdapter.setDataList(mResultList);
                            mListViewAdapter.notifyDataSetChanged();
                        }else {
                            Log.d(TAG, "查询行数为0");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 15 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            final Teacher teacher = new Teacher();
            teacher.setId(0);
            teacher.setName("某某老师");
            teacher.setSex("男");
            teacher.setCourse("数学");

            GoldSQLite.INSTANCE.getInsertOperation("demo2", "Teacher")
                    .insert(teacher)
                    .build()
                    .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                        @Override
                        public void onResultReturn(@NotNull Integer result) {
                            Log.d(TAG, "插入行ID（老师表）：" + result);
//                            Toast.makeText(MainActivity.this, "插入老师表完毕:" + flag, Toast.LENGTH_SHORT).show();
                            mInfoTv.setText(String.format(getString(R.string.operation_info), "插入新的老师：id为" + result));
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoldSQLite.INSTANCE.ungisterObserver(observer1);
        GoldSQLite.INSTANCE.ungisterObserver(observer2);
        GoldSQLite.INSTANCE.ungisterObserver(observer3);
    }
}
