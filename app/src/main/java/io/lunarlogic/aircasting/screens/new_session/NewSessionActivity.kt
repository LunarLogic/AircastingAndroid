package io.lunarlogic.aircasting.screens.new_session

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import io.lunarlogic.aircasting.AircastingApplication
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.bluetooth.BluetoothManager
import io.lunarlogic.aircasting.lib.NavigationController
import io.lunarlogic.aircasting.lib.Settings
import io.lunarlogic.aircasting.permissions.PermissionsManager
import io.lunarlogic.aircasting.screens.dashboard.DashboardPagerAdapter
import io.lunarlogic.aircasting.sensor.Session
import io.lunarlogic.aircasting.sensor.SessionBuilder
import io.lunarlogic.aircasting.sensor.airbeam2.AirBeam2Connector
import io.lunarlogic.aircasting.sensor.microphone.AudioReader
import javax.inject.Inject

class NewSessionActivity : AppCompatActivity() {

    private lateinit var controller: NewSessionController

    @Inject
    lateinit var permissionsManager: PermissionsManager

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var bluetoothManager: BluetoothManager

    @Inject
    lateinit var airbeam2Connector: AirBeam2Connector

    @Inject
    lateinit var audioReader: AudioReader

    @Inject
    lateinit var sessionBuilder: SessionBuilder

    companion object {
        val SESSION_TYPE_KEY = "sessionType"

        fun start(rootActivity: FragmentActivity?, sessionType: Session.Type) {
            rootActivity?.let{
                val startForResult = it.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    val tabId = DashboardPagerAdapter.tabIndexForSessionType(sessionType, Session.Status.RECORDING)
                    NavigationController.goToDashboard(tabId)
                }

                val intent = Intent(it, NewSessionActivity::class.java)
                intent.putExtra(SESSION_TYPE_KEY, sessionType)
                startForResult.launch(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionType = intent.extras?.get(SESSION_TYPE_KEY) as Session.Type

        val app = application as AircastingApplication
        val appComponent = app.appComponent
        appComponent.inject(this)

        val view = NewSessionViewMvcImpl(layoutInflater, null)
        controller = NewSessionController(
            this,
            view,
            supportFragmentManager,
            permissionsManager,
            bluetoothManager,
            airbeam2Connector,
            audioReader,
            sessionBuilder,
            sessionType
        )
        controller.onCreate()

        setContentView(view.rootView)
        setSupportActionBar(findViewById(R.id.new_session_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        controller.onBackPressed()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        controller.onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        controller.onActivityResult(requestCode, resultCode)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            val fragmentManager = supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
