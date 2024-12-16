package cafe.adriel.voyager.navigator.lifecycle

import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.stately.collections.ConcurrentMutableMap
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public typealias NavigatorKey = String

public object NavigatorLifecycleStore {

    private val owners = ConcurrentMutableMap<NavigatorKey, ConcurrentMutableMap<KType, NavigatorDisposable>>()

    /**
     * Register a NavigatorDisposable that will be called `onDispose` on the
     * [navigator] leaves the Composition.
     */
    public inline fun <reified T : NavigatorDisposable> get(
        navigator: Navigator,
        noinline factory: (NavigatorKey) -> T
    ): T {
        return get(navigator, typeOf<T>(), factory) as T
    }

    @PublishedApi
    internal fun <T : NavigatorDisposable> get(
        navigator: Navigator,
        screenDisposeListenerType: KType,
        factory: (NavigatorKey) -> T
    ): NavigatorDisposable {
        return owners.getOrPut(navigator.key) {
            ConcurrentMutableMap<KType, NavigatorDisposable>().apply {
                put(screenDisposeListenerType, factory(navigator.key))
            }
        }.getOrPut(screenDisposeListenerType) {
            factory(navigator.key)
        }
    }

    public fun remove(navigator: Navigator) {
        owners.remove(navigator.key)?.forEach { it.value.onDispose(navigator) }
    }
}
