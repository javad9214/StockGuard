package com.example.composetrainer.utils.populatedbfortest

import android.content.Context
import com.example.composetrainer.domain.model.ProductJson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadProductJson(context: Context): List<ProductJson> {
    val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<ProductJson>>() {}.type
    return Gson().fromJson(json, type)
}