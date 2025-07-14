package com.capellax.slidescull.ui.navigation

import android.app.Activity
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.capellax.slidescull.model.GalleryImage
import com.capellax.slidescull.ui.components.PurchaseBottomSheet
import com.capellax.slidescull.ui.screen.*
import com.capellax.slidescull.viewmodel.GalleryViewModel

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    val galleryViewModel: GalleryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            // Yeni animasyonlu splash
            SplashScreen(navController)
        }

        composable("permission") {
            PermissionScreen(navController)
        }

        composable("entry") {
            var showSheet by remember { mutableStateOf(false) }

            EntryScreen(
                onRemoveAdsClick = { showSheet = true },
                onTryFreeClick = { navController.navigate("main") }
            )

            if (showSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showSheet = false }
                ) {
                    PurchaseBottomSheet(
                        onPurchaseClick = { showSheet = false },
                        onCancel = { showSheet = false }
                    )
                }
            }
        }

        composable("main") {
            MainSwipeScreen(
                viewModel = galleryViewModel,
                onDone = { toDeleteList ->
                    // Kullanıcı "Done" butonuna basınca confirm_delete'e gidecek
                    navController.currentBackStackEntry?.savedStateHandle?.set("toDelete", toDeleteList)
                    navController.navigate("confirm_delete")
                }
            )
        }

        composable("confirm_delete") {
            val toDelete = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<GalleryImage>>("toDelete") ?: emptyList()

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    galleryViewModel.onDeleteConfirmed()
                    // success ekranına giderken geri dönmeyi kapatıyoruz
                    navController.navigate("success") {
                        popUpTo("confirm_delete") { inclusive = true }
                    }
                }
            }

            ConfirmDeleteScreen(
                initialImages = toDelete,
                onConfirmDelete = { selected ->
                    val intentSender = galleryViewModel.createDeleteRequest(selected)
                    intentSender?.let {
                        launcher.launch(IntentSenderRequest.Builder(it).build())
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable("success") {
            SuccessScreen(onContinueClick = {
                // success screen'den geri dönülmesin
                // entry ekranına geçelim
                navController.navigate("entry") {
                    popUpTo("success") { inclusive = true }
                }
            })
        }
    }
}
