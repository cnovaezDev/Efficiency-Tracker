package cnovaez.dev.todoappcompose.login.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 3:35 PM
 ** cnovaez.dev@outlook.com
 **/
@Dao
interface LoginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertLogin(LoginEntity: LoginEntity)

    @Query("SELECT * FROM logins WHERE secret = 0")
    suspend fun getLogin(): LoginEntity

    @Query("SELECT * FROM logins WHERE secret = 1")
    suspend fun getSecretLogin(): LoginEntity
}