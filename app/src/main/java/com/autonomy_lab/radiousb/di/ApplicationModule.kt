package com.autonomy_lab.radiousb.di

import android.content.Context
import com.autonomy_lab.radiousb.communication.ProtocolParser
import com.autonomy_lab.radiousb.communication.usb.UsbSerialController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideUsBSerialController(@ApplicationContext context: Context, parser: ProtocolParser): UsbSerialController{
        return UsbSerialController(context, parser)
    }

    @Provides
    @Singleton
    fun providesProtocolParser(): ProtocolParser{
        return ProtocolParser()
    }
}