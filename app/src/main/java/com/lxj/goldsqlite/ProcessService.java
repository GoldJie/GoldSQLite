package com.lxj.goldsqlite;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by lixinjie on 2019/8/7
 */
public class ProcessService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
