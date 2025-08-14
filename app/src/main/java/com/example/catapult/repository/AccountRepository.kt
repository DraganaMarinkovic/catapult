package com.example.catapult.repository

import com.example.catapult.db.AccountDao
import com.example.catapult.db.AccountEntity
import com.example.catapult.datastore.HasAccountStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val dao: AccountDao,
    private val store: HasAccountStore
) {
    val hasAccountFlow: Flow<Boolean> = store.hasAccountFlow

    fun getAccount(): Flow<AccountEntity?> = dao.getAccount().flowOn(Dispatchers.IO)

    suspend fun createAccount(account: AccountEntity): Long {
        withContext(Dispatchers.IO) {
            val rowId = dao.upsert(account)
            store.setHasAccount(true)
            return@withContext rowId
        }
        return -1
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            dao.deleteAllAccounts()
            store.setHasAccount(false)
        }
    }
}
