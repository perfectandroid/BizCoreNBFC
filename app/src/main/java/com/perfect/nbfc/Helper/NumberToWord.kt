package com.perfect.nbfc.Helper


object NumberToWord {
    private var input: String? = null
    private val num: Int = 0
    private val units =
        arrayOf("", " One", " Two", " Three", " Four", " Five", " Six", " Seven", " Eight", " Nine")
    private val teen = arrayOf(
        " Ten",
        " Eleven",
        " Twelve",
        " Thirteen",
        " Fourteen",
        " Fifteen",
        " Sixteen",
        " Seventeen",
        " Eighteen",
        " Nineteen"
    )
    private val tens = arrayOf(
        " Twenty",
        " Thirty",
        " Forty",
        " Fifty",
        " Sixty",
        " Seventy",
        " Eighty",
        " Ninety"
    )
    private val maxs = arrayOf("", "", " Hundred", " Thousand", " Lakh", " Crore")


    fun convertNumberToWords(n: Int): String {
        if (n == 0)
            return "zero"
        input = numToString(n)

        var converted = ""

        var pos = 1

        var hun = false
        while (input!!.length > 0) {
            if (pos == 1)
            // TENS AND UNIT POSITION
            {
                if (input!!.length >= 2)
                // TWO DIGIT NUMBERS
                {

                    val temp = input!!.substring(input!!.length - 2, input!!.length)
                    input = input!!.substring(0, input!!.length - 2)
                    converted += digits(temp)
                } else if (input!!.length == 1)
                // 1 DIGIT NUMBER
                {
                    converted += digits(input!!)

                    input = ""
                }
                pos++
            } else if (pos == 2)
            // HUNDRED POSITION
            {

                val temp = input!!.substring(input!!.length - 1, input!!.length)
                input = input!!.substring(0, input!!.length - 1)
                if (converted.length > 0 && digits(temp) !== "") {
                    /*converted=(digits(temp)+maxs[pos]+" and")+converted;*/
                    converted = digits(temp) + maxs[pos] + " " + converted

                    hun = true
                } else {
                    if (digits(temp) === "")
                    else
                        converted = digits(temp) + maxs[pos] + converted
                    hun = true
                }
                pos++
            } else if (pos > 2)
            // REMAINING NUMBERS PAIRED BY TWO
            {
                if (input!!.length >= 2)
                // EXTRACT 2 DIGITS
                {

                    val temp = input!!.substring(input!!.length - 2, input!!.length)
                    input = input!!.substring(0, input!!.length - 2)
                    if (!hun && converted.length > 0)
                    /*converted=digits(temp)+maxs[pos]+" and"+converted;*/
                        converted = digits(temp) + maxs[pos] + " " + converted
                    else {
                        if (digits(temp) === "")
                        else
                            converted = digits(temp) + maxs[pos] + converted
                    }
                } else if (input!!.length == 1)
                // EXTRACT 1 DIGIT
                {

                    if (!hun && converted.length > 0)
                    /*converted=digits(input)+maxs[pos]+" and"+converted;*/
                        converted = digits(input!!) + maxs[pos] + " " + converted
                    else {
                        if (digits(input!!) === "")
                        else
                            converted = digits(input!!) + maxs[pos] + converted
                        input = ""
                    }
                }
                pos++
            }
        }
        return converted
    }

    private fun digits(temp: String) // TO RETURN SELECTED NUMBERS IN WORDS
            : String {
        var converted = ""
        for (i in temp.length - 1 downTo 0) {
            val ch = temp[i].toInt() - 48
            if (i == 0 && ch > 1 && temp.length > 1)
                converted =
                    tens[ch - 2] + converted // IF TENS DIGIT STARTS WITH 2 OR MORE IT FALLS UNDER TENS
            else if (i == 0 && ch == 1 && temp.length == 2)
            // IF TENS DIGIT STARTS WITH 1 IT FALLS UNDER TEENS
            {
                var sum = 0
                for (j in 0..1)
                    sum = sum * 10 + (temp[j].toInt() - 48)
                return teen[sum - 10]
            } else {
                if (ch > 0)
                    converted = units[ch] + converted
            } // IF SINGLE DIGIT PROVIDED
        }
        return converted
    }

    private fun numToString(x: Int) // CONVERT THE NUMBER TO STRING
            : String {
        var x = x
        var num = ""
        while (x != 0) {
            num = (x % 10 + 48).toChar() + num
            x /= 10
        }
        return num
    }


}
