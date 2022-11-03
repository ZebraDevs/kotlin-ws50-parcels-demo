package com.zebra.nilac.csvbarcodelookup.models

class Event<T>(content: T) {

    private val mContent: T
    private var hasBeenHandled = false

    init {
        requireNotNull(content) { "null values in Event are not allowed." }
        mContent = content
    }

    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            mContent
        }

    fun hasBeenHandled(): Boolean {
        return hasBeenHandled
    }
}