package cnovaez.dev.todoappcompose.login.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import cnovaez.dev.todoappcompose.R
import cnovaez.dev.todoappcompose.core.Routes
import cnovaez.dev.todoappcompose.login.data.LoginEntity
import kotlinx.coroutines.launch

/**
 ** Created by Carlos A. Novaez Guerrero on 11/5/2023 1:51 PM
 ** cnovaez.dev@outlook.com
 **/


@Composable
fun PinEntryScreen(loginViewModel: LoginViewModel, navigationController: NavHostController) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        Surface(modifier = Modifier.fillMaxSize())
        {
            var enteredPin by remember { mutableStateOf("") }
            val pinFields = remember { mutableStateListOf("", "", "", "") }

            val navigateTasks = loginViewModel.navigateTasks.observeAsState()

            if (navigateTasks.value == true) {
                navigationController.navigate(Routes.Tasks.route)
            }
            val loginPin = loginViewModel.login.value
            val correctPinSize = "1234" // Tamaño que debe tener el pin



            if (loginPin != null && loginPin.pass != -1) {

                if (enteredPin.hashCode() == loginPin.pass.hashCode()) {
                    // PIN correcto
                    loginViewModel.navigateTasks.postValue(true)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        PinInputFields(
                            pinFields = pinFields,
                            shouldShake = (enteredPin.trim().length == correctPinSize.length && enteredPin.hashCode() != loginPin.pass)
                        ) {
                            enteredPin = it
                        }
                    }
                }
            } else {
                if (enteredPin.length == correctPinSize.length) {
                    Dialog(onDismissRequest = { }) {
                        Column {
                            Row {
                                IconButton(onClick = {
                                    enteredPin = ""
                                    pinFields[3] = ""
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Close Save Pin"
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.save_pin),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(1f)
                                )
                                IconButton(onClick = {
                                    loginViewModel.insertLogin(
                                        LoginEntity(
                                            pass = enteredPin.hashCode(),
                                            secret = false
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Save,
                                        contentDescription = "Save Pin"
                                    )
                                }
                            }
                        }

                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    NewPinInputFields(
                        pinFields = pinFields,
                    ) {
                        enteredPin = it
                    }
                }
            }
        }
    }
}

@Composable
fun PinInputFields(
    pinFields: MutableList<String>,
    shouldShake: Boolean,
    onPinEntered: (String) -> Unit
) {
    val focusRequesters = List(pinFields.size) { FocusRequester() }
    val scope = rememberCoroutineScope()
    // Animación de temblor
    val shake by animateFloatAsState(
        targetValue = if (shouldShake) 2f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(50, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Dialog(onDismissRequest = { }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Icon Login",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(80.dp),
                    tint = Color.LightGray
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F4C3))
                        .padding(8.dp)

                ) {
                    for (i in pinFields.indices) {
                        BasicTextField(
                            value = pinFields[i],
                            onValueChange = {
                                if (it.length <= 1) {
                                    pinFields[i] = it
                                    if (it.isNotEmpty() && i < pinFields.size - 1) {
                                        pinFields[i + 1] = ""
                                        scope.launch {
                                            focusRequesters[i + 1].requestFocus()
                                        }
                                    }
                                    onPinEntered(pinFields.joinToString(""))
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .size(40.dp)
                                .focusRequester(focusRequesters[i])
                                .align(Alignment.CenterVertically)
                                .padding(4.dp)
                                .border(1.dp, Color.Black)
                                .weight(1f)
                                // Aplicar el desplazamiento de temblor
                                .offset(x = shake.dp * (i % 2 * 2 - 1)),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewPinInputFields(
    pinFields: MutableList<String>,
    onPinEntered: (String) -> Unit
) {
    val focusRequesters = List(pinFields.size) { FocusRequester() }
    val scope = rememberCoroutineScope()

    Dialog(onDismissRequest = { }) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column() {
                Icon(
                    imageVector = Icons.Rounded.Password,
                    contentDescription = "Icon Login",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(80.dp),
                    tint = Color.LightGray
                )
                Text(
                    text = stringResource(R.string.welcome_msg),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F4C3))
                        .padding(16.dp)
                ) {
                    for (i in pinFields.indices) {
                        BasicTextField(
                            value = pinFields[i],
                            onValueChange = {
                                if (it.length <= 1) {
                                    pinFields[i] = it
                                    if (it.isNotEmpty() && i < pinFields.size - 1) {
                                        pinFields[i + 1] = ""
                                        scope.launch {
                                            focusRequesters[i + 1].requestFocus()
                                        }
                                    }
                                    onPinEntered(pinFields.joinToString(""))
                                }
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .size(40.dp)
                                .focusRequester(focusRequesters[i])
                                .align(Alignment.CenterVertically)
                                .padding(4.dp)
                                .border(1.dp, Color.Black)
                                .weight(1f),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}


