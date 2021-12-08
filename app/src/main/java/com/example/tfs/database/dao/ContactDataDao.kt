package com.example.tfs.database.dao

import androidx.room.*
import com.example.tfs.database.entity.LocalOwner
import com.example.tfs.database.entity.LocalUser
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface ContactDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(localUsers: List<LocalUser>): Completable

    @Query("SELECT * FROM contacts")
    fun getAllUsers(): Single<List<LocalUser>>

    @Query("SELECT * FROM contacts WHERE user_id = :userId")
    fun getUser(userId: Int): Maybe<LocalUser>

    @Query("DELETE FROM contacts")
    fun clearContacts(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOwner(owner: LocalOwner): Completable

    @Delete
    fun deleteOwner(owner: LocalOwner): Completable

    @Query("SELECT * FROM owner")
    fun getOwner(): Maybe<LocalOwner>
}