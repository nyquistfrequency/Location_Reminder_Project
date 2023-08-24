package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [29])
class RemindersListViewModelTest : AutoCloseKoinTest() {

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
    fun setupRemindersListViewModel() {
        fakeReminderDataSource = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource
        )
    }

    @Test
    fun reminderList_checkIfContentAvailable() = runBlockingTest{
        // Given a reminder
        val savedReminder1 =
            ReminderDTO("Title1", "Description 1", "Location 1", 10.000, -10.000)

        // Save it
        fakeReminderDataSource.saveReminder(savedReminder1)

        // Load it to the list
        remindersListViewModel.loadReminders()

        // Then crosscheck that list isn't empty
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue().isEmpty(), `is`(false))
    }

    @Test
    fun invalidDataSource_ShouldReturnError() {
        // Given a DataSource with no valid value
        fakeReminderDataSource = FakeDataSource(null)

        // (updating viewModel because tasks = null for this test)
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeReminderDataSource
        )

        // Make the FakeDataSource return errors if loadReminders is triggered without a valid DataSource
        fakeReminderDataSource.shouldReturnError(true)
        remindersListViewModel.loadReminders()

        // Then crosscheck to confirm if the error has been thrown correctly
        assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            `is`("Error: Reminders not found")
        )

    }

    @Test
    fun loadTasks_checkLoading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        remindersListViewModel.loadReminders()

        // Then progress indicator is shown
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

}