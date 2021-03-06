package pl.llp.aircasting.screens.session_view

import android.graphics.drawable.GradientDrawable

class SelectedSensorBorder: GradientDrawable {
    private val BORDER_WIDTH = 3
    private val CORNER_RADIUS = 23f

    constructor(color: Int): super() {
        setStroke(BORDER_WIDTH, color)
        cornerRadius = CORNER_RADIUS
    }
}
