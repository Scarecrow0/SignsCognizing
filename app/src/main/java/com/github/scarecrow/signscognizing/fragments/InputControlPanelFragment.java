package com.github.scarecrow.signscognizing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.scarecrow.signscognizing.R;
import com.github.scarecrow.signscognizing.Utilities.ArmbandManager;
import com.github.scarecrow.signscognizing.Utilities.MessageManager;
import com.github.scarecrow.signscognizing.Utilities.SocketConnectionManager;
import com.github.scarecrow.signscognizing.activities.MainActivity;
import com.github.scarecrow.signscognizing.adapters.VoiceRecordButton;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Scarecrow on 2018/2/
 *
 */

public class InputControlPanelFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_control_panel, container,
                false);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        //返回button
        final Button bt = view.findViewById(R.id.button_input_panel_back);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketConnectionManager.getInstance()
                        .disconnect();
                MainActivity activity = (MainActivity) getActivity();
                activity.switchFragment(MainActivity.FRAGMENT_ARMBANDS_SELECT);
                activity.switchFragment(MainActivity.FRAGMENT_INFO_DISPLAY);
            }
        });


        //手语输入
        final Button bt_cap = view.findViewById(R.id.button_input_panel_sign_start);
        bt_cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean capture_state = MessageManager.getInstance()
                        .isCapturingSign();
                if (!capture_state) {
                    MessageManager.getInstance()
                            .requestCaptureSign();
                    bt_cap.setText("结束手语采集");

                } else {
                    SocketConnectionManager.getInstance()
                            .sendMessage(buildStopCaptureRequest());
                    bt_cap.setText("手语采集");
                }
            }
        });
        MessageManager.getInstance()
                .addNewNoticeTarget(new MessageManager.NoticeMessageChanged() {
                    @Override
                    public void onNewMessageAdd() {
                    }

                    @Override
                    public void onMessageContentChange() {
                    }

                    @Override
                    public void onSignCaptureEnd() {
                        bt_cap.setText("开始手语采集");
                    }

                    @Override
                    public void onSignCaptureStart() {
                        bt_cap.setText("结束手语采集");
                    }
                });



        //语音输入
    }

    private String buildStopCaptureRequest() {
        JSONObject request_body = new JSONObject();
        try {
            request_body.accumulate("control", "stop_recognize");
            request_body.accumulate("data", "");
        } catch (Exception ee) {
            Log.e(TAG, "buildStopCaptureRequest: ", ee);
            ee.printStackTrace();
        }
        return request_body.toString();
    }

    @Override
    public void onStop() {
        View view = getView();
        if (view != null) {
            VoiceRecordButton voiceRecordButton =
                    view.findViewById(R.id.button_input_panel_voice_start);
            voiceRecordButton.releaseMediaResource();
        }
        super.onStop();
    }
}
