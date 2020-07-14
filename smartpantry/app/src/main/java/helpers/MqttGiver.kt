package helpers

import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.smartpantry.Adapter.MyAdapter
import com.example.smartpantry.PhoneActivity
import com.example.smartpantry.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


class MqttGiver(userType: UserType) : AppCompatActivity() {

    private lateinit var mqttClient: MqttAndroidClient
    // TAG
    companion object {
        const val TAG = "AndroidMqttClient"
    }
    var subscriptionTopicPantryId: String? = null
    var subscriptionTopicPantryStatus: String? = null
    var publishTopic: String? = null
    var publishTextMessage: String? = null
    var receiveMessage: String? = null
    private lateinit var auth: FirebaseAuth


    //send pantry id and status to PantryActivity.kt
    var receiveTopicPantryId: String? = null
    var receiveTopicPantryStatus: String? = null

    var numeric = true


    //firebase
    private lateinit var myRef: DatabaseReference // = FirebaseDatabase.getInstance().getReference()  //point to the root named "penquiz3d349"



    private lateinit var phoneActivity: PhoneActivity



    fun connect(context: Context) {
        val serverURI = "tcp://broker.emqx.io:1883"
        mqttClient = MqttAndroidClient(context.applicationContext, serverURI, "kotlin_client")
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {

                Log.d(TAG, "Receive first message: ${message.toString()} from topic: $topic")
                numeric = message.toString().matches("-?\\d+(\\.\\d+)?".toRegex())
                //string is numerical
                if(numeric){
                    Log.d(TAG, "Receive numerical message: ${message.toString()} from topic: $topic")
                    receiveTopicPantryId = message.toString()
                    Log.d(TAG, "Receive numerical message and ready to send: ${receiveTopicPantryId} from topic: $topic")
                  //  unsubscribe(subscriptionTopicPantryId.toString())

                }
                //string is not numerical
                else{
                    Log.d(TAG, "Receive string message: ${message.toString()} from topic: $topic")
                    receiveTopicPantryStatus = message.toString()
                    Log.d(TAG, "Receive string message and ready to send: ${receiveTopicPantryStatus} from topic: $topic")
                   // unsubscribe(subscriptionTopicPantryStatus.toString())
                }

                if(receiveTopicPantryStatus == "empty"){
                    setValueToFirebase(receiveTopicPantryId.toString(), receiveTopicPantryStatus.toString())
                }
               else if(receiveTopicPantryStatus == "full"){
                    removeValue(receiveTopicPantryId)
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
                   // publishMessage()
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

    private fun removeValue(receiveTopicPantryId: String?) {

        myRef = FirebaseDatabase.getInstance().getReference("Empty Pantry").child("pantryId")
        myRef.child(receiveTopicPantryId.toString()).removeValue()
        Log.d(TAG, "already remove value $receiveTopicPantryId")

//        val oldItems: Query = myRef.child("pantryId").orderByChild(receiveTopicPantryId.toString())
//        Log.d(TAG, "ready to remove value $receiveTopicPantryId")
//        oldItems.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                throw error.toException();
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.d(TAG, "remove value $receiveTopicPantryId")
//                for (itemSnapshot in snapshot.children) {
//                    Log.d(TAG, "already remove value $receiveTopicPantryId")
//                    itemSnapshot.ref.removeValue()
//                }
//            }
//
//        })


//        myRef = FirebaseDatabase.getInstance().getReference("Empty Pantry").child("pantryId").child(receiveTopicPantryId.toString())
//        myRef.child(receiveTopicPantryId.toString()).removeValue();
    }

    private fun setValueToFirebase(receiveTopicPantryId: String?, receiveTopicPantryStatus: String?) {

        // Log.d("FETCHING", "receiveValue $receiveTopicPantryId")

        myRef = FirebaseDatabase.getInstance().getReference("Empty Pantry").child("pantryId").child(receiveTopicPantryId.toString())
        myRef.child("pantryID").setValue(receiveTopicPantryId)
        myRef.child("status").setValue(receiveTopicPantryStatus)


        Log.d("FETCHING", "value $receiveTopicPantryId $receiveTopicPantryStatus")
        Log.d("FETCHING", "setValueToFirebase")

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

        Log.d("subscribeTopic", "Subscribing")
        mqttClient.subscribe(subscriptionTopicPantryId, 0).actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.d(TAG, "first Subscribed to $subscriptionTopicPantryId")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.d(TAG, "Failed to subscribe to $subscriptionTopicPantryId")
                exception.printStackTrace()
            }
        }

        mqttClient.subscribe(subscriptionTopicPantryStatus, 0).actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                Log.d(TAG, "second Subscribed to $subscriptionTopicPantryStatus")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                Log.d(TAG, "Failed to subscribe to $subscriptionTopicPantryStatus")
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