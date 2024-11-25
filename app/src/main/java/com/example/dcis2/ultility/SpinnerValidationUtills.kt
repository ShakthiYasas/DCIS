package com.example.dcis2.ultility

import android.widget.Spinner
import android.widget.TextView

object SpinnerValidationUtils {

    fun validateSpinnerSelections(spinnerRequiredPairs: List<Pair<Spinner, TextView>>): Boolean {
        var allValid = true
        spinnerRequiredPairs.forEach { (spinner, requiredTextView) ->
            if (spinner.selectedItem == "None") {
                requiredTextView.visibility = TextView.VISIBLE
                allValid = false
            } else {
                requiredTextView.visibility = TextView.GONE
            }
        }
        return allValid
    }
}
