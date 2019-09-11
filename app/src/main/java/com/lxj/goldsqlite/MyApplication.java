package com.lxj.goldsqlite;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.db.DbStateListener;

/**
 * Created by lixinjie on 2019/7/10
 */
public class MyApplication extends Application {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        GoldSQLite.INSTANCE.init(this);

        Intent intent = new Intent(getApplicationContext(), ProcessService.class);
//        留意8.0后系统对startService的严格控制，此处为Example，就不做特殊处理了
        startService(intent);
        GoldSQLite.INSTANCE.resgisterDbListener(new DbStateListener() {
            @Override
            public void onCreate(@NonNull String dbName) {

            }

            @Override
            public void onUpgrate(@NonNull String dbName, int oldVersion, int newVersion) {

            }
        });
    }
}
