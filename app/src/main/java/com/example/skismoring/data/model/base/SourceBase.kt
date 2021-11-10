package com.example.skismoring.data.model.base


// result generated from /json - sources-API

data class SourceBase(val context: String?, val type: String?, val apiVersion: String?, val license: String?,
                      val createdAt: String?, val queryTime: String?, val currentItemCount: Number?,
                      val itemsPerPage: Number?, val offset: Number?, val totalItemCount: Number?,
                      val nextLink: String?, val previousLink: String?, val currentLink: String?, val data: List<Sensor>?)

data class Sensor(val type: String?, val id: String?, val name: String?, val shortName: String?,
                  val country: String?, val countryCode: String?, val wmoId: String?, val geometry: Geometry?,
                  val distance: String?, val masl: String?, val validFrom: String?, val validTo: String?,
                  val county: String?, val countyId: String?, val municipality: String?,
                  val municipalityId: String?, val stationHolders: List<String?>, val externalIds: List<String?>,
                  val icaoCodes: String?, val shipCodes: String?, val wigosId: String?)

data class Geometry(val type: String?, val coordinates: List<String?>)



