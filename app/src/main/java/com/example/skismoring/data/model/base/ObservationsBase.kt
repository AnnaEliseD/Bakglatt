package com.example.skismoring.data.model.base


// result generated from /json - observations-API

data class ObservationsBase(val context: String?, val type: String?, val apiVersion: String?, val license: String?,
                           val createdAt: String?, val queryTime: String?, val currentItemCount: Number?,
                           val itemsPerPage: Number?, val offset: Number?, val totalItemCount: Number?,
                           val nextLink: String?, val previousLink: String?, val currentLink: String?, val data: List<Data1808906512>?)

data class Data1808906512(val sourceId: String?, val geometry: Geometry?, val referenceTime: String?,
                          val observations: List<Observations>?)

data class Level2(val levelType: String?, val unit: String?, val value: String?)

data class Observations(val elementId: String?, val value: String?, val origValue: String?,
                        val unit: String?, val codeTable: String?, val level: Level2?,
                        val timeOffset: String?, val timeResolution: String?, val timeSeriesId: String?,
                        val performanceCategory: String?, val exposureCategory: String?,
                        val qualityCode: String?, val controlInfo: String?, val dataVersion: String?)
