package io.lunarlogic.aircasting.screens.common

import android.graphics.drawable.GradientDrawable

class SelectedSensorBorder: GradientDrawable {
    private val BORDER_WIDTH = 4
    private val CORNER_RADIUS = 20f

    constructor(color: Int): super() {
        setStroke(BORDER_WIDTH, color)
        cornerRadius = CORNER_RADIUS
    }
}
