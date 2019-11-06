package com.kgh.mqttpublish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    String TAG = "MQTT";
    MqttAndroidClient client;

    static String MQTT_HOST = "broker.mqttdashboard.com:8000";
    static String USERNAME = "shellios";
    static String PASSWORD = "gvc";
    String TOPIC_STRING = "testtopic/mqt";
    String MESSAGE = "HelloFromAndroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String clientId = MqttClient.generateClientId();
//        String clientId = "clientId-lPTuWbeivJ";
        client = new MqttAndroidClient(this.getApplicationContext(), MQTT_HOST,
                        clientId);
        /*client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);
        */

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    // We are connected
                    Log.d(TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Failed to Connect !", Toast.LENGTH_SHORT).show();
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void connect(View v){

    }

    public void pub(View v){

        Toast.makeText(this, "Published", Toast.LENGTH_SHORT).show();

        String topic = TOPIC_STRING;
        String payload = MESSAGE;
        try {
            client.publish(topic, payload.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
