package cafe.adriel.voyager.core.lifecycle

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import co.touchlab.stately.collections.ConcurrentMutableMap
import co.touchlab.stately.collections.ConcurrentMutableSet

internal object LifecycleEffectStore : ScreenDisposable {
    private val executedLifecycles = ConcurrentMutableMap<ScreenKey, ConcurrentMutableSet<LifecycleEffectOnceScope>>()

    fun store(screen: Screen, effectKey: String): LifecycleEffectOnceScope {
        val set = executedLifecycles.getOrPut(screen.key) { ConcurrentMutableSet() }
        val scope = LifecycleEffectOnceScope(uniqueKey = effectKey, set.size + 1)
        set.add(scope)

        return scope
    }

    fun hasExecuted(screen: Screen, effectKey: String): Boolean =
        executedLifecycles.get(screen.key)?.any { it.uniqueKey == effectKey } == true

    override fun onDispose(screen: Screen) {
        val scopes = executedLifecycles.remove(screen.key)
        scopes?.sortedBy { it.registerOrderIndex }?.reversed()?.forEach { scope ->
            scope.onDisposed?.invoke()
        }
    }
}
