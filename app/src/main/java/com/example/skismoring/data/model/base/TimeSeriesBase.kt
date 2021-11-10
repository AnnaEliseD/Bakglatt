package com.example.skismoring.data.model.base

// result generated from /json - timeSeries-API

data class TimeSeriesBase(val context: String?, val type: String?, val apiVersion: String?, val license: String?,
                          val createdAt: String?, val queryTime: Number?, val currentItemCount: Number?,
                          val itemsPerPage: Number?, val offset: Number?, val totalItemCount: Number?,
                          val currentLink: String?, val data: List<Data254512069>?)

data class Data254512069(val sourceId: String?, val validFrom: String?, val validTo: String?,
                         val timeOffset: String?, val timeResolution: String?, val timeSeriesId: Number?,
                         val elementId: String?, val unit: String?, val codeTable: String?,
                         val performanceCategory: String?, val exposureCategory: String?,
                         val status: String?, val uri: String?)

data class Level(val levelType: String?, val unit: String?, val value: Number?)

