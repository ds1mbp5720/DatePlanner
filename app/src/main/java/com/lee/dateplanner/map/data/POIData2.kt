package com.lee.dateplanner.map.data


import com.google.gson.annotations.SerializedName

data class POIData2(
    val documents: List<Document>,
    val meta: Meta
) {
    data class Document(
        @SerializedName("address_name")
        val addressName: String,
        @SerializedName("category_group_code")
        val categoryGroupCode: String,
        @SerializedName("category_group_name")
        val categoryGroupName: String,
        @SerializedName("category_name")
        val categoryName: String,
        val distance: String,
        val id: String,
        val phone: String,
        @SerializedName("place_name")
        val placeName: String,
        @SerializedName("place_url")
        val placeUrl: String,
        @SerializedName("road_address_name")
        val roadAddressName: String,
        val x: String,
        val y: String
    )

    data class Meta(
        @SerializedName("is_end")
        val isEnd: Boolean,
        @SerializedName("pageable_count")
        val pageableCount: Int,
        @SerializedName("same_name")
        val sameName: Any,
        @SerializedName("total_count")
        val totalCount: Int
    )
}