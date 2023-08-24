package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class SaveReminderViewModelTest : AutoCloseKoinTest() {

    //Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    //fake data source to be injected in the viewModel
    private lateinit var fakeSaveReminderFakeDataSource: FakeDataSource


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupSaveReminderViewModel() {
        fakeSaveReminderFakeDataSource = FakeDataSource()

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeSaveReminderFakeDataSource
        )
    }

    @Test
    fun saveReminder_returnErrorIfTitleMissing() {
        // Given a reminder that has no title
        val reminderToBeSaved =
            ReminderDataItem("", "Description 1", "Location 1", 10.000, -10.000)

        // Then validateEnteredData is false
        assertThat(saveReminderViewModel.validateEnteredData(reminderToBeSaved), `is`(false))

        // Thus triggering an error message snackbar
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
    }

    @Test
    fun saveReminder_returnErrorIfLocationMissing() {
        // Given a reminder that has no location
        val reminderToBeSaved =
            ReminderDataItem("Title 1", "Description 1", "", 10.000, -10.000)

        // Then validateEnteredData is false
        assertThat(saveReminderViewModel.validateEnteredData(reminderToBeSaved), `is`(false))

        // Thus triggering an error message snackbar
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))

    }

    @Test
    fun saveReminder_checkLoading() {
        // Given a reminder
        val reminderToBeSaved =
            ReminderDataItem("Title 1", "Description 1", "Location 1", 10.000, -10.000)

        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Save the reminder in the viewmodel
        saveReminderViewModel.saveReminder(reminderToBeSaved)

        // Then progress indicator is shown
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(false))
    }
}