package helpers

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MqttClient(applicationContext: Context) {

    private lateinit var mqttClient: MqttAndroidClient
    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }
    var subscriptionTopic: String? = null
    var publishTextMessage: String? = null


    fun connect(context: Context) {
        val serverURI = "tcp://broker.emqx.io:1883"
        mqttClient = MqttAndroidClient(context.applicationContext, serverURI, "kotlin_client")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
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
                    subscribeTopic()
                    publishMessage()
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
            mqttClient.publish(subscriptionTopic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$publishTextMessage published to $subscriptionTopic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $publishTextMessage to $subscriptionTopic")
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
}