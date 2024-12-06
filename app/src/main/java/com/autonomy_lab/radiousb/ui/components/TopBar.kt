package com.autonomy_lab.radiousb.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autonomy_lab.radiousb.ui.MainViewModel
import com.autonomy_lab.radiousb.ui.theme.app_dark_background
import com.autonomy_lab.radiousb.ui.theme.app_dark_text
import com.autonomy_lab.radiousb.ui.theme.app_dark_yellow_background
import com.autonomy_lab.radiousb.ui.theme.app_soft_blue_background
import com.autonomy_lab.radiousb.ui.theme.app_weird_yellow_background
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMain(
    modifier: Modifier = Modifier,
    onTechScreenClicked: ()->Unit,
    viewModel: MainViewModel,
    ) {

    val connected by viewModel.isUsbConnected().collectAsState()
    val normalizeOn by viewModel.normalizeValuesOn.collectAsState()
    val maxHoldOn by viewModel.maxHoldOn.collectAsState()

    val mainButtonColor = if (connected) app_soft_blue_background else app_dark_yellow_background
    val normalizeButtonColor = if (normalizeOn) app_soft_blue_background else app_dark_yellow_background
    val maxHoldButtonColor = if (maxHoldOn) app_soft_blue_background else app_dark_yellow_background

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = app_dark_background,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val paddingCompensation = 16.dp.toPx().roundToInt()
                        val adjustedConstraints = constraints.copy(
                            maxWidth = constraints.maxWidth + paddingCompensation
                        )
                        val placeable = measurable.measure(adjustedConstraints)
                        layout(placeable.width, placeable.height) {
                            placeable.place(-paddingCompensation / 2, 0)
                        }
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,

            ){

                TopButton(onClick = {viewModel.startStop()}, text = "START/END", modifier = Modifier.weight(0.25f), color = mainButtonColor)
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = onTechScreenClicked, text = "TECH SCREEN", modifier = Modifier.weight(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = {viewModel.normalizeValuesOnOff()}, text = "NORMALIZE", modifier = Modifier.weight(0.25f), color = normalizeButtonColor)
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = {viewModel.maxHoldOnOff()}, text = "MAX HOLD", modifier = Modifier.weight(0.25f), color = maxHoldButtonColor)


            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarTech(
    modifier: Modifier = Modifier,
    onBackToMainClicked: ()->Unit,
    onToggleAllClicked: ()->Unit,
    onNormalizeClicked: ()->Unit,
    onDefaultValuesClicked: ()->Unit,
) {


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = app_dark_background,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .layout { measurable, constraints ->
                        val paddingCompensation = 16.dp.toPx().roundToInt()
                        val adjustedConstraints = constraints.copy(
                            maxWidth = constraints.maxWidth + paddingCompensation
                        )
                        val placeable = measurable.measure(adjustedConstraints)
                        layout(placeable.width, placeable.height) {
                            placeable.place(-paddingCompensation / 2, 0)
                        }
                    }
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,

                ){

                TopButton(onClick = onBackToMainClicked, text = "BACK TO MAIN", modifier = Modifier.weight(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = onToggleAllClicked, text = "TOGGLE ALL", modifier = Modifier.weight(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = onNormalizeClicked, text = "", modifier = Modifier.weight(0.25f))
                Spacer(modifier = Modifier.width(4.dp))
                TopButton(onClick = onDefaultValuesClicked, text = "DEFAULT VALUES", modifier = Modifier.weight(0.25f))


            }
        }
    )
}

@Composable
fun TopButton(modifier: Modifier = Modifier,
              onClick: ()-> Unit,
              text: String,
              color: Color = app_dark_yellow_background
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(1.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp),
    ){
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = app_dark_text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold ,
        )
    }
}

