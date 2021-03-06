package pl.llp.aircasting.di

import dagger.Module
import dagger.Provides
import pl.llp.aircasting.bluetooth.BluetoothManager
import pl.llp.aircasting.permissions.PermissionsManager
import javax.inject.Singleton

@Module
open class PermissionsModule {
    @Provides
    @Singleton
    open fun providesPermissionsManager(): PermissionsManager = PermissionsManager()

    @Provides
    @Singleton
    open fun providesBluetoothManager(permissionsManager: PermissionsManager): BluetoothManager {
        return BluetoothManager()
    }
}
