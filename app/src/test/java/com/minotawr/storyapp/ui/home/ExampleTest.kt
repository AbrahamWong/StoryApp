package com.minotawr.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.minotawr.storyapp.data.remote.network.Resource
import com.minotawr.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class ExampleTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `test flow with turbine`() = runTest {
        val flow = flow {
            emit(Resource.Loading())
            delay(100L)
            emit(Resource.Success("booyah"))
        }

        flow.test {
            val await = awaitItem()
            assert(await is Resource.Loading)

            val successAwait = awaitItem()
            assertEquals(successAwait.data, "booyah")

            awaitComplete()
        }
    }

}