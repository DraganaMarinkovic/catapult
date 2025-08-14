package com.example.catapult.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts LIMIT 1")
    fun getAccount(): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(account: AccountEntity): Long

    @Query("DELETE FROM accounts")
    fun deleteAllAccounts()
}
