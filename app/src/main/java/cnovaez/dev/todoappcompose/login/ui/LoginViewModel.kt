package cnovaez.dev.todoappcompose.login.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cnovaez.dev.todoappcompose.login.data.LoginEntity
import cnovaez.dev.todoappcompose.login.use_cases.GetLoginUseCase
import cnovaez.dev.todoappcompose.login.use_cases.GetSecretLoginUseCase
import cnovaez.dev.todoappcompose.login.use_cases.InsertLoginUseCase
import cnovaez.dev.todoappcompose.utils.logs.LogError
import cnovaez.dev.todoappcompose.utils.logs.LogInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 3:46 PM
 ** cnovaez.dev@outlook.com
 **/
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val insertLoginUseCase: InsertLoginUseCase,
    private val getLoginUseCase: GetLoginUseCase,
    private val getSecretLoginUseCase: GetSecretLoginUseCase
) : ViewModel() {


    private val _login = MutableLiveData<LoginEntity>()
    private val _secretLogin = MutableLiveData<LoginEntity>()

    val login: LiveData<LoginEntity> = _login
    val secretLogin: LiveData<LoginEntity> = _secretLogin
    val navigateTasks = MutableLiveData<Boolean>()
    fun insertLogin(loginEntity: LoginEntity) {
        viewModelScope.launch {
            try {
                insertLoginUseCase(loginEntity)
                navigateTasks.value = true
                LogInfo("Login insertado correctamente")
            } catch (e: Exception) {
                e.printStackTrace()
                LogError("Error insertando login", e)
            }
        }
    }

    fun getLogin() {
        viewModelScope.launch {
            _login.value = getLoginUseCase()
        }
    }

    fun getSecretLogin() {
        viewModelScope.launch {
            _secretLogin.value = getSecretLoginUseCase()
        }
    }

}