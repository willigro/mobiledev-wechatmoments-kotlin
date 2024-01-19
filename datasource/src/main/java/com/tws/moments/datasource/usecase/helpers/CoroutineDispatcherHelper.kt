package com.tws.moments.datasource.usecase.helpers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface IDispatcher {
    fun dispatcherIO(): CoroutineDispatcher

    fun dispatcherMain(): CoroutineDispatcher

    fun dispatcherDefault(): CoroutineDispatcher
}

class CoroutineDispatcherHelper : IDispatcher {

    override fun dispatcherIO(): CoroutineDispatcher = Dispatchers.IO

    override fun dispatcherMain(): CoroutineDispatcher = Dispatchers.Main

    override fun dispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

}