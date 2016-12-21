package ru.rovkinmax.globusittech.preenter

import android.app.LoaderManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import ru.rovkinmax.globusittech.loader.LoaderProvider
import ru.rovkinmax.globusittech.model.User
import ru.rovkinmax.globusittech.presenter.MainPresenter
import ru.rovkinmax.globusittech.view.MainView

@RunWith(MockitoJUnitRunner::class)
class MainPresenterTest {

    @Mock
    private lateinit var loaderProvider: LoaderProvider<List<User>>

    @Mock
    private lateinit var loaderManager: LoaderManager

    @Mock
    private lateinit var view: MainView

    private lateinit var presenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = Mockito.spy(MainPresenter(view, loaderManager, loaderProvider))
    }

    @Test
    fun testSwapTest() {
        Mockito.doNothing().`when`(presenter).dispatchListForUpdateDataBase(Mockito.anyList())
        val list = arrayListOf(
                User(id = 0, next = 1, prev = -1),
                User(id = 1, next = 2, prev = 0),
                User(id = 2, next = 3, prev = 1),
                User(id = 3, next = -1, prev = 2))

        presenter.swapList(list, 2, 0)
        Mockito.verify(view).notifyItemMoved(2, 0)

        arrayListOf(
                User(id = 2, next = 0, prev = -1),
                User(id = 0, next = 1, prev = 2),
                User(id = 1, next = 3, prev = 0),
                User(id = 3, next = -1, prev = 1))
                .forEachIndexed { i, user ->
                    Assert.assertEquals(user, list[i])
                }

        presenter.swapList(list, 1, 3)
        Mockito.verify(view).notifyItemMoved(1, 3)

        arrayListOf(
                User(id = 2, next = 1, prev = -1),
                User(id = 1, next = 3, prev = 2),
                User(id = 3, next = 0, prev = 1),
                User(id = 0, next = -1, prev = 3))
                .forEachIndexed { i, user ->
                    Assert.assertEquals(user, list[i])
                }
    }

    @Test
    fun testRestoreListOrder() {
        val expected = arrayListOf(
                User(id = 0, next = 1, prev = -1),
                User(id = 1, next = 2, prev = 0),
                User(id = 2, next = 3, prev = 1),
                User(id = 3, next = -1, prev = 2))


        val list = arrayListOf(
                User(id = 0, next = 1, prev = -1),
                User(id = 3, next = -1, prev = 2),
                User(id = 2, next = 3, prev = 1),
                User(id = 1, next = 2, prev = 0))

        MainPresenter.restoreListOrder(list).forEachIndexed { i, user ->
            Assert.assertEquals(expected[i], user)
        }
    }
}