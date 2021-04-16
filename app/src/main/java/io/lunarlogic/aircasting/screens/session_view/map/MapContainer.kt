package io.lunarlogic.aircasting.screens.session_view.map

import android.content.Context
import android.location.Location
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.*
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.AnimatedLoader
import io.lunarlogic.aircasting.lib.BitmapHelper
import io.lunarlogic.aircasting.lib.MeasurementColor
import io.lunarlogic.aircasting.lib.SessionBoundingBox
import io.lunarlogic.aircasting.screens.dashboard.SessionPresenter
import io.lunarlogic.aircasting.models.Measurement
import io.lunarlogic.aircasting.models.MeasurementStream
import io.lunarlogic.aircasting.models.Session
import io.lunarlogic.aircasting.screens.session_view.SessionDetailsViewMvc
import kotlinx.android.synthetic.main.activity_map.view.*
import java.lang.Math.*
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.asinh

class MapContainer: OnMapReadyCallback {
    private val DEFAULT_ZOOM = 16f

    private val mContext: Context
    private var mListener: SessionDetailsViewMvc.Listener? = null

    private var mMap: GoogleMap? = null
    private val mLocateButton: ImageView?
    private val mMapFragment: SupportMapFragment?

    private var mSessionPresenter: SessionPresenter? = null
    private var mMeasurements: List<Measurement> = emptyList()

    private var mMeasurementsLineOptions: PolylineOptions = defaultPolylineOptions()
    private var mMeasurementsLine: Polyline? = null
    private val mMeasurementPoints = ArrayList<LatLng>()
//    private val mMeasurementSpans = ArrayList<StyleSpan>()
    private var mLastMeasurementMarker: Marker? = null

    private var mTileOverlay: TileOverlay? = null
    private var mOldTileOverlay: TileOverlay? = null
    private var mTileColorSwitch: Boolean = true

    private val status = AtomicInteger(Status.INIT.value)

    enum class Status(val value: Int) {
        INIT(0),
        MAP_LOADED(1),
        SESSION_LOADED(2)
    }

    constructor(rootView: View?, context: Context, supportFragmentManager: FragmentManager?) {
        mContext = context

        mMapFragment = supportFragmentManager?.findFragmentById(R.id.map) as? SupportMapFragment
        mMapFragment?.getMapAsync(this)
        mMapFragment?.view?.visibility = View.GONE

        mLocateButton = rootView?.locate_button
        mLocateButton?.setOnClickListener {
            locate()
        }
        mLocateButton?.visibility = View.GONE
    }

    fun registerListener(listener: SessionDetailsViewMvc.Listener) {
        mListener = listener
    }

    fun unregisterListener() {
        mListener = null
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        mMap = googleMap

        // sometimes onMapReady is invoked earlier than bindStream
        if (status.get() == Status.SESSION_LOADED.value) {
            setup()
        }
        status.set(Status.MAP_LOADED.value)
    }

    fun setup() {
        clearMap()

        mMap?.isBuildingsEnabled = false

        drawSession()

        animateCameraToSession()
        if (mMeasurements.isNotEmpty()) showMap()
    }

    fun bindSession(sessionPresenter: SessionPresenter?) {
        mSessionPresenter = sessionPresenter
        mMeasurements = measurementsWithLocations(mSessionPresenter?.selectedStream)

        if (mSessionPresenter?.isFixed() == true) {
            drawFixedMeasurement()
        }
        // sometimes onMapReady is invoked earlier than bindStream
        if (status.get() == Status.MAP_LOADED.value) {
            setup()
        }
        if (mMeasurements.isNotEmpty()) status.set(Status.SESSION_LOADED.value)
    }

    private fun measurementsWithLocations(stream: MeasurementStream?): List<Measurement> {
        val measurements = stream?.measurements?.filter { it.latitude !== null && it.longitude != null }
        return measurements ?: emptyList()
    }

    private fun drawSession() {
        if (mMap == null) return
        if (mMeasurements.isEmpty()) return

        var latestPoint: LatLng? = null
        var latestColor: Int? = null

        var i = 0
        for (measurement in mMeasurements) {
            latestColor = MeasurementColor.forMap(mContext, measurement, mSessionPresenter?.selectedSensorThreshold())

            if (i > 0) {
//                mMeasurementSpans.add(StyleSpan(latestColor))
            }
            latestPoint = LatLng(measurement.latitude!!, measurement.longitude!!)
            mMeasurementPoints.add(latestPoint)
            i += 1
        }
        mMeasurementsLineOptions.addAll(mMeasurementPoints)//.addAllSpans(mMeasurementSpans)
        mMeasurementsLine = mMap?.addPolyline(mMeasurementsLineOptions)

        if (latestPoint != null && latestColor != null) {
            drawLastMeasurementMarker(latestPoint, latestColor)
        }

        if (mTileOverlay != null) {
            mTileOverlay?.remove()
        }
        mTileOverlay = mMap?.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(getTileProvider()).visible(true)
        )
    }

    private fun drawLastMeasurementMarker(point: LatLng?, color: Int?) {
        if (point == null || color == null) return
        if (mLastMeasurementMarker != null) mLastMeasurementMarker!!.remove()

        val icon = BitmapHelper.bitmapFromVector(mContext, R.drawable.ic_dot_20, color)
        mLastMeasurementMarker = mMap?.addMarker(
            MarkerOptions()
                .position(point)
                .icon(icon)
        )
    }

    private fun animateCameraToSession() {
        if (mSessionPresenter?.isFixed() == true) {
            animateCameraToFixedSession()
        } else {
            animateCameraToMobileSession()
        }
    }

    private fun animateCameraToMobileSession() {
        if (mMeasurements.isEmpty()) return

        val boundingBox = SessionBoundingBox.get(mMeasurements)
        val padding = 100 // meters
        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(boundingBox, padding))
    }

    private fun animateCameraToFixedSession() {
        val session = mSessionPresenter?.session
        val location = session?.location

        location ?: return

        centerMap(location)
    }

    fun centerMap(location: Location) {
        val position = LatLng(location.latitude, location.longitude)
        centerMap(position)
    }

    private fun centerMap(location: Session.Location) {
        val position = LatLng(location.latitude, location.longitude)
        centerMap(position)
    }

    private fun centerMap(position: LatLng) {
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
    }

    fun addMobileMeasurement(measurement: Measurement) {
        if (mSessionPresenter?.isRecording() == true) {
            drawMobileMeasurement(measurementColorPoint(measurement))
        }
    }

    private fun drawFixedMeasurement() {
        val colorPoint = measurementColorPoint(mMeasurements.last())
        drawLastMeasurementMarker(colorPoint?.point, colorPoint?.color)
    }

    private fun drawMobileMeasurement(colorPoint: ColorPoint?) {
        if (colorPoint == null) return

        mMeasurementPoints.add(colorPoint.point)
//        mMeasurementSpans.add(StyleSpan(colorPoint.color))

        if (mMeasurementsLine == null) {
            mMeasurementsLine = mMap?.addPolyline(mMeasurementsLineOptions)
        }

        mMeasurementsLine?.setPoints(mMeasurementPoints)
//        mMeasurementsLine?.setSpans(mMeasurementSpans)
        println("MARYSIA: Adding invisible overlay")

        // Adding "invisible" overlay
        val newTileOverlay =  mMap?.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(getTileProvider()).transparency(1f).visible(true)
        )

        mTileOverlay?.transparency = 0.5f // making previous overlay visible
        mOldTileOverlay?.remove() // removing previously displayed visible overlay
        mOldTileOverlay = mTileOverlay
        mTileOverlay = newTileOverlay

        drawLastMeasurementMarker(colorPoint.point, colorPoint.color)
    }

    private fun measurementColorPoint(measurement: Measurement) : ColorPoint? {
        if (measurement.latitude == null || measurement.longitude == null) return null

        val point = LatLng(measurement.latitude, measurement.longitude)
        val color = MeasurementColor.forMap(mContext, measurement, mSessionPresenter?.selectedSensorThreshold())

        return ColorPoint(point, color)
    }

    fun refresh(sessionPresenter: SessionPresenter?) {
        clearMap()
        bindSession(sessionPresenter)
        drawSession()
    }

    private fun locate() {
        mListener?.locateRequested()
    }

    private fun clearMap() {
        mMap?.clear()
        mMeasurementPoints.clear()
//        mMeasurementSpans.clear()
        mMeasurementsLine = null
        mMeasurementsLineOptions = defaultPolylineOptions()
    }

    private fun defaultPolylineOptions(): PolylineOptions {
        return PolylineOptions()
            .width(20f)
            .jointType(JointType.ROUND)
            .endCap(RoundCap())
            .startCap(RoundCap())
    }

    private fun showMap() {
        mMapFragment?.view?.visibility = View.VISIBLE
        mLocateButton?.visibility = View.VISIBLE
    }

    //TILES
    private fun getTileProvider(): TileProvider {
        var tileProvider: TileProvider = object : UrlTileProvider(128, 128) {

            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                println("MARYSIA: tile ${x}x${y}")
                /* Define the URL pattern for the tile images */
                var url = ""
                if (mTileColorSwitch) {
                    mTileColorSwitch = false
                      url = "https://www.linkpicture.com/q/square-xl.png"

                } else {
                    mTileColorSwitch = true
                    url = "https://www.linkpicture.com/q/green_1.png"
                }

                return if (!checkTileExists(x, y, zoom)) {
                    null
                } else try {
                    URL(url)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
            }

            /*
         * Check that the tile server supports the requested x, y and zoom.
         * Complete this stub according to the tile range you support.
         * If you support a limited range of tiles at different zoom levels, then you
         * need to define the supported x, y range at each zoom level.
         */
            private fun checkTileExists(x: Int, y: Int, zoom: Int): Boolean {
                println("MARYSIA: zoom: ${zoom}")
                val minZoom = 1
                val maxZoom = 20
                return zoom in minZoom..maxZoom
            }


        }
        return tileProvider
    }

    private fun getXYTile(lat : Double, lon: Double, zoom : Int) : Pair<Int, Int> {
        val latRad = Math.toRadians(lat)
        var xtile = floor( (lon + 180) / 360 * (1 shl zoom) ).toInt()
        var ytile = floor( (1.0 - asinh(tan(latRad)) / PI) / 2 * (1 shl zoom) ).toInt()

        if (xtile < 0) {
            xtile = 0
        }
        if (xtile >= (1 shl zoom)) {
            xtile= (1 shl zoom) - 1
        }
        if (ytile < 0) {
            ytile = 0
        }
        if (ytile >= (1 shl zoom)) {
            ytile = (1 shl zoom) - 1
        }

        return Pair(xtile, ytile)
    }
}

data class ColorPoint(val point: LatLng, val color: Int)
