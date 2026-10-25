package com.masoud.csvviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.masoud.csvviewer.presentation.feature.csvtable.composables.CsvTableScreen
import com.masoud.csvviewer.presentation.theme.CsvViewerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CsvViewerTheme {
                CsvTableScreen()
            }
        }
    }
}
