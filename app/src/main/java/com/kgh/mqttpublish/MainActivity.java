package com.kgh.mqttpublish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    String TAG = "MQTT";
    MqttAndroidClient client;

    EditText edTopic;
    TextView statusConnect, startWeight, changeWeight, endWeight;

    static String MQTT_HOST = "tcp://52.66.234.178:1884";
    static String USERNAME = "llmuvmdemo";
    static String PASSWORD = "GB@demo#19";
//    String TOPIC_STRING = "testtopic/mqt";
    String TOPIC_STRING = "GVM/WS/042";
    String MESSAGE = "HelloFromAndroid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusConnect = findViewById(R.id.textView);
        startWeight = findViewById(R.id.valueStart);
        changeWeight = findViewById(R.id.valueChanged);
        endWeight = findViewById(R.id.valueEnd);

        edTopic = findViewById(R.id.editText);

        connect();
    }

    public void connect(){

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTT_HOST,
                clientId);
        /*client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);
        */

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {

            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    statusConnect.setText("Connected");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    Toast.makeText(MainActivity.this, "Failed to Connect !", Toast.LENGTH_SHORT).show();
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    statusConnect.setText("Failed To Connect !");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MainActivity.this, "Connection lost !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                if (msg.contains("*SW")){
                    msg = msg.replace("*SW,","").replace("#","");
//                    msg.replace("*SW,","");
                    startWeight.setText(msg);
                }
                if (msg.contains("*CW")){
                    msg = msg.replace("*CW,","").replace("#","");
                    changeWeight.setText(msg);
                }
                if (msg.contains("*EW")){
                    msg = msg.replace("*EW,","").replace("#","");
                    endWeight.setText(msg);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void pub(View v) {

        String topic = "GVM/WS/" + edTopic.getText().toString();
        String payload = edMessage.getText().toString();

        connect();
        if (statusConnect.getText().toString() == "Connected") {

            if (!TextUtils.isEmpty(topic) && !TextUtils.isEmpty(payload)) {
                try {
                    client.publish(topic, payload.getBytes(), 0, false);
                    Toast.makeText(this, "Published", Toast.LENGTH_SHORT).show();
                } catch (MqttException e) {
                    Toast.makeText(this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else
                Toast.makeText(this, "Please fill out all the fields !", Toast.LENGTH_LONG).show();
            Toast.makeText(this, topic, Toast.LENGTH_LONG).show();
        }
    }

    public void setSubscription(){

        String topic = "GVM/WS/" + edTopic.getText().toString() + "GVC";

        try{
            client.subscribe("GVM/WS/042", 0);
            Log.i("MQTT", "Subscription");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
