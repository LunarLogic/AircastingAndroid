package io.lunarlogic.aircasting.screens.new_session.select_device

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.*
import io.lunarlogic.aircasting.R
import io.lunarlogic.aircasting.lib.AnimatedLoader
import io.lunarlogic.aircasting.screens.common.BaseObservableViewMvc
import kotlinx.android.synthetic.main.select_device_group_header.view.*
import kotlinx.android.synthetic.main.select_device_item.view.*
import kotlinx.android.synthetic.main.select_device_item.view.label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectDeviceViewMvcImpl: BaseObservableViewMvc<SelectDeviceViewMvc.Listener>,
    SelectDeviceViewMvc {

    private var mRefreshListener: SelectDeviceViewMvc.OnRefreshListener? = null
    private var mRecyclerDevices: RecyclerView? = null
    private val mAdapter: GroupAdapter<GroupieViewHolder>
    private var mAirBeamDevicesSection: RecyclerViewSection? = null
    private var mOtherDevicesSection: RecyclerViewSection? = null
    private var mSelectedDeviceItem: DeviceItem? = null
    private var mRefreshButton: Button? = null
    private var mConnectButton: Button? = null

    constructor(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): super() {
        this.rootView = inflater.inflate(R.layout.fragment_select_device, parent, false)

        mRecyclerDevices = findViewById(R.id.recycler_devices)
        mRecyclerDevices?.setLayoutManager(LinearLayoutManager(rootView!!.context))
        mAdapter = GroupAdapter()
        mAdapter.setOnItemClickListener { item, view ->
            val recyclerViewItem = item as? RecyclerViewDeviceItem
            recyclerViewItem?.let { onDeviceItemSelected(it) }
        }
        mRecyclerDevices?.setAdapter(mAdapter)
        mRefreshButton = findViewById(R.id.refresh_button)
        mRefreshButton?.setOnClickListener {
            onRefreshClicked()
        }
        mConnectButton = findViewById(R.id.connect_button)
        mConnectButton?.setOnClickListener {
            onConnectClicked()
        }
    }

    override fun registerOnRefreshListener(refreshListener: SelectDeviceViewMvc.OnRefreshListener) {
        mRefreshListener = refreshListener
    }

    inner class RecyclerViewDeviceItem(val deviceItem: DeviceItem, var selected: Boolean = false) : Item<GroupieViewHolder>() {
        override fun getLayout() = R.layout.select_device_item

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if (selected) {
                viewHolder.itemView.radio.setImageResource(R.drawable.ic_radio_selected)
            } else {
                viewHolder.itemView.radio.setImageResource(R.drawable.ic_radio)
            }
            viewHolder.itemView.label.text = deviceItem.name.toUpperCase()
        }
    }

    inner class RecyclerViewGroupHeader(private val label: String?): Item<GroupieViewHolder>() {
        private var mLoader: ImageView? = null

        override fun getLayout() = R.layout.select_device_group_header

        override fun createViewHolder(itemView: View): GroupieViewHolder {
            val viewHolder = super.createViewHolder(itemView)

            mLoader = viewHolder.itemView.loader
            showLoader()

            return viewHolder
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.label.text = label
        }

        fun showLoader() {
            AnimatedLoader(mLoader).start()
            mLoader?.visibility = View.VISIBLE
            hideRefreshButton()

            // There is no way to actually know when the device list is ready
            // So we are hiding loader after 3s...
            GlobalScope.launch(Dispatchers.IO) {
                delay(3000)
                GlobalScope.launch(Dispatchers.Main) {
                    hideLoader()
                }
            }
        }

        fun hideLoader() {
            mLoader?.visibility = View.INVISIBLE
            showRefreshButton()
        }
    }

    inner class RecyclerViewSection(private val label: String?, private val deviceItems: List<DeviceItem>): Section() {
        private var mDeviceItemsAddresses: MutableSet<String>
        private val mHeader: RecyclerViewGroupHeader

        init {
            mHeader = RecyclerViewGroupHeader(label)
            setHeader(mHeader)

            mDeviceItemsAddresses = deviceItems.map { it.address }.toMutableSet()
            val recyclerItems = deviceItems.map { deviceItem -> RecyclerViewDeviceItem(deviceItem) }
            addAll(recyclerItems)
        }

        fun addUnique(deviceItem: DeviceItem) {
            if (!mDeviceItemsAddresses.contains(deviceItem.address)) {
                mDeviceItemsAddresses.add(deviceItem.address)
                val item = RecyclerViewDeviceItem(deviceItem)
                add(item)
            }
        }

        fun showLoader() {
            mHeader.showLoader()
        }
    }

    override fun bindDeviceItems(deviceItems: List<DeviceItem>) {
        val airbeams = deviceItems.filter { it.isAirBeam() }
        val others = deviceItems.filter { !it.isAirBeam() }

        mAirBeamDevicesSection = RecyclerViewSection(getLabel(R.string.select_device_airbeams_label), airbeams)
        mAdapter.add(mAirBeamDevicesSection!!)
        mOtherDevicesSection = RecyclerViewSection(getLabel(R.string.select_device_others_label), others)
        mAdapter.add(mOtherDevicesSection!!)
    }

    override fun addDeviceItem(deviceItem: DeviceItem) {
        if (deviceItem.isAirBeam()) {
            mAirBeamDevicesSection?.addUnique(deviceItem)
        } else {
            mOtherDevicesSection?.addUnique(deviceItem)
        }
    }

    fun showRefreshButton() {
        mRefreshButton?.visibility = View.VISIBLE
    }

    fun hideRefreshButton() {
        mRefreshButton?.visibility = View.INVISIBLE
    }

    private fun getLabel(labelId: Int): String? {
        return this.rootView?.context?.getString(labelId)
    }

    private fun onDeviceItemSelected(recyclerViewDeviceItem: RecyclerViewDeviceItem) {
        mSelectedDeviceItem = recyclerViewDeviceItem.deviceItem

        mAirBeamDevicesSection?.let { clearSelections(it) }
        mOtherDevicesSection?.let { clearSelections(it) }

        recyclerViewDeviceItem.selected = true
        mAdapter.notifyDataSetChanged()
    }

    private fun clearSelections(section: RecyclerViewSection) {
        section.groups?.forEach { item ->
            val recyclerViewDeviceItem = item as? RecyclerViewDeviceItem
            recyclerViewDeviceItem?.selected = false
        }
    }

    private fun showLoaders() {
        mAirBeamDevicesSection?.showLoader()
        mOtherDevicesSection?.showLoader()
    }

    private fun onRefreshClicked() {
        showLoaders()
        mRefreshListener?.onRefreshClicked()
    }

    private fun onConnectClicked() {
        mSelectedDeviceItem ?: return

        for (listener in listeners) {
            listener.onConnectClicked(mSelectedDeviceItem!!)
        }
    }
}
