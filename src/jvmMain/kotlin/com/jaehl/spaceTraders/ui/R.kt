package com.jaehl.spaceTraders.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

object R {
    object Color {
        val dialogBackground = Color(0x8f000000)

        val rowBackgroundOdd = Color(0xffffffff)
        val rowBackgroundEven = Color(0xffededed)

//        val textDark = Color(0xff000000)
//        val textLight = Color(0xffffffff)

        val primary50 = Color(0xffE9F5E7)
        val primary100 = Color(0xffCAE6C4)
        val primary200 = Color(0xffA8D79E)
        val primary300 = Color(0xff84C876)
        val primary400 = Color(0xff6ABC58)
        val primary500 = Color(0xff50B038)
        val primary600 = Color(0xff47A130)
        val primary700 = Color(0xff3A8F25)
        val primary800 = Color(0xff2E7E1A)
        val primary900 = Color(0xff156004)

        object OnPrimary {
            val highEmphasis = Color(0xffffffff)
            val mediumEmphasis = Color(0xBAffffff)
            val disabled = Color(0x5Effffff)
        }

        val secondary50  = Color(0xffF2E6F4)
        val secondary100  = Color(0xffDFBFE4)
        val secondary200  = Color(0xffCC96D4)
        val secondary300  = Color(0xffB76DC2)
        val secondary400  = Color(0xffA74FB4)
        val secondary500  = Color(0xff9834A7)
        val secondary600  = Color(0xff8A30A1)
        val secondary700  = Color(0xff782A98)
        val secondary800  = Color(0xff682690)
        val secondary900  = Color(0xff4B1E7F)

        object OnSecondary {
            val highEmphasis = Color(0xffffffff)
            val mediumEmphasis = Color(0xBAffffff)
            val disabled = Color(0x5Effffff)
        }

        val neutral50  = Color(0xffF8FFF7)
        val neutral100  = Color(0xffECF0EB)
        val neutral200  = Color(0xffD1D6D0)
        val neutral300  = Color(0xffB6BDB5)
        val neutral400  = Color(0xff9CA39B)
        val neutral500  = Color(0xff828A80)
        val neutral600  = Color(0xff6C706B)
        val neutral700  = Color(0xff4E574C)
        val neutral800  = Color(0xff2E3D2A)
        val neutral900  = Color(0xff1B2419)

        val surface = Color(0xffffffff)

        object OnSurface {
            val highEmphasis = Color(0xDB000000)
            val mediumEmphasis = Color(0x96000000)
            val disabled = Color(0x4D000000)
        }

        val background = neutral100

        object OnBackground {
            val highEmphasis = Color(0xDB000000)
            val mediumEmphasis = Color(0x96000000)
            val disabled = Color(0x4D000000)
        }

        val error50 = Color(0xffF2B8B8)
        val error300 = Color(0xffF78B8B)
        val error500 = Color(0xffE35252)
        val error700 = Color(0xffD82323)
        val error900 = Color(0xffBF1F1F)

        object OnError {
            val highEmphasis = Color(0xffffffff)
            val mediumEmphasis = Color(0xBAffffff)
            val disabled = Color(0x5Effffff)
        }

//        object Primary {
//            val background = Color(0xff596157)
//            val content = textLight
//        }
//        object Secondary {
//            val background = Color(0xff5B8C5A)
//            val content = textLight
//        }
//
//        object Tertiary {
//            val background = Color(0xffCFD186)
//            val content = textDark
//        }
//
//        object Warning {
//            val background = Color(0xffE3655B)
//            val content = textLight
//        }

        object Disabled {
            val background = Color(0xffbababa)
            val content = Color(0xff4a4a4a)
        }

        val dividerColor = Color(0xffadadad)

        val pageBackground = Color(0xffededed)
        val cardBackground = Color(0xffffffff)
        val cardTitleBackground = Color(0xffdedede)

        val errorText = Color(0xffc23838)

        val transparent = Color(0x00000000)

        //val disabledBackground = Color(0xffbababa)

        val codeBlock = Color(0xffe0e0e0)

        val rowBackground = Color(0x00000000)
        val rowText = OnSurface.highEmphasis
        val rowHoverBackground = Color(0x11000000)
        val rowHoverText = Color(0xff000000)

        val rowSelectedBackground = secondary500
        val rowSelectedText = OnSecondary.highEmphasis

//        object Button {
//            var background = lightColors.primary
//            var text = OnPrimary.highEmphasis
//        }

//        object ButtonOutlined {
//            var border = Secondary.background
//            var text = Secondary.background
//        }

//        object ButtonDelete {
//            var background = Warning.background
//            var text = Warning.content
//        }

        object TopAppBar {
            var background = primary600
            var text = OnPrimary.highEmphasis
        }

        object Card {
            object SubTitle {
                var background = primary500
                var text = OnPrimary.highEmphasis
            }
        }

        object OutlinedTextField {
            val focusedBorderColor = primary500
            val focusedLabelColor = primary500
        }
    }

    object Dimensions {
        val buttonFontSize = 14.sp
        val navItemFontSize = 14.sp
    }

    val lightColors = Colors(
        primary= Color.primary600,
        primaryVariant= Color.secondary800,
        secondary = Color.secondary500,
        secondaryVariant = Color.secondary800,
        background= Color.background,
        surface = Color.surface,
        error = Color.error700,
        onPrimary = Color.OnPrimary.highEmphasis,
        onSecondary = Color.OnSecondary.highEmphasis,
        onBackground = Color.OnBackground.highEmphasis,
        onSurface = Color.OnSurface.highEmphasis,
        onError = Color.OnError.highEmphasis,
        isLight = true
    )
}