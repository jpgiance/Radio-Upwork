package com.autonomy_lab.radiousb.ui.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autonomy_lab.radiousb.ui.MainViewModel
import com.autonomy_lab.radiousb.data.BarInfo
import com.autonomy_lab.radiousb.ui.components.TopBarMain
import com.autonomy_lab.radiousb.ui.theme.app_alarm_red_background
import com.autonomy_lab.radiousb.ui.theme.app_dark_background
import com.autonomy_lab.radiousb.ui.theme.app_dark_text
import com.autonomy_lab.radiousb.ui.theme.app_grey_background
import com.autonomy_lab.radiousb.ui.theme.app_yellow_background

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navigateToTechScreen: () -> Unit,
    viewModel: MainViewModel,
) {



    val list by viewModel.barInfoList.collectAsState()
    val maxHoldOn by viewModel.maxHoldOn.collectAsState()
    val normalizeValuesOn by viewModel.normalizeValuesOn.collectAsState()


    Scaffold (
        topBar = {
            TopBarMain(
                onTechScreenClicked = navigateToTechScreen,
                viewModel = viewModel
            )
        },
        content = {padding ->

            Column (
                modifier = Modifier
                    .background(app_dark_background)
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
            ){
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.75f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(app_grey_background),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "BAND NAME\nBAND FREQUENCY",
                            textAlign = TextAlign.Center,
                            color = app_dark_text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(15.dp))
                            .background(app_grey_background),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "CURRENT\nVALUE",
                            color = app_dark_text,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp,
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                        )
                    }

                }

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LazyColumn (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 5.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues( top = 10.dp, bottom = 10.dp)

                    ) {
                        items(list.filter { it.show }) { item ->

                            ItemMainContent(
                                item = item,
                                normalizeValuesOn = normalizeValuesOn,
                                maxHoldOn = maxHoldOn
                            )

                        }


                    }
                }

            }

        }
    )
}


@Composable
fun ItemMainContent(modifier: Modifier = Modifier, item: BarInfo, normalizeValuesOn: Boolean, maxHoldOn: Boolean) {

    var barFraction = 0f

    var adjustedValue = 600f

    if (item.lastValue > 0){
        adjustedValue = (item.lastValue % (2900 - 600 + 1)) + 600
//        Log.e("TAG", "ItemMainContent: $adjustedValue" )

        val minScaleValue = 2900f
        val maxScaleValue = 600f

        if (adjustedValue in 600.0..2900.0) {
            val range = minScaleValue - maxScaleValue // Calculate the range
            val normalizedValue = (adjustedValue - maxScaleValue)/ range

            // Invert the normalized value to match the desired mapping
            barFraction = 1.0f - normalizedValue
        }
    }

    val currentValue =
        if (maxHoldOn) item.maxValue
        else if (normalizeValuesOn) item.normalizedValue
        else adjustedValue


    val barColor =  if (item.alarmOn and item.alarmState) app_alarm_red_background else app_yellow_background
    val barValueColor =  if (item.alarmOn and item.alarmState) Color.White else app_dark_text
    val currentValueColor =  if (item.alarmOn and item.alarmState) app_alarm_red_background else app_grey_background


    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.75f)
                .clip(RoundedCornerShape(15.dp))
                .background(app_grey_background),
            contentAlignment = Alignment.CenterStart
        ){
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barFraction)
                    .clip(RoundedCornerShape(15.dp))
                    .background(barColor)

            )
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${item.bandFrequency.toInt()}",
                    color = barValueColor,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(15.dp))
                .background(currentValueColor),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "$currentValue",
                maxLines = 1,
                color = barValueColor,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            )
        }

    }

    
}

