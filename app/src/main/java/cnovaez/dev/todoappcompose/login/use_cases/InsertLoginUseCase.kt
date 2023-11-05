package cnovaez.dev.todoappcompose.login.use_cases

import cnovaez.dev.todoappcompose.login.data.LoginEntity
import cnovaez.dev.todoappcompose.login.data.LoginRepository
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 3:42 PM
 ** cnovaez.dev@outlook.com
 **/
class InsertLoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(loginEntity: LoginEntity) {
        loginRepository.insertLogin(loginEntity)
    }
}