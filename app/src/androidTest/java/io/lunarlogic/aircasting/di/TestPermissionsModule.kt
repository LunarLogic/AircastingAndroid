package io.lunarlogic.aircasting.di

import io.lunarlogic.aircasting.bluetooth.BluetoothManager
import io.lunarlogic.aircasting.permissions.PermissionsManager
import org.mockito.Mockito

class TestPermissionsModule: PermissionsModule() {
    override fun providesPermissionsManager(): PermissionsManager {
        return Mockito.mock(PermissionsManager::class.java)
    }

    override fun providesBluetoothManager(permissionsManager: PermissionsManager): BluetoothManager {
        return Mockito.mock(BluetoothManager::class.java)
    }
}