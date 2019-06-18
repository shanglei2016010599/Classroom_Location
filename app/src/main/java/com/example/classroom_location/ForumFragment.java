package com.example.classroom_location;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Response;

public class ForumFragment extends Fragment {

    private List<Msg> msgList = new ArrayList<>();

    private EditText inputText;

    private Button send;

    private RecyclerView msgRecyclerView;

    private MsgAdapter adapter;

    private static String Name = "";

    private final static String TAG = "ForumFragment";

    private View view;

    private final static int GET_MESSAGE_OK = 1;

    private final static int SEND_MESSAGE_OK = 2;

    private String responseData;

    private SwipeRefreshLayout swipeRefresh;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_MESSAGE_OK:
                    parseJSONWithGSON(responseData);
                    adapter = new MsgAdapter(msgList);
                    msgRecyclerView.setAdapter(adapter);
                    break;
                case SEND_MESSAGE_OK:
                    Log.d(TAG, "handleMessage: 发送成功");
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint("HandlerLeak")
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forum_fragment, container, false);

        Name = MainActivity.getUserName();

        initMsgs(); //初始化消息数据

        inputText = view.findViewById(R.id.input_text);
        send = view.findViewById(R.id.send);
        msgRecyclerView = view.findViewById(R.id.msg_recycler_view);
        swipeRefresh = view.findViewById(R.id.msg_swipe_refresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        msgRecyclerView.setLayoutManager(linearLayoutManager);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMessages();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = inputText.getText().toString();
                if ( !"".equals(message) ){
                    Msg msg = new Msg(Name, message, Msg.TYPE_SENT, String.valueOf(System.currentTimeMillis()));
                    msgList.add(msg);
                    /* 当有新消息时，刷新ListView中的显示 */
                    adapter.notifyItemInserted(msgList.size() - 1);
                    /* 将ListView定位到最后一行 */
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");  // 清空输入框的内容
                    HttpUtil.sendOkHttpRequestByPost(URL.url + "MessageServlet", "name", Name,
                            "message", message, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: error: " + e.toString());
                            Looper.prepare();
                            Toast.makeText(view.getContext(), "访问网络失败，请重试",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            assert response.body() != null;
                            responseData = response.body().string();
                            Log.d(TAG, "onResponse: " + responseData);
                            Message message = new Message();
                            message.what = SEND_MESSAGE_OK;
                            handler.sendMessage(message);      // 发送消息
                        }
                    });
                }
            }
        });
        return view;
    }

    private void initMsgs(){

        HttpUtil.sendOkHttpRequest(URL.url + "MessageServlet", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: error: " + e.toString());
                Looper.prepare();
                Toast.makeText(view.getContext(), "访问网络失败，请重试",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                responseData = response.body().string();
                Log.d(TAG, "onResponse: " + responseData);
                Message message = new Message();
                message.what = GET_MESSAGE_OK;
                handler.sendMessage(message);      // 发送消息
            }
        });
    }

    private void parseJSONWithGSON(String jsonData){
        Gson gson = new Gson();
        List<Msg> Messages = gson.fromJson(jsonData, new TypeToken<List<Msg>>(){}.getType());
        Log.d(TAG, "parseJSONWithGSON: " + msgList);
        msgList.clear();
        for (Msg msg : Messages){
            Log.d(TAG, "parseJSONWithGSON: " + msg.getName());
            Log.d(TAG, "parseJSONWithGSON: " + msg.getMessage());
            if (msg.getName().equals(Name)){
                msg.setType(Msg.TYPE_SENT);
            } else {
                msg.setType(Msg.TYPE_RECEIVED);
            }
            msgList.add(msg);
        }
        Log.d(TAG, "parseJSONWithGSON: " + msgList);
    }

    private void refreshMessages(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /* 需要通过获取活动，才能成功使用runOnUiThread()方法 */
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        initMsgs();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
