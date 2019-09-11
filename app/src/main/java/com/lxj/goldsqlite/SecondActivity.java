package com.lxj.goldsqlite;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation;
import com.lxj.gold.sqlite_core.dao.crud.provider.SQLiteObserver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.*;

/**
 * Created by lixinjie on 2019/8/5
 */
public class SecondActivity extends AppCompatActivity {
    private final String TAG = SecondActivity.class.getSimpleName();

    private TextView mProcessInfoTv;
    private TextView mInfoTv;

    private EditText mDeleteEt;
    private Button mDeleteBt;

    private Button mInsertTearcherBt;

    private ListView mTeachersLv;
    private ListViewAdapter mListViewAdapter;

    private SQLiteObserver observer5 = new SQLiteObserver("Teacher", OPERATION_INSERT | OPERATION_DELETE | OPERATION_UPDATE) {
        @Override
        public void onChange(@NotNull String tableName, int operationType) {
            Log.d(TAG, "监听老师表增、删、改操作：" + "表名: " + tableName + "; 操作： " + operationType);
//            查询并更新数据
            queryDataAndUpdateListView();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        GoldSQLite.INSTANCE.resgisterObserver(observer5);

//        初始化视图
        initView();
//        初始化按钮点击事件
        initListener();
//        初始化ListView
        initListView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mProcessInfoTv = findViewById(R.id.tv_process_info);
        mProcessInfoTv.setText(String.format(getString(R.string.process_info), android.os.Process.myPid()));
        mInfoTv = findViewById(R.id.tv_info);

        mTeachersLv = findViewById(R.id.lv_data_list);

        mInsertTearcherBt = findViewById(R.id.bt_insert_teacher);
        mDeleteBt = findViewById(R.id.bt_delete);

        mDeleteEt = findViewById(R.id.et_delete);
    }

    /**
     * 初始化ListView
     */
    private void initListView(){
        mListViewAdapter = new ListViewAdapter(SecondActivity.this);
        mTeachersLv.setAdapter(mListViewAdapter);
        queryDataAndUpdateListView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化事件点击监听
     */
    private void initListener(){
//        新插入一个教师
        mInsertTearcherBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInsertTearcherBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mListViewAdapter != null && mListViewAdapter.getDataList() != null){
                            final Teacher teacher = new Teacher();
                            teacher.setId(mListViewAdapter.getDataList().size());
                            teacher.setName("老师" + mListViewAdapter.getDataList().size());
                            teacher.setSex("女");
                            teacher.setCourse("语文");

                            GoldSQLite.INSTANCE.getInsertOperation("demo2", "Teacher")
                                    .insert(teacher)
                                    .build()
                                    .execute(new ABaseDbOperation.OnDaoFinishedCallback<Integer>() {
                                        @Override
                                        public void onResultReturn(@NotNull Integer result) {
                                            Log.d(TAG, "插入行ID（老师表）：" + result);
                                            mInfoTv.setText(String.format(getString(R.string.operation_info), "插入老师:" + "老师" + mListViewAdapter.getDataList().size()));
                                            Toast.makeText(SecondActivity.this, "插入ID（老师表）：" + result, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
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
                                    Toast.makeText(SecondActivity.this, "删除行数：" + result, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    /**
     * 查询数据并更新列表
     */
    private void queryDataAndUpdateListView(){
        GoldSQLite.INSTANCE.getQueryOperation("demo2", Teacher.class)
                .build()
                .execute(new ABaseDbOperation.OnDaoFinishedCallback<List<Object>>() {
                    @Override
                    public void onResultReturn(@NotNull List<Object> result) {
                        if(!result.isEmpty()){
                            Log.d(TAG, "查询行数：" + result.size());
                            mInfoTv.setText(String.format(getString(R.string.operation_info), "查询老师表行数：" + result.size()));
                            List<Teacher> mResultList = new ArrayList<>();
                            for(Object object: result){
                                if(object instanceof Teacher){
                                    mResultList.add((Teacher)object);
                                }
                                mListViewAdapter.setDataList(mResultList);
                                mListViewAdapter.notifyDataSetChanged();
                            }
                        }else {
                            Log.d(TAG, "查询行数为0");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoldSQLite.INSTANCE.ungisterObserver(observer5);
    }
}
