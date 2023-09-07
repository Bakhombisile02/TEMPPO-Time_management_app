package com.example.opsc_poe

import android.net.Uri
import java.net.URI
import java.net.URL
import java.util.*

//class to store timesheet object
class TimeSheetData(
    var startDate: String? = null,
    var endDate: String? = null,
    var description: String? = null,
    var photo: String? = null,
    var category: String? = null,
    var recordedTime: String? = null
) {
    // No-argument constructor required for Firebase serialization
    constructor() : this(null, null, null, null, null, null)
}
//----------------------------------------End of File ------------------------------------------------------//