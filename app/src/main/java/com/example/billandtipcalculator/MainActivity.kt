@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.billandtipcalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.billandtipcalculator.components.InputField
import com.example.billandtipcalculator.components.RoundIconButton
import com.example.billandtipcalculator.ui.theme.BillAndTipCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit){
    BillAndTipCalculatorTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
        .fillMaxWidth()
        .height(150.dp),
        color = Color(color = 0xFFE9D7F7)) {
        Column(
            Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Total Per Person", style = MaterialTheme.typography.h5)
            Text(text = "$${"%.2f".format(totalPerPerson)}",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview
@Composable
fun MainContent(){
    BillForm(){

    }
}

@Preview
@Composable
fun BillForm(modifier: Modifier = Modifier, onValChange: (String) -> Unit = {}){
    val billAmount = remember {
        mutableStateOf("")
    }

    val validState =  remember(billAmount.value) {
        billAmount.value.trim().isNotEmpty()
    }

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = remember {
        mutableStateOf(0)
    }

    val tipAmount = remember {
        mutableStateOf(0.0)
    }

    val totalPerPerson = remember {
        mutableStateOf(0.0)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val numberOfContributors = remember {
        mutableStateOf(1)
    }

    fun calculate(){
        tipAmount.value = billAmount.value.trim().toDouble() * tipPercentage.value / 100
        totalPerPerson.value = (billAmount.value.trim().toFloat() + tipAmount.value) / numberOfContributors.value
    }

    Column(modifier = Modifier.padding(15.dp)) {
        TopHeader(totalPerPerson.value)
        Spacer(modifier = Modifier.height(16.dp))
        Surface(modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color.LightGray)) {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                InputField(
                    modifier = Modifier.fillMaxWidth(),
                    valueState = billAmount,
                    labelId = "Enter Bill Amount",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions{
                        if (!validState){
                            return@KeyboardActions
                        }
                        onValChange(billAmount.value.trim())
                        calculate()
                        keyboardController?.hide()
                    })
                if(validState){
                    Column() {
                        Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                            Text(
                                text = "Split",
                                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(120.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 3.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                                    if (numberOfContributors.value <= 1)
                                        return@RoundIconButton

                                    numberOfContributors.value--
                                    calculate()
                                })
                                Text(text = "${numberOfContributors.value}", modifier = Modifier
                                    .padding(horizontal = 6.dp)
                                    .align(alignment = Alignment.CenterVertically), textAlign = TextAlign.Center)
                                RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                                    numberOfContributors.value++
                                    calculate()
                                })
                            }
                        }

                        Row(modifier = Modifier.padding(horizontal = 6.dp)) {
                            Text(text = "Tip", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                            Spacer(modifier = Modifier.width(200.dp))
                            Text(text = "$${"%.2f".format(tipAmount.value)}", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                        }

                        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "${tipPercentage.value}%", modifier = Modifier.padding(top = 16.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Slider(value = sliderPositionState.value, onValueChange = { newVal ->
                                sliderPositionState.value = newVal
                                tipPercentage.value = (sliderPositionState.value * 100).toInt()
                            }, onValueChangeFinished = {
                                calculate()
                            }, modifier = Modifier.padding(horizontal = 8.dp),
                                steps = 10)
                        }
                    }
                }
            }
        }
    }
}