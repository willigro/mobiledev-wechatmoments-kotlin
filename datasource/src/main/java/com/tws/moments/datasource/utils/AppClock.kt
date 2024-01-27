package com.tws.moments.datasource.utils

interface AppClock {
    fun now(): Long
}

class RealAppClock : AppClock {
    override fun now(): Long = System.currentTimeMillis()
}