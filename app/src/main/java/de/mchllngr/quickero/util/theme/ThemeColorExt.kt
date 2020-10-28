package de.mchllngr.quickero.util.theme

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun Context.getThemeColor(@AttrRes attributeColor: Int) = TypedValue().also { theme.resolveAttribute(attributeColor, it, true) }.data
