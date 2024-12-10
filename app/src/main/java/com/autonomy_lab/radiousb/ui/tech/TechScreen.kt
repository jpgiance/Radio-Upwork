package com.autonomy_lab.radiousb.ui.tech

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.autonomy_lab.radiousb.ui.MainViewModel
import com.autonomy_lab.radiousb.data.BarInfo
import com.autonomy_lab.radiousb.ui.components.TopBarTech
import com.autonomy_lab.radiousb.ui.components.UserInputDialog
import com.autonomy_lab.radiousb.ui.theme.app_alarm_red_background
import com.autonomy_lab.radiousb.ui.theme.app_dark_background
import com.autonomy_lab.radiousb.ui.theme.app_dark_text
import com.autonomy_lab.radiousb.ui.theme.app_grey_background
import com.autonomy_lab.radiousb.ui.theme.app_soft_blue_background
import com.autonomy_lab.radiousb.ui.theme.app_weird_yellow_background
import com.autonomy_lab.radiousb.ui.theme.app_yellow_background

@Composable
fun TechScreen(modifier: Modifier = Modifier,
               navigateToMainScreen: ()->Unit,
               viewModel: MainViewModel,
) {

    val list = viewModel.barInfoList.collectAsState()

    val dialogTypeAlarm = remember { mutableStateOf(true) } // true for Alarm, false for Max Bar Value
    val openAlertDialog = remember { mutableStateOf(false) }
    val alarmSettingPositionClicked = remember { mutableIntStateOf(0) }
    val maxBarValueSettingPositionClicked = remember { mutableIntStateOf(0) }


    UserInputDialog(
        showDialog = openAlertDialog.value,
        onDismiss = {
            openAlertDialog.value = false
        },
        onConfirm = { position, newValue ->
            openAlertDialog.value = false
            Log.e("TAG", "New Value is: $newValue in position $position" )

            if (dialogTypeAlarm.value){
                viewModel.updateAlarmSetting(position = position, newValue = newValue)
            }else{
                viewModel.updateMaxBarValueSetting(position = position, newValue = newValue.toInt())
            }

        },
        oldValue = if (dialogTypeAlarm.value) list.value[alarmSettingPositionClicked.intValue].alarmValue else list.value[maxBarValueSettingPositionClicked.intValue].maxBarValue.toFloat(),
        position = if (dialogTypeAlarm.value) alarmSettingPositionClicked.intValue else maxBarValueSettingPositionClicked.intValue
    )

    Scaffold (
        topBar = {
            TopBarTech(
                onBackToMainClicked = navigateToMainScreen,
                onToggleAllClicked = {viewModel.toggleAllShow()},
                onNormalizeClicked = {},
                onDefaultValuesClicked = {}
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
                            .fillMaxWidth(0.3f)
                            .clip(RoundedCornerShape(15.dp))
                            .background(app_yellow_background),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "BAND/FREQUENCY",
                            textAlign = TextAlign.Center,
                            color = app_dark_text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row (
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxSize()
                    ){
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(app_weird_yellow_background),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "SHOW /\nHIDE",
                                color = app_dark_text,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 12.sp,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(app_soft_blue_background),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "Max Bar\nValue",
                                color = app_dark_text,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 12.sp,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(app_soft_blue_background),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "Alarm\nsetting",
                                color = app_dark_text,
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 12.sp,
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(end = 4.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(app_alarm_red_background),
                            contentAlignment = Alignment.Center
                        ){
                            Text(
                                text = "ALARM",
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
                        items(list.value.size) { index ->
                            val item = list.value[index]

                            ItemTechContent(
                                item = item,
                                onAlarmOnOff = { viewModel.alarmOnOff(index) },
                                showHide = { viewModel.showHide(index)},
                                onAlarmSettingClicked = {
                                    dialogTypeAlarm.value = true
                                    openAlertDialog.value = true
                                    alarmSettingPositionClicked.intValue = index
                                },
                                onMaxBarValueSettingClicked = {
                                    dialogTypeAlarm.value = false
                                    openAlertDialog.value = true
                                    maxBarValueSettingPositionClicked.intValue = index
                                }
                            )

                        }


                    }
                }

            }


        }
    )

}


@Composable
fun ItemTechContent(modifier: Modifier = Modifier, item: BarInfo, onAlarmOnOff: ()->Unit, showHide: ()->Unit, onAlarmSettingClicked: ()->Unit, onMaxBarValueSettingClicked: ()->Unit) {


    val showText = if (item.show)  "YES" else "NO"
    val showColor = if (item.show)  app_weird_yellow_background else app_grey_background
    val alarmText = if (item.alarmOn)  "ON" else "OFF"
    val alarmColor = if (item.alarmOn)  app_alarm_red_background else app_grey_background



    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.3f)
                .clip(RoundedCornerShape(15.dp))
                .background(app_yellow_background),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "${item.bandFrequency.toInt()}",
                textAlign = TextAlign.Center,
                color = app_dark_text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ){
            Box(
                modifier = Modifier
                    .clickable { showHide() }
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(showColor),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = showText,
                    color = app_dark_text,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(app_soft_blue_background)
                    .clickable { onMaxBarValueSettingClicked() },
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${item.maxBarValue}",
                    color = app_dark_text,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(app_soft_blue_background)
                    .clickable { onAlarmSettingClicked() },
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "${item.alarmValue}",
                    color = app_dark_text,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 12.sp,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clickable { onAlarmOnOff() }
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(alarmColor),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = alarmText,
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


    }


}

