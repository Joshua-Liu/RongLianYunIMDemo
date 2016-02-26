package com.xwhcoder.ronglianimdemo.activity;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xwhcoder.ronglianimdemo.R;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessageBody;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.io.File;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = "Chat";
    private Context context;

    private ECInitParams ecInitParams;

    private final String DEV_ACCOUNT = "wh1990xiao2005@hotmail.com";
    private final String APP_ID = "8a48b55152f73add0153076200531d04";
    private final String APP_TOKEN = "c74195eb772a72e561c6da631abb4fac";

    private EditText userNumberEt, targetNumberEt;
    private Button loginBtn, sendTextBtn, sendImageBtn, sendVoiceBtn, sendFileBtn;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findView();
        setListener();
        init();
    }

    //初始化控件
    private void findView() {
        userNumberEt = (EditText) findViewById(R.id.activity_chat_login_number_et);
        targetNumberEt = (EditText) findViewById(R.id.activity_chat_target_number_et);
        loginBtn = (Button) findViewById(R.id.activity_chat_login_login_btn);
        sendTextBtn = (Button) findViewById(R.id.activity_chat_send_text_btn);
        sendVoiceBtn = (Button) findViewById(R.id.activity_chat_send_voice_btn);
        sendImageBtn = (Button) findViewById(R.id.activity_chat_send_image_btn);
        sendFileBtn = (Button) findViewById(R.id.activity_chat_send_file_btn);
    }

    //设置控件监听器
    private void setListener() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ecInitParams.setUserid(userNumberEt.getText().toString());
                showToast("登录参数合法，正在尝试登录。当前身份：" + userNumberEt.getText().toString());
                if (ecInitParams.validate()) {
                    setECDeviceListeners();
                    ECDevice.login(ecInitParams);
                }
            }
        });
        sendTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
        sendVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVoice();
            }
        });
        sendImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });
        sendFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFile();
            }
        });
    }

    private void init() {
        context = this;
        initSDK();
    }

    //初始化SDK
    private void initSDK() {
        if (!ECDevice.isInitialized()) {
            //如果未经初始化，则初始化它
            ECDevice.initial(this, new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    showToast("初始化SDK成功，输入手机号即可登录");
                }

                @Override
                public void onError(Exception e) {
                    showToast("初始化SDK有误，这应该是程序的问题");
                }
            });
        }
        initParams();
    }

    //初始化登陆参数
    private void initParams() {
        ecInitParams = ECInitParams.createParams();
        ecInitParams.setUserid(DEV_ACCOUNT);
        ecInitParams.setAppKey(APP_ID);
        ecInitParams.setToken(APP_TOKEN);
        ecInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        ecInitParams.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
        ecInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
    }

    //设置监听器
    private void setECDeviceListeners() {
        //连接状态监听器
        ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
            @Override
            public void onConnect() {
                showToast("正在登录……");
            }

            @Override
            public void onDisconnect(ECError ecError) {

            }

            @Override
            public void onConnectState(ECDevice.ECConnectState ecConnectState, ECError ecError) {
                if (ecConnectState == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                    showToast("登录成功");
                }
                if (ecConnectState == ECDevice.ECConnectState.CONNECT_FAILED) {
                    showToast("登录失败");
                }
                if (ecConnectState == ECDevice.ECConnectState.CONNECTING) {
                    showToast("正在登录……");
                }
            }
        });
        //消息监听器
        ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {
            @Override
            public void OnReceivedMessage(ECMessage ecMessage) {
                if (ecMessage.getType() == ECMessage.Type.TXT) {
                    showToast("接收到文字消息：" + ecMessage.getBody());
                }
                if (ecMessage.getType() == ECMessage.Type.FILE) {
                    showToast("接收到文件消息：" + ((ECFileMessageBody) ecMessage.getBody()).getRemoteUrl());
                }
                if (ecMessage.getType() == ECMessage.Type.IMAGE) {
                    showToast("接收到图片消息：" + ((ECFileMessageBody) ecMessage.getBody()).getRemoteUrl());
                }
                if (ecMessage.getType() == ECMessage.Type.VOICE) {
                    showToast("接收到语音消息：" + ((ECFileMessageBody) ecMessage.getBody()).getRemoteUrl());
                }
            }

            @Override
            public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {

            }

            @Override
            public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {

            }

            @Override
            public void onOfflineMessageCount(int i) {

            }

            @Override
            public int onGetOfflineMessage() {
                return 0;
            }

            @Override
            public void onReceiveOfflineMessage(List<ECMessage> list) {

            }

            @Override
            public void onReceiveOfflineMessageCompletion() {

            }

            @Override
            public void onServicePersonVersion(int i) {

            }

            @Override
            public void onReceiveDeskMessage(ECMessage ecMessage) {

            }

            @Override
            public void onSoftVersion(String s, int i) {

            }
        });
    }

    //发送文字示例
    private void sendText() {
        ECMessage textMsg = ECMessage.createECMessage(ECMessage.Type.TXT);
        textMsg.setTo(targetNumberEt.getText().toString());
        ECMessageBody textMsgBody = new ECTextMessageBody("This is an example");
        textMsg.setBody(textMsgBody);
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        ecChatManager.sendMessage(textMsg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError ecError, ECMessage ecMessage) {
                showToast("发送文字结束 " + ecError.errorMsg);
            }

            @Override
            public void onProgress(String s, int i, int i1) {

            }
        });
    }

    //发送语音示例
    private void sendVoice() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "music.mp3");
        ECMessage fileMsg = ECMessage.createECMessage(ECMessage.Type.VOICE);
        fileMsg.setTo(targetNumberEt.getText().toString());
        ECFileMessageBody ecFileMessageBody = new ECFileMessageBody();
        ecFileMessageBody.setLocalUrl(Environment.getExternalStorageDirectory() + File.separator + "music.mp3");
        ecFileMessageBody.setFileName("music.mp3");
        ecFileMessageBody.setFileExt("mp3");
        ecFileMessageBody.setLength(file.length());
        ecFileMessageBody.setIsCompress(false);
        showToast("The file length is " + file.length());
        fileMsg.setBody(ecFileMessageBody);
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        ecChatManager.sendMessage(fileMsg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError ecError, ECMessage ecMessage) {
                showToast("发送语音结束 " + ecError.errorMsg);
            }

            @Override
            public void onProgress(String s, int totalByte, int progressByte) {
                showToast("发送语音进行中 （" + progressByte + "/" + totalByte + "）");
            }
        });
    }

    //发送图片示例
    private void sendImage() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        ECMessage fileMsg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
        fileMsg.setTo(targetNumberEt.getText().toString());
        ECFileMessageBody ecFileMessageBody = new ECFileMessageBody();
        ecFileMessageBody.setLocalUrl(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        ecFileMessageBody.setFileName("image.jpg");
        ecFileMessageBody.setFileExt("jpg");
        ecFileMessageBody.setLength(file.length());
        ecFileMessageBody.setIsCompress(false);
        showToast("The file length is " + file.length());
        fileMsg.setBody(ecFileMessageBody);
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        ecChatManager.sendMessage(fileMsg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError ecError, ECMessage ecMessage) {
                showToast("发送图片结束 " + ecError.errorMsg);
            }

            @Override
            public void onProgress(String s, int totalByte, int progressByte) {
                showToast("发送图片进行中 （" + progressByte + "/" + totalByte + "）");
            }
        });
    }

    //发送文件示例
    private void sendFile() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "file.doc");
        ECMessage fileMsg = ECMessage.createECMessage(ECMessage.Type.FILE);
        fileMsg.setTo(targetNumberEt.getText().toString());
        ECFileMessageBody ecFileMessageBody = new ECFileMessageBody();
        ecFileMessageBody.setLocalUrl(Environment.getExternalStorageDirectory() + File.separator + "file.doc");
        ecFileMessageBody.setFileName("file.doc");
        ecFileMessageBody.setFileExt("doc");
        ecFileMessageBody.setLength(file.length());
        ecFileMessageBody.setIsCompress(false);
        showToast("The file length is " + file.length());
        fileMsg.setBody(ecFileMessageBody);
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        ecChatManager.sendMessage(fileMsg, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError ecError, ECMessage ecMessage) {
                showToast("发送文件结束 " + ecError.errorMsg);
            }

            @Override
            public void onProgress(String s, int totalByte, int progressByte) {
                showToast("发送文件进行中 （" + progressByte + "/" + totalByte + "）");
            }
        });
    }

    private void showToast(String toastStr) {
        if (toast == null) {
            toast = new Toast(context);
        }
        toast.cancel();
        toast = Toast.makeText(context, toastStr, Toast.LENGTH_SHORT);
        toast.show();
    }

}
