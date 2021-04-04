package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "election_table")
@Parcelize
data class Election(
        @PrimaryKey var id: Int,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "electionDay") var electionDay: Date,
        @Embedded(prefix = "division_") @Json(name = "ocdDivisionId") var division: Division,
        @ColumnInfo(name = "Saved") var Saved: Boolean = false
) : Parcelable