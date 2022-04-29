package uz.ilhomjon.bluetooth2

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import uz.ilhomjon.bluetooth2.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var bAdapter:BluetoothAdapter
    lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bAdapter == null){
            binding.bluetoothStatusTv.text = "Bluetooth is not available"
        }else{
            binding.bluetoothStatusTv.text = "Bluetooth is available"
        }

        if(bAdapter.isEnabled){
            binding.bluetoothTv.setImageResource(R.drawable.ic_bluetooth_on)
        }else{
            binding.bluetoothTv.setImageResource(R.drawable.ic_bluetooth_off)
        }

        binding.turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled){
                Toast.makeText(this, "Already on", Toast.LENGTH_SHORT).show()
            }else{
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, 1)
            }
        }
        binding.turnOffBtn.setOnClickListener {
            if (!bAdapter.isEnabled){
                Toast.makeText(this, "Already off", Toast.LENGTH_SHORT).show()
            }else{
                bAdapter.disable()
                binding.bluetoothTv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(this, "Bluetooth turned off", Toast.LENGTH_SHORT).show()
            }
        }
        binding.discoverableBtn.setOnClickListener {
            if (!bAdapter.isDiscovering){
                Toast.makeText(this, "Making your device discoverable", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, 2)
            }
        }

        binding.pairedBtn.setOnClickListener {
            if (bAdapter.isEnabled){
                binding.pairedTv.text = "Paired Devices"

                val devices = bAdapter.bondedDevices
                for (device in devices) {
                    val deviceName = device.name
                    val deviceAddress = device
                    binding.pairedTv.append("\nDevice: $deviceName ,  $deviceAddress")

                }
            }else{
                Toast.makeText(this, "Turn on bluetooth first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                binding.bluetoothTv.setImageResource(R.drawable.ic_bluetooth_on)
                Toast.makeText(this, "Bluetooth is on", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Could not on bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(UUID.randomUUID())
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()
                binding.pairedTv.append("\n Connected")

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
//                manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
}