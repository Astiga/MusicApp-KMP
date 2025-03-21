package musicapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.seiko.imageloader.Bitmap
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.MemoryCacheBuilder
import com.seiko.imageloader.cache.memory.MemoryKey
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.intercept.bitmapMemoryCacheConfig
import com.seiko.imageloader.util.identityHashCode
import musicapp.decompose.MusicRootImpl
import musicapp.di.KoinHelper
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController
import platform.UIKit.setStatusBarStyle

fun MainiOS(
    lifecycle: LifecycleRegistry,
): UIViewController {
    KoinHelper.initKoin()

    return ComposeUIViewController(configure = { 
        enforceStrictPlistSanityCheck = false 
    }) {
        // Set status bar style for light content (white icons)
        UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleLightContent)

        val rootComponent = MusicRootImpl(
            componentContext = DefaultComponentContext(lifecycle = lifecycle)
        )

        Column(Modifier.fillMaxSize()) {
            CompositionLocalProvider(
                LocalImageLoader provides ImageLoader {
                    components {
                        setupDefaultComponents()
                    }
                    interceptor {
                        bitmapMemoryCacheConfig(
                            valueHashProvider = { identityHashCode(it) },
                            valueSizeProvider = { 500 },
                            block = fun MemoryCacheBuilder<MemoryKey, Bitmap>.() {
                                maxSizePercent(0.25)
                            }
                        )
                    }
                }
            ) {
                MainCommon(rootComponent, false)
            }
        }
    }
}
