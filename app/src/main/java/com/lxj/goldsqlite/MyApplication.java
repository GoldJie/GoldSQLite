package com.lxj.goldsqlite;

import android.app.Application;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.lxj.gold.sqlite_core.GoldSQLite;
import com.lxj.gold.sqlite_core.db.DbStateListener;

/**
 * Created by lixinjie on 2019/7/10
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        GoldSQLite.INSTANCE.init(this);

        Intent intent = new Intent(getApplicationContext(), ProcessService.class);
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
