package com.example.studentregistration
import android.content.Context

import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val api = RetrofitClient.apiService

        api.getUser().enqueue(object : retrofit2.Callback<User> {

            override fun onResponse(
                call: retrofit2.Call<User>,
                response: retrofit2.Response<User>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()

                    Toast.makeText(
                        this@MainActivity,
                        "User: ${user?.name}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: retrofit2.Call<User>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "API Failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable ("login"){
                    LoginScreen(navController)
                }

                composable("register") {
                    RegistrationScreen(navController)
                }

                composable(
                    route = "success/{name}/{email}/{phone}",
                    arguments = listOf(
                        navArgument("name") { type = NavType.StringType },
                        navArgument("email") { type = NavType.StringType },
                        navArgument("phone") { type = NavType.StringType }
                    )
                ) { backStackEntry ->

                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val email = backStackEntry.arguments?.getString("email") ?: ""
                    val phone = backStackEntry.arguments?.getString("phone") ?: ""

                    SuccessScreen(
                        name = name,
                        email = email,
                        phone = phone,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(navController: NavHostController) {
    val context=LocalContext.current
    var userInfo by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Student Registration",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                if (name.isNotEmpty() &&
                    email.isNotEmpty() &&
                    phone.isNotEmpty()
                ) {

                    Toast.makeText(
                        context,
                        "Registration Successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.navigate(
                        "success/$name/$email/$phone"
                    )

                } else {

                    Toast.makeText(
                        context,
                        "Fill all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {

            RetrofitClient.apiService.getUser()
                .enqueue(object : Callback<User> {

                    override fun onResponse(
                        call: Call<User>,
                        response: Response<User>
                    ) {

                        val user = response.body()

                        userInfo =
                            "Name: ${user?.name}\nEmail: ${user?.email}"
                    }

                    override fun onFailure(
                        call: Call<User>,
                        t: Throwable
                    ) {

                        userInfo = "GET Failed"
                    }
                })

        }) {
            Text("GET")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text=userInfo
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            postUser(context)
        }) {
            Text("POST")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            updateUser(context)
        }) {
            Text("PUT")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            deleteUser(context)
        }) {
            Text("DELETE")
        }
    }
}


@Composable
fun SuccessScreen(
    name: String,
    email: String,
    phone: String,
    navController: NavHostController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Registration Successful",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Name: $name")
        Text("Email: $email")
        Text("Phone: $phone")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text("Back")
        }
    }
}
fun postUser(context: Context) {

    val user = User(
        name = "Vishnu",
        email = "vishnu@gmail.com"
    )

    RetrofitClient.apiService.createUser(user)
        .enqueue(object : Callback<User> {

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {

              val createdUser=response.body()
                Toast.makeText(
                    context,
                    "Created User:${
                        createdUser?.name
                    }",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFailure(
                call: Call<User>,
                t: Throwable
            ) {

                Toast.makeText(
                    context,
                    "POST Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
}





fun updateUser(context: Context) {

    val user = User(
        id = 1,
        name = "Updated Vishnu",
        email = "updated@gmail.com"
    )

    RetrofitClient.apiService.updateUser(
        1,
        user
    ).enqueue(object : Callback<User> {

        override fun onResponse(
            call: Call<User>,
            response: Response<User>
        ) {

            val updatedUser=response.body()
            Toast.makeText(
                context,
                "Updated User:${
                    updatedUser?.name
                }",
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onFailure(
            call: Call<User>,
            t: Throwable
        ) {

            Toast.makeText(
                context,
                "PUT Failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    })
}







fun deleteUser(context: Context) {

    RetrofitClient.apiService.deleteUser(1)
        .enqueue(object : Callback<Void> {

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {

                Toast.makeText(
                    context,
                    "User Deleted Successfully",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onFailure(
                call: Call<Void>,
                t: Throwable
            ) {

                Toast.makeText(
                    context,
                    "DELETE Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
}