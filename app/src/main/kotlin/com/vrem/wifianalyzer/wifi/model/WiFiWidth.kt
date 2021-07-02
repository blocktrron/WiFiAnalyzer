/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2021 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.vrem.wifianalyzer.wifi.model

import android.net.wifi.ScanResult
import com.vrem.util.buildMinVersionM
import kotlin.math.abs

typealias ChannelWidth = Int

private val channelWidth20: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_20MHZ else 0
private val channelWidth40: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_40MHZ else 1
private val channelWidth80: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_80MHZ else 2
private val channelWidth160: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_160MHZ else 3
private val channelWidth80Plus: ChannelWidth = if (buildMinVersionM()) ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ else 4

typealias CalculateCenter = (primary: Int, center: Int) -> Int

internal val calculateCenter20: CalculateCenter = { primary, _ -> primary }

internal val calculateCenter40: CalculateCenter = { primary, center ->
    if (abs(primary - center) >= WiFiWidth.MHZ_40.frequencyWidthHalf) {
        (primary + center) / 2
    } else {
        center
    }
}

internal val calculateCenter80: CalculateCenter = { _, center -> center }

internal val calculateCenter160: CalculateCenter = { primary, center ->
    when (primary) {
        in 5170..5330 -> 5250
        in 5490..5730 -> 5570
        in 5735..5895 -> 5815
        in 5955..6095 -> 6025
        in 6115..6255 -> 6185
        in 6275..6415 -> 6345
        else -> center
    }
}

enum class WiFiWidth(val channelWidth: ChannelWidth, val frequencyWidth: Int, val guardBand: Int, val calculateCenter: CalculateCenter) {
    MHZ_20(channelWidth20, 20, 2, calculateCenter20),
    MHZ_40(channelWidth40, 40, 3, calculateCenter40),
    MHZ_80(channelWidth80, 80, 3, calculateCenter80),
    MHZ_160(channelWidth160, 160, 3, calculateCenter160),
    MHZ_80_PLUS(channelWidth80Plus, 80, 3, calculateCenter80);

    val frequencyWidthHalf: Int = frequencyWidth / 2

    companion object {
        fun findOne(channelWidth: ChannelWidth): WiFiWidth =
                values().firstOrNull { it.channelWidth == channelWidth } ?: MHZ_20
    }
}
