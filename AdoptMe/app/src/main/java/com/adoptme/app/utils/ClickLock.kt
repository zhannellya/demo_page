package com.adoptme.app.utils

import java.util.concurrent.atomic.AtomicBoolean

/** Ignores repeated clicks while a save is already running. */
class ClickLock {
    private val busy = AtomicBoolean(false)

    fun tryAcquire(): Boolean = busy.compareAndSet(false, true)

    fun release() {
        busy.set(false)
    }
}
