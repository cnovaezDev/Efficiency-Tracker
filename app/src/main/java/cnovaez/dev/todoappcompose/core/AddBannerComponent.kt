package cnovaez.dev.todoappcompose.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 ** Created by Carlos A. Novaez Guerrero on 11/2/2023 4:10 PM
 ** cnovaez.dev@outlook.com
 **/

@Composable
fun AddBannerComponent(modifier: Modifier = Modifier.fillMaxWidth(), adId: String) {
    Column(modifier = modifier) {
        AndroidView(modifier = Modifier.fillMaxWidth(), factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = adId
                loadAd(AdRequest.Builder().build())
            }
        })
    }
}