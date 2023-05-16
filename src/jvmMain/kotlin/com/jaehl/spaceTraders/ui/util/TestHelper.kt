package com.jaehl.spaceTraders.ui.util


object TestHelper {
    fun createEnumString() : String {
        var logString = ""
        listOf<String>(
            "COSMIC",
            "VOID",
            "GALACTIC",
            "QUANTUM",
            "DOMINION"


        ).forEach { key ->
            var name = ""
            var title = ""
            val list = key.split("_")
            list.forEachIndexed { index, it ->
                if(it == "I"){
                    title += "1"
                    name += "1"
                }
                else if(it == "II"){
                    title += "2"
                    name += "2"
                }
                else if(it == "III"){
                    title += "3"
                    name += "3"
                }
                else {
                    title += it.lowercase().replaceFirstChar(Char::toTitleCase)
                    name += "${it.lowercase().replaceFirstChar(Char::toTitleCase)}"
                    if(index != (list.size -1)) {
                        name += ""
                    }
                }
            }

            logString += "\n@SerializedName(\"${key}\")"
            logString += "\n$title(\"$name\"),\n"
        }
        return logString
    }
}