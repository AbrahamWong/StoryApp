package com.minotawr.storyapp.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.minotawr.storyapp.data.StoryTestPagingSource
import com.minotawr.storyapp.domain.model.Story
import com.minotawr.storyapp.domain.usecase.AuthUseCase
import com.minotawr.storyapp.domain.usecase.StoryUseCase
import com.minotawr.storyapp.ui.home.adapter.paging.HomePagingAdapter
import com.minotawr.storyapp.utils.DataDummy
import com.minotawr.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock private lateinit var authUseCase: AuthUseCase
    @Mock private lateinit var storyUseCase: StoryUseCase

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel(authUseCase, storyUseCase)
    }

    @Test
    fun `when successfully Get all story should not null or empty`() = runTest {
        val dummyData = DataDummy.generateDummyUseCaseData()
        val dummyPagingSource = StoryTestPagingSource.snapshot(dummyData)

        val expectedStories: Flow<PagingData<Story>> = flow {
            emit(dummyPagingSource)
        }

        Mockito.`when`(storyUseCase.getPagedStories()).thenReturn(expectedStories)

        viewModel.getPagedStories().test {
            val actualStories = awaitItem()
            assertNotNull(actualStories)

            val differ = AsyncPagingDataDiffer(
                diffCallback = HomePagingAdapter.DIFF_CALLBACK,
                updateCallback = object: ListUpdateCallback {
                    override fun onInserted(position: Int, count: Int) {}
                    override fun onRemoved(position: Int, count: Int) {}
                    override fun onMoved(fromPosition: Int, toPosition: Int) {}
                    override fun onChanged(position: Int, count: Int, payload: Any?) {}
                },
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualStories)

            assertNotNull(differ.snapshot())
            assertEquals(dummyData.size, differ.snapshot().size)
            assertEquals(dummyData[0], differ.snapshot()[0])

            Mockito.verify(storyUseCase).getPagedStories()

            cancel()
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `when Get all story is empty should return empty`() = runTest {
        val expectedStories: Flow<PagingData<Story>> = flow {
            emit(PagingData.from(emptyList()))
        }

        Mockito.`when`(storyUseCase.getPagedStories()).thenReturn(expectedStories)

        viewModel.getPagedStories().test {
            val actualStories = awaitItem()
            assertNotNull(actualStories)

            val differ = AsyncPagingDataDiffer(
                diffCallback = HomePagingAdapter.DIFF_CALLBACK,
                updateCallback = object: ListUpdateCallback {
                    override fun onInserted(position: Int, count: Int) {}
                    override fun onRemoved(position: Int, count: Int) {}
                    override fun onMoved(fromPosition: Int, toPosition: Int) {}
                    override fun onChanged(position: Int, count: Int, payload: Any?) {}
                },
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualStories)

            assertEquals(0, differ.snapshot().size)

            Mockito.verify(storyUseCase).getPagedStories()

            cancel()
            ensureAllEventsConsumed()
        }
    }
}