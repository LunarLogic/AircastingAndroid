package io.lunarlogic.aircasting.screens.new_session

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.lunarlogic.aircasting.bluetooth.BluetoothActivity
import io.lunarlogic.aircasting.bluetooth.BluetoothDeviceFoundReceiver
import io.lunarlogic.aircasting.lib.ResultCodes
import io.lunarlogic.aircasting.permissions.PermissionsManager

class NewSessionActivity : AppCompatActivity(), BluetoothActivity {
    private var mNewSessionController: NewSessionController? = null

    companion object {
        fun start(context: Context?) {
            context?.let{
                val intent = Intent(it, NewSessionActivity::class.java)
                it.startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val newSessionView = NewSessionViewMvcImpl(layoutInflater, null)
        mNewSessionController = NewSessionController(this, this, newSessionView, supportFragmentManager)

        setContentView(newSessionView.rootView)
        mNewSessionController!!.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNewSessionController!!.onDestroy()
    }

    override fun requestBluetoothPermissions(permissionsManager: PermissionsManager) {
        permissionsManager.requestBluetoothPermissions(this)
    }

    override fun bluetoothPermissionsGranted(permissionsManager: PermissionsManager): Boolean {
        return permissionsManager.bluetoothPermissionsGranted(this)
    }

    override fun requestBluetoothEnable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, ResultCodes.AIRCASTING_REQUEST_BLUETOOTH_ENABLE)
    }

    override fun registerBluetoothDeviceFoundReceiver(receiver: BluetoothDeviceFoundReceiver) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    override fun unregisterBluetoothDeviceFoundReceiver(receiver: BluetoothDeviceFoundReceiver) {
        unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()

        mNewSessionController!!.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mNewSessionController!!.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        mNewSessionController!!.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mNewSessionController!!.onActivityResult(requestCode, resultCode, data)
    }
}
