package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.koin.dsl.koinApplication

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    //fake data source to be injected in the viewModel
    private lateinit var fakeReminderDataSource: FakeDataSource

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupRemindersListViewModel(){
        fakeReminderDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeReminderDataSource)
    }

    @Test
    fun testInvalidDataSource_returnError(){
        // Make the FakeDataSource return errors
        fakeReminderDataSource.shouldReturnError(true)

        // When the DataSource has no valid value
        fakeReminderDataSource = FakeDataSource(null)

        // Then return an error message
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue(), `is`("Error getting reminders"))

    }
    //TODO: provide testing to the RemindersListViewModel and its live data objects

}