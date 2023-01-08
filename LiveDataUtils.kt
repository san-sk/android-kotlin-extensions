/**
Required Lib
Kotlin reflection - https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect
*/

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Extension to access the private functions of a class
 */
inline fun <reified T> T.callPrivateFunc(name: String, vararg args: Any?): Any? {
    val classArray: Array<Class<*>> = args.map { it!!::class.java }.toTypedArray()
    return T::class.java.getDeclaredMethod(name, *classArray)
        .apply { isAccessible = true }
        .invoke(this, *args)
}

/**
 * Get LiveData private function [LiveData.getVersion]
 *
 * Uses reflection concept
 */
fun LiveData<*>.version(): Int {
    return this.callPrivateFunc("getVersion") as Int
}

/**
 * Use this to get the latest value from live data.
 * This will skip the initial value and emit the latest one.
 *
 * Usage: to get values only after hitting the api
 */
fun <T> LiveData<T>.observeLatest(owner: LifecycleOwner, observer: Observer<in T>) {
    val sinceVersion = this.version()
    this.observe(owner, LatestObserver<T>(observer, this, sinceVersion))
}

fun <T> LiveData<T>.observeForeverLatest(
    observer: Observer<in T>,
    skipPendingValue: Boolean = true
) {
    val sinceVersion = this.version()
    this.observeForever(LatestObserver<T>(observer, this, sinceVersion))
}

// Removes the observer which has been previously observed by [observeFreshly] or [observeForeverFreshly].
fun <T> LiveData<T>.removeObserverLatest(observer: Observer<in T>) {
    this.removeObserver(LatestObserver<T>(observer, this, 0))
}

class LatestObserver<T>(
    private val delegate: Observer<in T>,
    private val liveData: LiveData<*>,
    private val sinceVersion: Int
) : Observer<T> {

    override fun onChanged(t: T) {
        if (liveData.version() > sinceVersion) {
            delegate.onChanged(t)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (delegate != (other as LatestObserver<*>).delegate) return false
        return true
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }
}
