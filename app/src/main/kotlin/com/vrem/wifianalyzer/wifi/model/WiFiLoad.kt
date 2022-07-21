/*
 * WiFiAnalyzer
 * Copyright (C) 2015 - 2022 VREM Software Development <VREMSoftwareDevelopment@gmail.com>
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
import com.vrem.util.buildMinVersionR
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.math.abs

data class WiFiLoad(
        val clients: Int = -1,
        val load: Int = -1) {
    constructor(informationElements: List<ScanResult.InformationElement>) :
        this(parseClientCount(informationElements), parseChannelLoad(informationElements));

    companion object {
        val EMPTY = WiFiLoad()

        private fun getLoadElement(informationElements: List<ScanResult.InformationElement>) : ByteBuffer? {
            if (!buildMinVersionR())
                return null;

            val ie = informationElements.firstOrNull { e -> e.id.equals(11) }

            if (ie == null || ie.bytes.remaining() < 5)
                return null

            return ie.bytes
        }

        private fun parseClientCount(informationElements: List<ScanResult.InformationElement>): Int {
            val first = getLoadElement(informationElements) ?: return -1

            val b = byteArrayOf(first.get(0), first.get(1))
            val bb = ByteBuffer.wrap(b)
            bb.order(ByteOrder.LITTLE_ENDIAN);

            return bb.getShort(0).toUShort().toInt();
        }

        private fun parseChannelLoad(informationElements: List<ScanResult.InformationElement>): Int {
            val first = getLoadElement(informationElements) ?: return -1;
            return first.get(2).and(0xff.toByte()).toUInt().toInt() * 100 / 255
        }
    }
}
