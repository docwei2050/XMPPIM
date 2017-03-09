package utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    utils
 *  @文件名:   PingYingUtil
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/7 19:07
 *  @描述：    TODO
 */
public class PingYingUtil {
    private static final String TAG = "PingYingUtil";
    public static String getPingYing(String content){
        //没有声调的
         return PinyinHelper.convertToPinyinString(content, null, PinyinFormat.WITHOUT_TONE);
    }
}
