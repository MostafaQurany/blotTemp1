package com.example.untitled4
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.*


class MainActivity: FlutterActivity() {

    @RequiresApi(VERSION_CODES.M)
    val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)

    @RequiresApi(VERSION_CODES.M)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
    private val CHANNEL = "samples.flutter.dev/battery"
    lateinit var bluetoothScanner: BluetoothScanner
    var deviceList = mutableListOf<BluetoothDevice>()
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            /* if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()

                if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }
            } else {
                result.notImplemented()
            }*/
            bluetoothScanner = BluetoothScanner(this)
            if (call.method == "getDivceScane") {
                deviceList = bluetoothScanner.getDeviceList()
                result.success(deviceList)
            } else if (call.method == "startScan") {
                bluetoothScanner.startScan()
            } else if (call.method == "stopScan") {
                bluetoothScanner.stopScan()
            }           // This method is invoked on the main thread.
            // TODO
        }
    }


    private fun getBatteryLevel(): Int {
        val batteryLevel: Int
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            batteryLevel =
                intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1
                )
        }

        return batteryLevel
    }


}
class BluetoothScanner(private val context: Context ,) {
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val deviceList = mutableListOf<BluetoothDevice>()
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null) {
                        deviceList.add(device)
                    }
                }
            }
        }
    }

    fun startScan() {
        if (bluetoothAdapter.isEnabled) {
            val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
            context.registerReceiver(broadcastReceiver, filter)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            bluetoothAdapter.startDiscovery()
        } else {
            // Handle Bluetooth not being enabled
        }
    }

    fun stopScan() {
        context.unregisterReceiver(broadcastReceiver)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        bluetoothAdapter.cancelDiscovery()
    }

    fun getDeviceList(): MutableList<BluetoothDevice> {
        return Collections.unmodifiableList(deviceList)
    }
}
