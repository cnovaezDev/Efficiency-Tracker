package cnovaez.dev.todoappcompose.add_tasks.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 9:16 PM
 ** cnovaez.dev@outlook.com
 **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showSnackbar(
    message: String,
    actionLabel: String,
    onActionCanceled: () -> Unit,
    onSnackBarDismissed: () -> Unit,
): SnackbarResult {
    val scaffoldState = rememberBottomSheetScaffoldState()
    LaunchedEffect(scaffoldState.snackbarHostState) {
        scaffoldState.snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel
        ).let { result ->
            if (result == SnackbarResult.ActionPerformed) {
                onActionCanceled()
            } else if (result == SnackbarResult.Dismissed) {
                onSnackBarDismissed()
            }
        }
    }

    return SnackbarResult.ActionPerformed
}