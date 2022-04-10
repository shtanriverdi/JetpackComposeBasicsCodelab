package com.example.basicscodelab

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basicscodelab.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // The app theme used inside setContent depends on how your project is named
            BasicsCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MyApp()
                }
            }
        }
    }
}

/*
* However we don't have access to "shouldShowOnboarding" .
* It's clear that we need to share the state that we created in OnboardingScreen with the MyApp composable.
* Instead of somehow sharing the value of the state with its parent,
* we hoist the state–we simply move it to the common ancestor that needs to access it.
* */
@Composable
fun MyApp() {

    // Instead of using remember you can use rememberSaveable.
    // This will save each state surviving configuration changes (such as rotations) and process death.
    var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

    if (shouldShowOnboarding) {
        // Send lambda expression to OnboardingScreen function, when button clicked will mutate our state
        OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
    } else {
        Greetings()
    }
}

@Composable
private fun Greetings(names: List<String> = List(1000) { "$it" }) {
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        items(items = names) { name ->
            Greeting(name = name)
        }
    }
}

@Composable
private fun Greeting(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
    ) {
        CardContent(name)
    }
}

@Composable
fun CardContent(name: String) {
    // You can think of internal state as a private variable in a class.
    // remember is used to guard against recomposition, so the state is not reset.
    var expanded by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(24.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        // Or you can use following 3 lines instead of "weight" modifier
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            /*The weight modifier makes the element fill all available space,
            making it flexible, effectively pushing away the other elements that don't have a weight,
            which are called inflexible. It also makes the fillMaxWidth modifier redundant.
            * */
            modifier = Modifier
                .weight(1f)
            // "coerceAtLeast" Ensures that this value is not less than the specified minimumValue
        ) {
            Text(text = "Hello, ")
            // you can modify a predefined style by using the "copy" function
            Text(
                text = name, style = MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
            if (expanded) Text(text = "GENESIS")
        }
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = if (expanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                }
            )
        }
    }
}

/*
* Compose apps transform data into UI by calling composable functions.
* If your data changes, Compose re-executes these functions with the new data,
* creating an updated UI—this is called "recomposition".
* */

/*
* Composable functions can execute frequently and in any order,
* you must not rely on the ordering in which the code is executed,
* or on how many times this function will be recomposed.
* */

/*
* To add internal state to a composable, you can use the "mutableStateOf" function,
* which makes Compose recompose functions that read that "State".
* "State" and "MutableState" are interfaces that hold some value and trigger UI updates (recompositions)
* whenever that value changes.
* To preserve state across recompositions, remember the mutable state using "remember".
* */

@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
    // shouldShowOnboarding is using a "by" keyword instead of the "=".
    // This is a property delegate that saves you from typing .value every time.
    var shouldShowOnboarding by remember { mutableStateOf(true) }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to the Basics Codelab!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked
            ) {
                Text("Continue")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Composable
fun DefaultPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}
