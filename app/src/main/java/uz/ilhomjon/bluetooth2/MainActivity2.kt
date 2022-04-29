package uz.ilhomjon.bluetooth2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import uz.ilhomjon.bluetooth2.databinding.ActivityMain2Binding
import java.io.IOException
import java.util.*

class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    lateinit var i1:Button
    lateinit var t1:TextView

    var address: String? = null
    var name:kotlin.String? = null

    var myBluetooth: BluetoothAdapter? = null
    var btSocket: BluetoothSocket? = null
    var pairedDevices: Set<BluetoothDevice>? = null
    val myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            setw()
        } catch (e: Exception) {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Throws(IOException::class)
    private fun setw() {
        t1 = findViewById<View>(R.id.textView1) as TextView
        bluetooth_connect_device()
        i1 = findViewById<View>(R.id.button1) as Button
        i1.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                led_on_off("1")
            }
            if (event.action == MotionEvent.ACTION_UP) {
                led_on_off("0")
            }
            true
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun bluetooth_connect_device() {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter()
            address = myBluetooth?.getAddress()
            pairedDevices = myBluetooth?.getBondedDevices()
            if (pairedDevices?.size!! > 0) {
                for (bt in pairedDevices!!) {
                    address = bt.address.toString()
                    name = bt.name.toString()
                    Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (we: java.lang.Exception) {
        }
        myBluetooth = BluetoothAdapter.getDefaultAdapter() //get the mobile bluetooth device
        val dispositivo =
            myBluetooth!!.getRemoteDevice(address) //connects to the device's address and checks if it's available
        btSocket =
            dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID) //create a RFCOMM (SPP) connection
        btSocket!!.connect()
        try {
            t1.text = "BT Name: $name\nBT Address: $address"
        } catch (e: java.lang.Exception) {
        }
    }

    fun onClick(v: View?) {
        try {
        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun led_on_off(i: String) {
        try {
            if (btSocket != null) {
                btSocket!!.outputStream.write(i.toByteArray())
            }
        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }


}