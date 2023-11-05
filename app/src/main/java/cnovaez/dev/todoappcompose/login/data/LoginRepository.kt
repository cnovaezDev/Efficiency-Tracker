package cnovaez.dev.todoappcompose.login.data

import javax.inject.Inject
import javax.inject.Singleton

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 3:41 PM
 ** cnovaez.dev@outlook.com
 **/
@Singleton
class LoginRepository @Inject constructor(
    private val loginDao: LoginDao
) {
   suspend fun insertLogin(loginEntity: LoginEntity) {
        loginDao.insertLogin(loginEntity)
    }

    suspend fun getLogin(): LoginEntity {
        return loginDao.getLogin()
    }

    suspend fun getSecretLogin(): LoginEntity {
        return loginDao.getSecretLogin()
    }
}
