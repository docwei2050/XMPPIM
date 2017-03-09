package utils;

import android.os.Handler;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    utils
 *  @文件名:   ThreadUtils
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 21:52
 *  @描述：    TODO
 */
public class ThreadUtils {
    private static final String TAG = "ThreadUtils";
    public static Handler mHandler=new Handler();
    public static void runInMainThread(Runnable task){
        mHandler.post(task);
    }
    public static void runInThread(Runnable task){
        new Thread(task).start();
    }
}
