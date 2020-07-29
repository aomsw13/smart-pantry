package helpers

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smartpantry.MainActivity
import com.example.smartpantry.MainActivityGiver
import com.example.smartpantry.PhoneActivity
import com.google.firebase.database.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MqttClient(mainActivity: MainActivity) : AppCompatActivity() {

    private lateinit var mqttClient: MqttAndroidClient
    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }
    var subscriptionTopic: String? = null
    var publishTopic: String? = null
    var publishTextMessage: String? = null
    var receiveMessage: String? = null
    var idUserTaker = mainActivity.id

    //firebase
//    private lateinit var myRef1: DatabaseReference // = FirebaseDatabase.getInstance().getReference()  //point to the root named "penquiz3d349"

    private lateinit var phoneActivity: PhoneActivity



    fun connect(context: Context) {
        val serverURI = "tcp://broker.emqx.io:1883"
        mqttClient = MqttAndroidClient(context.applicationContext, serverURI, "kotlin_client")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {

                if(message.toString() == "closed"){

                    Log.d("MqttClient", "test id: $idUserTaker")
                    Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")

                    val ref = FirebaseDatabase.getInstance().reference.child("Unique sender id").child(idUserTaker.toString())
                    val updates: MutableMap<String, Any> = HashMap()
                    //updates["success"] = "off after"
                    //ref.updateChildren(updates)
                    unsubscribe(subscriptionTopic.toString())

                }

            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }
        })
        val options = MqttConnectOptions()
        options.isCleanSession = true
        options.keepAliveInterval = 120  //This value, measured in seconds, defines the maximum time interval between messages sent or received
        options.isAutomaticReconnect = true
        options.serverURIs = arrayOf("tcp://postman.cloudmqtt.com:14107")
        options.userName = "ptnqociv"
        options.setPassword("Bc-dN1Ef9jo-".toCharArray())

        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                    publishMessage()
                    Handler().postDelayed({
                        Log.d("MqttClient", "current enter handle postDelay")
                        subscribeTopic()
                    }, 5000)

                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }


    fun publishMessage( qos: Int = 1, retained: Boolean = false) {

        try {
            val message = MqttMessage()
            message.payload = publishTextMessage!!.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(publishTopic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$publishTextMessage published to ${publishTopic}")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $publishTextMessage to $publishTopic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    fun subscribeTopic() {
//        try {
//            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    Log.d(TAG, "Subscribed to $topic")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    Log.d(TAG, "Failed to subscribe $topic")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }

        mqttClient.subscribe(subscriptionTopic, 0).actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.d(TAG, "Subscribed to $subscriptionTopic")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.d(TAG, "Failed to subscribe to $subscriptionTopic")
                exception.printStackTrace()
            }
        }
    }

    fun close() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }
}