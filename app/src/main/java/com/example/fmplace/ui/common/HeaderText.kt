package com.example.fmplace.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.fmplace.ui.theme.AppTypography

@Composable
fun AppHeaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTypography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}
