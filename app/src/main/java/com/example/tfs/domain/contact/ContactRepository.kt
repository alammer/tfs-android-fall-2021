package com.example.tfs.domain.contact

import com.example.tfs.database.dao.ContactDataDao
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.*
import com.example.tfs.util.retryWhenError
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

interface ContactRepository {

    fun fetchUserList(query: String): Observable<List<LocalUser>>

    fun getUser(userId: Int): Maybe<LocalUser>

    fun getOwner(): Single<LocalUser>
}


class ContactRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: ContactDataDao,
) : ContactRepository {

    override fun fetchUserList(
        query: String,
    ): Observable<List<LocalUser>> {
        val remoteSource: Observable<List<LocalUser>> =
            getRemoteUserList()

        return getLocalUserList()
            .subscribeOn(Schedulers.io())
            .flatMapObservable { localUserList: List<LocalUser> ->
                remoteSource
/*                    .observeOn(Schedulers.computation())    //DiffUtil maybe?
                   .filter { remoteStreamList: List<LocalStream> ->
                        remoteStreamList != localStreamList
                    }*/
                    .flatMapSingle { remoteUserList ->
                        localDao.insertAllUsers(remoteUserList)
                            .andThen(Single.just(remoteUserList.filter { it.userName.contains(query) }))
                    }
                    .startWith(localUserList.filter { it.userName.contains(query) })
            }
    }

    override fun getUser(userId: Int): Maybe<LocalUser> =
        localDao.getUser(userId)
            .subscribeOn(Schedulers.io())

    override fun getOwner(): Single<LocalUser> {
        return remoteApi.getOwner()
            .subscribeOn(Schedulers.io())
            .flatMap { user ->
                getUserPresence(user.id)
                    .onErrorReturnItem(
                        UserPresence(
                            Presence(
                                AggregatedStatus(
                                    "Info not available",
                                    0L
                                )
                            )
                        )
                    )
                    .map { presence -> user.toLocalUser(presence.userPresence.state) }
            }
    }

    private fun getLocalUserList(): Single<List<LocalUser>> =
        localDao.getAllUsers()
            .subscribeOn(Schedulers.io())

    private fun getRemoteUserList(): Observable<List<LocalUser>> =
        remoteApi.getAllUsers()
            .map { response -> response.userList }
            .toObservable()
            .retryWhenError(3, 1)
            .concatMap { userList -> Observable.fromIterable(userList) }
            .flatMap { user -> getUserWithPresence(user) }
            .toList()
            .toObservable()

    private fun getUserWithPresence(user: User) =
        Single.zip(remoteApi.getUser(user.id),
            getUserPresence(user.id),
            { userResponse, presenceResponse ->
                Pair(
                    userResponse.user,
                    presenceResponse.userPresence.state
                )
            })
            .map { (user, presence) ->
                user.toLocalUser(presence)
            }
            .toObservable()

    private fun getUserPresence(userId: Int): Single<UserPresence> {
        return remoteApi.getUserPresence(userId)
            .onErrorReturnItem(UserPresence(Presence(AggregatedStatus("Info not available", 0L))))
    }

}