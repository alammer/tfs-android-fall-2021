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

    fun getLocalUserList(query: String): Single<List<LocalUser>>

    fun getRemoteUserList(query: String): Single<List<LocalUser>>

    fun getUser(userId: Int): Maybe<LocalUser>

    fun getOwner(): Single<LocalUser>
}


class ContactRepositoryImpl @Inject constructor(
    private val remoteApi: ApiService,
    private val localDao: ContactDataDao,
) : ContactRepository {

    override fun getLocalUserList(
        query: String,
    ): Single<List<LocalUser>> =
        localDao.getAllUsers()
            .subscribeOn(Schedulers.io())
            .map { localUserList -> localUserList.filter { it.userName.contains(query) } }

    override fun getRemoteUserList(
        query: String,
    ): Single<List<LocalUser>> {

        return fetchRemoteUserList()
            .subscribeOn(Schedulers.io())
            .flatMap { remoteUserList ->
                localDao.clearContacts()
                    .andThen(localDao.insertAllUsers(remoteUserList))
                    .andThen(Single.just(remoteUserList.filter { it.userName.contains(query) })) }
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

    private fun fetchRemoteUserList(): Single<List<LocalUser>> =
        remoteApi.getAllUsers()
            .map { response -> response.userList }
            .toObservable()
            .retryWhenError(3, 1)
            .flatMap { userList -> Observable.fromIterable(userList) }
            .flatMapSingle { user -> getUserWithPresence(user) }
            .toList()

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

    private fun getUserPresence(userId: Int): Single<UserPresence> {
        return remoteApi.getUserPresence(userId)
            .onErrorReturnItem(UserPresence(Presence(AggregatedStatus("Info not available", 0L))))
    }

}