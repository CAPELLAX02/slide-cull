package com.capellax.slidescull.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PurchaseBottomSheet(
    onPurchaseClick: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Remove Ads", style = MaterialTheme.typography.titleLarge)
        Text(
            "Pay once and enjoy an ad-free experience forever.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onPurchaseClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with Google Pay")
        }

        TextButton(onClick = onCancel) {
            Text("Cancel")
        }
    }
}
