package com.example.tfs.domain.contacts

import com.example.tfs.database.MessengerDB
import com.example.tfs.database.entity.LocalUser
import com.example.tfs.network.ApiService
import com.example.tfs.network.models.*
import com.example.tfs.network.models.UserPresence
import com.example.tfs.ui.streams.viewpager.StreamScreenState
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

interface ContactRepository {

    fun fetchUserList(query: String): Observable<List<LocalUser>>

    fun getUser(userId: Int): Maybe<LocalUser>
}

class ContactRepositoryImpl : ContactRepository {

    private val networkService = ApiService.create()
    private val database = MessengerDB.instance.localDataDao

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
                        database.insertAllUsers(remoteUserList)
                            .andThen(Single.just(remoteUserList.filter { it.userName.contains(query) }))
                    }
                    .startWith(localUserList.filter { it.userName.contains(query) })
            }
    }

    private fun getLocalUserList(): Single<List<LocalUser>> =
        database.getAllUsers()
            .subscribeOn(Schedulers.io())

    private fun getRemoteUserList(): Observable<List<LocalUser>> =
        networkService.getAllUsers()
            .map { response -> response.userList }
            .toObservable()
            .concatMap { userList -> Observable.fromIterable(userList) }
            .flatMap {user -> getUserWithPresence(user) }
            .toList()
            .toObservable()

    override fun getUser(userId: Int): Maybe<LocalUser> =
        database.getUser(userId)
            .subscribeOn(Schedulers.io())

    private fun getUserWithPresence(user: User) =
        Single.zip(networkService.getUser(user.id),
            getUserPresence(user.id),
            { userResponse, presenceResponse -> Pair(userResponse.user, presenceResponse.userPresence.userPresence) })
            .map { (user, presence) ->
                user.toLocalUser(presence)
            }
            .toObservable()

private fun getUserPresence(userId: Int): Single<UserPresence> {
        return networkService.getUserPresence(userId)
            .onErrorReturnItem(UserPresence(Presence(AggregatedStatus("Info not available", 0L))))
    }

}