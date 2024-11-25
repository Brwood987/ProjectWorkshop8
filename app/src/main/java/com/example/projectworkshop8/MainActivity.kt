//written by Ben Wood
//This activity pulls up the forms and and data from the database on the android emulator

package com.example.projectworkshop8

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projectworkshop8.api.ApiClient
import com.example.projectworkshop8.models.Product
import com.example.projectworkshop8.ui.theme.ProjectWorkshop8Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Opens the app directly when the emulator starts
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectWorkshop8Theme {
                MainScreen() // Use MainScreen here to show forms and the product list
            }
        }
    }
}

//refreshes the data whenever something gets manipulated
@Composable
fun MainScreen() {
    var refreshTrigger by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        AddProductForm {
            refreshTrigger++ // Increment trigger to refresh the product list
        }

        UpdateProductForm {
            refreshTrigger++ // Increment trigger to refresh the product list
        }

        DeleteProductForm {
            refreshTrigger++ // Increment trigger to refresh the product list
        }

        ProductList(
            refreshTrigger = refreshTrigger, // Pass the refresh trigger
            modifier = Modifier.fillMaxSize() // Ensure the product list fills the screen
        )
    }
}

//function for the product list
@Composable
fun ProductList(
    refreshTrigger: Int,
    modifier: Modifier = Modifier
) {
    //product variable
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    //loading variable
    var isLoading by remember { mutableStateOf(true) }

    // Fetch products whenever refreshTrigger changes
    LaunchedEffect(refreshTrigger) {
        isLoading = true
        ApiClient.apiService.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    products = response.body() ?: emptyList()
                    Log.d("ProductList", "Products fetched: $products")
                } else {
                    Log.e("ProductList", "Failed to fetch products: ${response.code()}")
                }
                isLoading = false
            }


            //on error produce an error log with a description of what happened
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("ProductList", "API call failed: ${t.message}")
                isLoading = false
            }
        })
    }

    //if loading increase the size of the circle
    if (isLoading) {
        CircularProgressIndicator(modifier = modifier.fillMaxSize())
        //otherwise show the product list
    } else {
        LazyColumn(modifier = modifier) {
            items(products) { product ->
                Text(
                    text = product.ProdName,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

//function for the product form
@Composable
fun AddProductForm(onProductAdded: () -> Unit) {
    //product name
    var productName by remember { mutableStateOf("") }
    //loading variable
    var isLoading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        //text field for the product form
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //submit button
        Button(
            onClick = {
                isLoading = true
                val newProduct = Product(ProductId = 0, ProdName = productName)
                ApiClient.apiService.createProduct(newProduct).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            onProductAdded() // Trigger a callback to refresh the product list
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        isLoading = false
                    }
                })
            },
            enabled = productName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Add Product")
            }
        }
    }
}

//update form
@Composable
fun UpdateProductForm(onProductUpdated: () -> Unit) {
    var productId by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    //text field for product id
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = productId,
            onValueChange = { productId = it },
            label = { Text("Product ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //text field for the name that you would want to change the product to
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("New Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //submit button
        Button(
            onClick = {
                isLoading = true
                val updatedProduct = Product(ProductId = productId.toInt(), ProdName = productName)
                ApiClient.apiService.updateProduct(productId.toInt(), updatedProduct)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            isLoading = false
                            if (response.isSuccessful) {
                                onProductUpdated()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            isLoading = false
                        }
                    })
            },
            enabled = productId.isNotBlank() && productName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Update Product")
            }
        }
    }
}

//delete form
@Composable
fun DeleteProductForm(onProductDeleted: () -> Unit) {
    var productId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    //text field for product id
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = productId,
            onValueChange = { productId = it },
            label = { Text("Product ID to Delete") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        //submit button
        Button(
            onClick = {
                isLoading = true
                ApiClient.apiService.deleteProduct(productId.toInt()).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            onProductDeleted()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        isLoading = false
                    }
                })
            },
            enabled = productId.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Delete Product")
            }
        }
    }
}



