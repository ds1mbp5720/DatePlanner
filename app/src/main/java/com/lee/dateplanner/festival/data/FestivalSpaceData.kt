package com.lee.dateplanner.festival.data


import com.google.gson.annotations.SerializedName

/**
 * 행사 장소 rest api 저장 data class
 */

data class FestivalSpaceData(
    @SerializedName("culturalSpaceInfo")
    val culturalSpaceInfo: CulturalSpaceInfo
) {
    data class CulturalSpaceInfo(
        @SerializedName("list_total_count")
        val listTotalCount: Int,
        @SerializedName("RESULT")
        val rESULT: RESULT,
        @SerializedName("row")
        val row: List<Row>
    ) {
        data class RESULT(
            @SerializedName("CODE")
            val cODE: String,
            @SerializedName("MESSAGE")
            val mESSAGE: String
        )

        data class Row(
            @SerializedName("ADDR")
            val aDDR: String,
            @SerializedName("AIRPORT")
            val aIRPORT: String,
            @SerializedName("BLUE")
            val bLUE: String,
            @SerializedName("BUSSTOP")
            val bUSSTOP: String,
            @SerializedName("CLOSEDAY")
            val cLOSEDAY: String,
            @SerializedName("ENTR_FEE")
            val eNTRFEE: String,
            @SerializedName("ENTRFREE")
            val eNTRFREE: String,
            @SerializedName("ETC_DESC")
            val eTCDESC: String,
            @SerializedName("FAC_DESC")
            val fACDESC: String,
            @SerializedName("FAC_NAME")
            val fACNAME: String,  // 장소명, 검색떄 사용
            @SerializedName("FAX")
            val fAX: String,
            @SerializedName("GREEN")
            val gREEN: String,
            @SerializedName("HOMEPAGE")
            val hOMEPAGE: String,
            @SerializedName("MAIN_IMG")
            val mAINIMG: String,
            @SerializedName("NUM")
            val nUM: String,
            @SerializedName("OPEN_DAY")
            val oPENDAY: String,
            @SerializedName("OPENHOUR")
            val oPENHOUR: String,
            @SerializedName("PHNE")
            val pHNE: String,
            @SerializedName("RED")
            val rED: String,
            @SerializedName("SEAT_CNT")
            val sEATCNT: String,
            @SerializedName("SUBJCODE")
            val sUBJCODE: String,
            @SerializedName("SUBWAY")
            val sUBWAY: String,
            @SerializedName("X_COORD")
            val xCOORD: String, // 위도
            @SerializedName("Y_COORD")
            val yCOORD: String, // 경도
            @SerializedName("YELLOW")
            val yELLOW: String
        )
    }
}