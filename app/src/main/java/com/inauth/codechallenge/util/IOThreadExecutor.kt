package com.inauth.codechallenge.util

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Executor that runs a task on a new background thread.
 */
class IOThreadExecutor : Executor {

    private val _io = Executors.newSingleThreadExecutor()

    override fun execute(command: Runnable) { _io.execute(command) }
}