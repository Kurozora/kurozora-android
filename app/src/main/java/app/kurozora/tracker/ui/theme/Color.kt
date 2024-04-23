package app.kurozora.tracker.ui.theme

import androidx.compose.ui.graphics.Color

val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

//Black
val Black = Color(0xFF000000)

class Global private constructor() {
    companion object {
        @Volatile
        var shared: Global = Global()
    }

    val background: Color = Color(0xFF353A50)
    val barTintColor: Color = Color(0xFF353A50)
    val barTitleTextColor: Color = Color(0xFFEEEEEE)
    val blurBackgroundColor: Color = Color(0x8F353A50)
    val borderColor: Color = Color(0xFFAFAFAF)
    val textColor: Color = Color(0xFFEEEEEE)
    val textFieldBackgroundColor: Color = Color(0xFF525C70)
    val textFieldTextColor: Color = Color(0xFFEEEEEE)
    val textFieldPlaceholderTextColor: Color = Color(0xFF979DA9)
    val tintColor: Color = Color(0xFFFF9300)
    val tintedBackgroundColor: Color = Color(0xFF50577D)
    val tintedButtonTextColor: Color = Color(0xFFEEEEEE)
    val separatorColor: Color = Color(0xFFC6C5C5)
    val separatorColorLight: Color = Color(0xFF5A5D6D)
    val subTextColor: Color = Color(0xFFAFAFAF)
}

//<color name="gray_200">#FFE8E7E7</color>
//<color name="gray_300">#FFC6C5C5</color>
//<color name="gray_400">#FFAFAFAF</color>
//<color name="gray_500">#FF8F8F8F</color>
//<color name="gray_600">#FF676767</color>
//<color name="gray_700">#FF545454</color>
//<color name="gray_800">#FF353535</color>
//<color name="gray_900">#FF161616</color>
