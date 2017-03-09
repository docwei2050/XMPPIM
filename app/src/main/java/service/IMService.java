package service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import db.ContactSqliteOpenHelper;
import db.MyContentProvider;
import db.SmsProvider;
import db.SmsSqliteOpenHelper;
import ui.LoginActivity;
import utils.PingYingUtil;
import utils.ThreadUtils;

/*
 *  @项目名：  xmppDemo 
 *  @包名：    service
 *  @文件名:   IMService
 *  @创建者:   Administrator
 *  @创建时间:  2017/3/6 23:33
 *  @描述：    TODO
 */
public class IMService extends Service{
    private static final String TAG         = "IMService";
    //操作数据库的uri
    public static       Uri    content_uri =Uri.parse("content://"+ MyContentProvider.AUTHORITIES+"/accounts");
    public static final Uri Content_URI=Uri.parse("content://"+ SmsProvider.AUTHORITES+"/sms");
    public static XMPPConnection conn;
    public static String currentAccount;
    private         Roster         mRoster;
    private ChatManager mManager;
    private Chat                 mChat;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }
    public class MyBinder extends Binder{
        //内部类可以直接获取外部类对象
        //拿到service对象后就可以使用service里面的方法了
        public IMService getIMService(){
            return IMService.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //同步联系人，避免fragment多次创建多次同步，
        //监听花名册改变
        //获取花名册
        //联系人同步应该放在子线程里面的
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                mRoster = conn.getRoster();
                mRoster.addRosterListener(mMyRosterListener);
                Collection<RosterEntry> entries = mRoster.getEntries();
                for(RosterEntry entry:entries){
                    String nikeName=entry.getName();
                    String JID=entry.getUser();
                    //更新或者插入数据库
                    System.out.println(nikeName+"_rrrrrrrrrrrrr-"+JID);
                    updateOrInsertEntry(entry);
                }

            }
        });
        //同步消息的话，应该也要放在服务中，因为activity很容易销毁必须重新创建
        //创建聊天对象，然后组装消息
        if(mManager==null) {
            mManager = conn.getChatManager();
            mManager.addChatListener(mMyChatManagerListener);
        }

    }
    public Map<String,Chat> mChatMap = new HashMap<>();
    //这个方法主要是给会话activity调用的，需要使用绑定服务，调用服务中的方法
     public void sendMessage(final String otherAccount, String body){
         //要满足创建多个Chat对象的，但是又不能每次发送信息的时候都要创建，
         //至少满足每一个聊天对象创建对应的chat，创建之后就要复用，所以这里就需要保存已经会话过的chat
         //没有的话就需要创建
         //这里使用集合保存
         if(!mChatMap.containsKey(otherAccount)){
             //主动创建聊天，并且对聊天信息做一个监听，需要一个传入的其他人的JID
             mChat = mManager.createChat(otherAccount, mMyMessageListener);
             mChatMap.put(otherAccount,mChat);
         }
         mChat=mChatMap.get(otherAccount);

         final Message message =new Message();
         message.setBody(body);
         message.setFrom(IMService.currentAccount);
         message.setTo(otherAccount);
         message.setType(Message.Type.chat);
         ThreadUtils.runInThread(new Runnable() {
             @Override
             public void run() {
                 try {
                     mChat.sendMessage(message);
                     //发送成功保存下消息
                     saveMessage(message,otherAccount);
                 } catch (XMPPException e) {
                     e.printStackTrace();
                 }
             }
         });
     }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
//-----------联系人的-----------start------------
    public MyRosterListener mMyRosterListener =new MyRosterListener();
    private class MyRosterListener implements RosterListener {
        @Override
        public void entriesAdded(Collection<String> collection) {
            for(String username:collection){
                //根据花名册拿到entry
                RosterEntry entry=mRoster.getEntry(username);
                updateOrInsertEntry(entry);
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            for(String username:collection){
                RosterEntry entry=mRoster.getEntry(username);
                updateOrInsertEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            for(String username:collection){
                getContentResolver().delete(content_uri,ContactSqliteOpenHelper.USERNAME+"=?", new String[]{username});
            }

        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    }
    private void updateOrInsertEntry(RosterEntry entry) {
        ContentValues values   =new ContentValues();
        String        username =entry.getUser();
        String        nickname =entry.getName();
        if(TextUtils.isEmpty(nickname)){
            nickname=username.substring(0,username.indexOf("@"));
        }
        values.put(ContactSqliteOpenHelper.NICKNAME,nickname);
        //有可能昵称没有设置，我们需要给他设置一下

        values.put(ContactSqliteOpenHelper.PINTYIN, PingYingUtil.getPingYing(nickname));
        if(!TextUtils.isEmpty(username)){
            int number=getContentResolver().update(content_uri,values,ContactSqliteOpenHelper.USERNAME+" = ?",new String[]{username});
            if(number==0){
                values.put(ContactSqliteOpenHelper.USERNAME,username);
               getContentResolver().insert(content_uri,values);
            }
        }

    }
    //-----------联系人的-----------end----------
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMyRosterListener!=null&&mRoster!=null){
            mRoster.removeRosterListener(mMyRosterListener);
        }
        if(mChat!=null&&mMyMessageListener!=null){
            mChat.removeMessageListener(mMyMessageListener);
        }
        if(mManager!=null&&mMyChatManagerListener!=null){
            mManager.removeChatListener(mMyChatManagerListener);
        }

    }
    //-----------监听消息-----------start-------------
    //给ChatManager做监听，用于其他人创建的聊天，给他添加监听
    public MyChatManagerListener mMyChatManagerListener=new MyChatManagerListener();
    class MyChatManagerListener implements ChatManagerListener{

        @Override
        public void chatCreated(Chat chat, boolean b) {
            System.out.println("b--"+b);
            //true的话就是我创建的，false就是别人创建的
            if(!b){
                //别人创建的了chat，我没必要再创建
                String otherAccount=chat.getParticipant();//docwei@docweo.com/Spark
                mChatMap.put(filterAccount(otherAccount),chat);
                chat.addMessageListener(mMyMessageListener);
            }
        }
    }
    //添加消息监听
    public MyMessageListener mMyMessageListener=new MyMessageListener();
    class MyMessageListener implements MessageListener {

        @Override
        public void processMessage(Chat chat, Message message) {
            //其他的消息
            if(message!=null) {
                saveMessage(message, chat.getParticipant());
            }

        }
    }
    public void saveMessage(Message msg,String sessionaccount){
        ContentValues values=new ContentValues();
        values.put(SmsSqliteOpenHelper.FROMACCOUNT,filterAccount(msg.getFrom()));
        values.put(SmsSqliteOpenHelper.TOACCOUNT,filterAccount(msg.getTo()));
        values.put(SmsSqliteOpenHelper.TYPE,msg.getType()+"");
        values.put(SmsSqliteOpenHelper.BODY,msg.getBody());
        values.put(SmsSqliteOpenHelper.STATUS,"offline");
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        values.put(SmsSqliteOpenHelper.TIME,date);
        values.put(SmsSqliteOpenHelper.SESSIONACCOUNT,filterAccount(sessionaccount));
        getContentResolver().insert(Content_URI,values);
    }
    public String filterAccount(String account){
        return account.substring(0,account.indexOf("@"))+"@"+ LoginActivity.SERVERNAME;
    }
    //-----------监听消息-----------end------------

}
