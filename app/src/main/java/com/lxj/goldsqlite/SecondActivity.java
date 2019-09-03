package com.lxj.goldsqlite;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation;

import java.util.List;

/**
 * Created by lixinjie on 2019/8/5
 */
public class SecondActivity extends AppCompatActivity {
    private final String TAG = SecondActivity.class.getSimpleName();
    TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textView = findViewById(R.id.textView);
        try {
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
//
        Teacher teacher = new Teacher();
        teacher.setId(2);
        teacher.setName("老师2");
        teacher.setSex("女");
        teacher.setCourse("语文");

            GoldSQLite.INSTANCE.getInsertOperation("demo2", "Teacher")
                .insert(teacher)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                    @Override
                    public void onResultReturn(Integer result) {
                        Log.d(TAG, "插入行ID（老师表）：" + result);
                    }
                });

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
//    //                                                "; 班级：" + student1.getClassz() + "; 成绩:" +student1.getGrade() + "; 家庭：" + student1.getFamily();
//                                        textView.setText(info);
//                                    }
//                                }
//                            }else {
//                                Log.d(TAG, "查询行数为0");
//                            }
//                        }
//                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error: " + e.getMessage());
        }
    }
}
