package com.lxj.goldsqlite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation;
import com.lxj.gold.sqlite_core.dao.crud.provider.SQLiteObserver;
import com.lxj.gold.sqlite_core.db.TableHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.ALL_OPERATION;
import static com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_INSERT;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    TextView textView;
    Button button;
    boolean flag;

    SQLiteObserver observer1 = new SQLiteObserver(null, OPERATION_INSERT) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听插入：" + "表名: " + tableName + "; 操作： " + operationType);
        }
    };

    SQLiteObserver observer2 = new SQLiteObserver("Student", ALL_OPERATION) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听学生表所有操作：" + "表名: " + tableName + "; 操作： " + operationType);
        }
    };

    SQLiteObserver observer3 = new SQLiteObserver("Teacher", OPERATION_INSERT) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听老师表插入操作：" + "表名: " + tableName + "; 操作： " + operationType);
        }
    };

//    SQLiteObserver observer4 = new SQLiteObserver() {
//        @Override
//        public void onChange(SQLiteEvent event) {
//            Log.d(TAG, "监听修改："+ "表名: " +event.getTableName() + "; 操作： " + event.getOperationType() + "; 结果： " + event.getOperationResult());
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        GoldSQLite.INSTANCE.resgisterObserver(observer1);
        GoldSQLite.INSTANCE.resgisterObserver(observer2);
        GoldSQLite.INSTANCE.resgisterObserver(observer3);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 15);
            }
        }
        textView = findViewById(R.id.tv_text);
        button = findViewById(R.id.bt_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                flag = true;
            }
        });

        final Student student = new Student();
        student.setId(9);
        student.setName("小明");
        student.setClassz("小学3年3班");
        student.setSex("男");
        student.setGrade(58);
        List<Student> students = new ArrayList<>();
        for(int i = 0; i < 8 ;i++){
            Student student1 = new Student();
            student1.setId(i);
            student1.setName("小" + i);
            student1.setSex("男");
            student1.setGrade(122);
            students.add(student1);
        }
        GoldSQLite.INSTANCE.getInsertOperation("demo1", "Student")
                .bulkInsert(students)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                    @Override
                    public void onResultReturn(Integer result) {
                        Log.d(TAG, "插入行数：" + result);
                    }
                });
//        SimpleSQLite.getInsertOperation("demo1", "Student")
//                .insert(student)
//                .build()
//                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
//                    @Override
//                    public void onResultReturn(Integer result) {
//                        Log.d(TAG, "插入行数：" + result);
////                        Toast.makeText(MainActivity.this, "插入行数：" + result, Toast.LENGTH_SHORT).show();
//                    }
//                });

        Map<String, String> valueMap = new ArrayMap<>();
        valueMap.put("name", "小7");
        GoldSQLite.INSTANCE.getUpdateOperation("demo1", "Student")
                .where("name", "小7")
                .update("name", "小明")
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                    @Override
                    public void onResultReturn(Integer result) {
                        Log.d(TAG, "更新行数：" + result);
//                        Toast.makeText(MainActivity.this, "更新行数：" + result, Toast.LENGTH_SHORT).show();
                    }
                });
//        GoldSQLite.INSTANCE.getQueryOperation("demo1", Student.class)
//                .build()
//                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Student>>() {
//                    @Override
//                    public void onResultReturn(List<Student> result) {
//
//                        if(result != null && result.size() != 0){
//                            Log.d(TAG, "查询行数：" + result.size());
//                            for(Student student1: result){
//                                if(student1 != null){
//                                    String info = "id: " + student1.getId() + "; 名称:" + student1.getName() + "; 性别：" + student1.getSex() +
//                                            "; 班级：" + student1.getClassz() + "; 成绩:" +student1.getGrade() + "; 家庭：" + student1.getFamily();
//                                    textView.setText(info);
//                                }
//                            }
//                        }else {
//                            Log.d(TAG, "查询行数为0");
//                        }
//                    }
//                });

//        boolean isExist = TableHelper.getInstance().isExist(db, "Teacher");
//        Toast.makeText(MainActivity.this, "是否存在：" + isExist, Toast.LENGTH_SHORT).show();
        GoldSQLite.INSTANCE.getDeleteOperation("demo1", "Student")
                .where("name", "小明")
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                    @Override
                    public void onResultReturn(Integer result) {
                        Log.d(TAG, "删除行数：" + result);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        GoldSQLite.INSTANCE.getQueryOperation("demo1", Student.class)
//                .build()
//                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Student>>() {
//                    @Override
//                    public void onResultReturn(List<Student> result) {
//
//                    }
//                });
//                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Student>>() {
//                    @Override
//                    public void onResultReturn(List<Student> result) {
//
//                        if(result != null && result.size() != 0){
//                            Log.d(TAG, "查询行数：" + result.size());
//                            for(Student student1: result){
//                                if(student1 != null){
//                                    String info = "id: " + student1.getId() + "; 名称:" + student1.getName() + "; 性别：" + student1.getSex() +
//                                            "; 班级：" + student1.getClassz() + "; 成绩:" +student1.getGrade() + "; 家庭：" + student1.getFamily();
//                                    textView.setText(info);
//                                }
//                            }
//                        }else {
//                            Log.d(TAG, "查询行数为0");
//                        }
//                    }
//                });
        if(flag){
//            GoldSQLite.INSTANCE.getQueryOperation("demo2", Teacher.class)
//                    .build()
//                    .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Teacher>>() {
//                        @Override
//                        public void onResultReturn(List<Teacher> result) {
//
//                            if(result != null && result.size() != 0){
//                                Log.d(TAG, "查询行数：" + result.size());
//                                for(Teacher student1: result){
//                                    if(student1 != null){
//                                        String info = "id: " + student1.getId() + "; 名称:" + student1.getName() + "; 性别：" + student1.getSex();
////                                                "; 班级：" + student1.getClassz() + "; 成绩:" +student1.getGrade() + "; 家庭：" + student1.getFamily();
////                                        textView.setText(info);
//                                    }
//                                }
//                            }else {
//                                Log.d(TAG, "查询行数为0");
//                            }
//                        }
//                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 15 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//            Toast.makeText(MainActivity.this, "通过权限", Toast.LENGTH_SHORT).show();

            boolean isExist = TableHelper.INSTANCE.isExist(GoldSQLite.INSTANCE.getSQLiteDb("demo2"), "Teacher");


            final Teacher teacher = new Teacher();
            teacher.setId(1);
            teacher.setName("老师");
            teacher.setSex("男");
            teacher.setCourse("数学");

            GoldSQLite.INSTANCE.getInsertOperation("demo2", "Teacher")
                    .insert(teacher)
                    .build()
                    .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                        @Override
                        public void onResultReturn(Integer result) {
                            Log.d(TAG, "插入行ID（老师表）：" + result);
                            flag = true;
                            Toast.makeText(MainActivity.this, "插入教师表完毕:" + flag, Toast.LENGTH_SHORT).show();
                        }
                    });
            Log.d("activity", "current Thread: " + android.os.Process.myTid());
            Toast.makeText(MainActivity.this, "是否存在：" + isExist, Toast.LENGTH_SHORT).show();
//            textView.setText("查询中");
//            Toast.makeText(MainActivity.this, "插入教师表:" + flag, Toast.LENGTH_SHORT).show();

//            GoldSQLite.INSTANCE.getQueryOperation("demo2", Teacher.class)
//                    .build()
//                    .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Teacher>>() {
//                        @Override
//                        public void onResultReturn(List<Teacher> result) {
//
//                            if(result != null && result.size() != 0){
//                                Log.d(TAG, "查询行数：" + result.size());
//                                for(Teacher student1: result){
//                                    if(student1 != null){
//                                        String info = "id: " + student1.getId() + "; 名称:" + student1.getName() + "; 性别：" + student1.getSex();
////                                                "; 班级：" + student1.getClassz() + "; 成绩:" +student1.getGrade() + "; 家庭：" + student1.getFamily();
//                                        textView.setText(info);
//                                    }
//                                }
//                            }else {
//                                Log.d(TAG, "查询行数为0");
//                            }
//                        }
//                    });
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
