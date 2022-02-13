package com.su.mediabox.util.comparator

import com.su.mediabox.plugin.standard.been.AnimeCoverBean


/**
 * 比较集数名称(title)的字典序，数字按照从大到小，例如90小于100
 * 例：第10集<第11集<第11.5集<第90集<第100集
 */
class EpisodeTitleComparator : Comparator<AnimeCoverBean> {

    // 计算出数字的结尾下标，可以包括一个小数点
    private fun findDigitEndIndex(arrChar: String, at: Int): Int {
        var k = at
        var c: Char
        var hasDot = false
        while (k < arrChar.length) {
            c = arrChar[k]
            if (c == '.' && !hasDot) hasDot = true
            else if (c > '9' || c < '0') break
            k++
        }
        return k
    }

    override fun compare(o1: AnimeCoverBean, o2: AnimeCoverBean): Int {
        val a: String = o1.title
        val b: String = o2.title
        var aIndex = 0
        var bIndex = 0
        var aComparedUnitEndIndex: Int
        var bComparedUnitEndIndex: Int
        while (aIndex < a.length && bIndex < b.length) {
            // 找a串的数字结束下标+1
            aComparedUnitEndIndex = findDigitEndIndex(a, aIndex)
            // 找b串的数字结束下标+1
            bComparedUnitEndIndex = findDigitEndIndex(b, bIndex)
            // 如果a和b数字的结束下标都增加了，则说明之前开始找的地方是数字，开始比较数字
            if (aComparedUnitEndIndex > aIndex && bComparedUnitEndIndex > bIndex) {
                // 用BigDecimal比较，防止浮点数出现精度问题
                val aDigit = a.substring(aIndex, aComparedUnitEndIndex).toBigDecimal()  // a数
                val bDigit = b.substring(bIndex, bComparedUnitEndIndex).toBigDecimal()  // b数
                // 如果a数!=b数，则返回其差值
                aDigit.compareTo(bDigit).let { if (it != 0) return it }
                // 如果a数==b数，则继续比较
                aIndex = aComparedUnitEndIndex
                bIndex = bComparedUnitEndIndex
            } else {
                if (a[aIndex] != b[bIndex]) return a[aIndex] - b[bIndex]
                aIndex++
                bIndex++
            }
        }
        return a.length - b.length
    }
}
