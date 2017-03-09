package utils;

import android.content.Context;
import android.widget.Toast;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    utils
 *  @文件名:   ToastUtil
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 21:59
 *  @描述：    TODO
 */
public class ToastUtil {
    private static final String TAG = "ToastUtil";
    public static void toastSafe(final Context context, final String message){
        ThreadUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
