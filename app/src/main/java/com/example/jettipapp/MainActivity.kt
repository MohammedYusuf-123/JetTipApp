package com.example.jettipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable (Modifier) -> Unit) {
    JetTipAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            content(Modifier.padding(innerPadding))
        }
    }
}

@Preview
@Composable
fun MainContent() {
    BillForm { billAmount ->
        Log.d("AMT", "Bill amount is: $billAmount")
    }
}

@Composable
fun TopHeader(totalPerPerson: Double) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            //.clip(shape = CircleShape.copy(all = CornerSize(60.dp)))
            .padding(16.dp),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        color = Color(0xFFE9D7F7),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.headlineMedium
                /*TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)*/
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember { mutableStateOf("") }
    val validState = remember(totalBillState.value) { totalBillState.value.trim().isNotBlank() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var splitByState = remember { mutableIntStateOf(1) }
    val sliderPositionState = remember { mutableFloatStateOf(0f) }
    val tipPercentage = sliderPositionState.floatValue.toInt()
    val range = IntRange(start = 1, endInclusive = 100)
    val tipAmountState = remember { mutableDoubleStateOf(0.0) }
    val totalPerPersonState = remember { mutableDoubleStateOf(0.0) }

    Column {
        TopHeader(totalPerPerson = totalPerPersonState.doubleValue)

        Surface(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())
                        keyboardController?.hide()
                    }
                )
                if (validState) {
                    Row(
                        modifier = Modifier
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "Split",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(120.dp))
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            RoundIconButton(
                                imageVector = Icons.Default.Remove,
                                onClick = {
                                    if (splitByState.intValue > 1) splitByState.intValue--
                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = sliderPositionState.floatValue.toInt(),
                                        splitBy = splitByState.intValue
                                    )
                                },
                            )
                            Text(
                                text = "${splitByState.intValue}",
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 9.dp, end = 9.dp)
                            )
                            RoundIconButton(
                                imageVector = Icons.Default.Add,
                                onClick = {
                                    if (splitByState.intValue < range.last) splitByState.intValue++
                                    totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        tipPercentage = sliderPositionState.floatValue.toInt(),
                                        splitBy = splitByState.intValue
                                    )
                                },
                            )
                        }
                    }
                } else {
                    Box {

                    }
                }
//tip row
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(183.dp))
                    Text(
                        text = "$${"%.2f".format(tipAmountState.doubleValue)}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column {
                    Row {
                        Text(text = "Select Tip %")
                        Spacer(modifier = Modifier.width(128.dp))
                        Text(text = "$tipPercentage%")
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    //Slider
                    Slider(
                        value = sliderPositionState.floatValue,
                        onValueChange = { newVal ->
                            sliderPositionState.floatValue = newVal
                            Log.d("Slider", "BillForm: $newVal")
                            tipAmountState.doubleValue =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPercentage = newVal.toInt()
                                )
                            totalPerPersonState.doubleValue = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = newVal.toInt(),
                                splitBy = splitByState.intValue
                            )

                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        valueRange = 0f..100f,
                        steps = 5,
//                        colors = SliderDefaults.colors(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JetTipApp() {
    JetTipAppTheme {
        MyApp {
            MainContent()
        }
    }
}