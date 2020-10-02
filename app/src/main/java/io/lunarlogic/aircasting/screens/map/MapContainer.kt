package io.lunarlogic.aircasting.screens.map

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
import io.lunarlogic.aircasting.lib.BitmapHelper
import io.lunarlogic.aircasting.lib.MeasurementColor
import io.lunarlogic.aircasting.lib.SessionBoundingBox
import io.lunarlogic.aircasting.location.LocationHelper
import io.lunarlogic.aircasting.sensor.Measurement
import io.lunarlogic.aircasting.sensor.MeasurementStream
import kotlinx.android.synthetic.main.activity_map.view.*
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

class MapContainer: OnMapReadyCallback {
    private val DEFAULT_ZOOM = 16f

    private val mContext: Context
    private var mListener: MapViewMvc.Listener? = null

    private var mMap: GoogleMap? = null
    private val mLocateButton: ImageView?

    private var mSelectedStream: MeasurementStream? = null
    private var mMeasurements: List<Measurement> = emptyList()

    private val mMeasurementsLineOptions = PolylineOptions()
        .width(20f)
        .jointType(JointType.ROUND)
        .endCap(RoundCap())
        .startCap(RoundCap())
    private var mMeasurementsLine: Polyline? = null
    private val mMeasurementPoints = ArrayList<LatLng>()
    private val mMeasurementSpans = ArrayList<StyleSpan>()
    private var mLastMeasurementMarker: Marker? = null

    private val status = AtomicInteger(Status.INIT.value)

    enum class Status(val value: Int){
        INIT(0),
        MAP_LOADED(1),
        SESSION_LOADED(2)
    }

    constructor(rootView: View?, context: Context, supportFragmentManager: FragmentManager?) {
        mContext = context

        val mapFragment = supportFragmentManager?.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        mLocateButton = rootView?.locate_button
        mLocateButton?.setOnClickListener {
            locate()
        }
    }

    fun registerListener(listener: MapViewMvc.Listener) {
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
        mMap?.isBuildingsEnabled = false

        if (mMeasurements.size > 0) {
            drawSession()
            animateCameraToSession()
        } else {
            locate()
        }
    }

    fun bindStream(stream: MeasurementStream?) {
        mSelectedStream = stream
        mMeasurements = measurementsWithLocations(stream)

        // sometimes onMapReady is invoked earlier than bindStream
        if (status.get() == Status.MAP_LOADED.value) {
            setup()
        }
        status.set(Status.SESSION_LOADED.value)
    }

    private fun measurementsWithLocations(stream: MeasurementStream?): List<Measurement> {
        val measurements = stream?.measurements?.filter { it.latitude !== null && it.longitude != null }
        return measurements ?: emptyList()
    }

    private fun drawSession() {
        if (mMap == null) return

        var latestPoint: LatLng? = null
        var latestColor: Int? = null

        var i = 0
        for (measurement in mMeasurements) {
            latestColor = MeasurementColor.forMap(mContext, measurement, mSelectedStream!!)

            if (i > 0) {
                mMeasurementSpans.add(StyleSpan(latestColor))
            }
            latestPoint = LatLng(measurement.latitude!!, measurement.longitude!!)
            mMeasurementPoints.add(latestPoint)
            i += 1
        }
        mMeasurementsLineOptions.addAll(mMeasurementPoints).addAllSpans(mMeasurementSpans)
        mMeasurementsLine = mMap?.addPolyline(mMeasurementsLineOptions)

        if (latestPoint != null && latestColor != null) {
            drawLastMeasurementMarker(latestPoint, latestColor)
        }
    }

    private fun drawLastMeasurementMarker(point: LatLng, color: Int) {
        if (mLastMeasurementMarker != null) mLastMeasurementMarker!!.remove()

        val icon = BitmapHelper.bitmapFromVector(mContext, R.drawable.ic_dot_20, color)
        mLastMeasurementMarker = mMap?.addMarker(
            MarkerOptions()
                .position(point)
                .icon(icon)
        )
    }

    private fun animateCameraToSession() {
        val boundingBox = SessionBoundingBox.get(mMeasurements)
        val padding = 100 // meters
        mMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(boundingBox, padding))
    }

    fun centerMap(location: Location) {
        val position = LatLng(location.latitude, location.longitude)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
    }

    fun drawFixedMeasurement(point: LatLng, color: Int) {
        drawLastMeasurementMarker(point, color)
    }

    fun drawMobileMeasurement(point: LatLng, color: Int) {
        mMeasurementPoints.add(point)
        mMeasurementSpans.add(StyleSpan(color))

        if (mMeasurementsLine == null) {
            mMeasurementsLine = mMap?.addPolyline(mMeasurementsLineOptions)
        }

        mMeasurementsLine?.setPoints(mMeasurementPoints)
        mMeasurementsLine?.setSpans(mMeasurementSpans)

        drawLastMeasurementMarker(point, color)
    }

    fun refresh(stream: MeasurementStream) {
        mMap?.clear()
        bindStream(stream)
        drawSession()
    }

    private fun locate() {
        mListener?.locateRequested()
    }
}
