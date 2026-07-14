package app.kurozora.core.icons

data class AppIconData(
    val identifier: String,
    val displayName: String,
    val categoryKey: String,
    val resourcePath: String,
)

data class AppIconCategory(
    val key: String,
    val displayName: String,
    val icons: List<AppIconData>,
)

private fun icon(
    identifier: String,
    displayName: String,
    categoryKey: String,
    resourcePath: String,
) = AppIconData(identifier, displayName, categoryKey, resourcePath)

fun getAllAppIconCategories(): List<AppIconCategory> = listOf(
    AppIconCategory("default", "Default", listOf(
        icon("default", "Kurozora", "default", "files/icons/Default/Kurozora/Kurozora.webp"),
        icon("kuro-chan", "Kuro-chan", "default", "files/icons/Default/Kuro-chan/kuro-chan.webp"),
        icon("kurozora_day", "Day", "default", "files/icons/Default/Day/kurozora_day.webp"),
        icon("kurozora_night", "Night", "default", "files/icons/Default/Night/kurozora_night.webp"),
    )),
    AppIconCategory("anime", "Anime", listOf(
        icon("monokuro", "Monokuro", "anime", "files/icons/Anime/Monokuro/monokuro.webp"),
    )),
    AppIconCategory("apple", "Apple", listOf(
        icon("6_colors", "6 Colors", "apple", "files/icons/Apple/6 Colors/6_colors.webp"),
        icon("6_colors_inverted", "6 Colors Inverted", "apple", "files/icons/Apple/6 Colors Inverted/6_colors_inverted.webp"),
        icon("ios_6", "iOS 6", "apple", "files/icons/Apple/iOS 6/iOS_6.webp"),
        icon("ios_18", "iOS 18", "apple", "files/icons/Apple/iOS 18/iOS 18.webp"),
        icon("kurozora_connect", "Kurozora Connect", "apple", "files/icons/Apple/Kurozora Connect/kurozora_connect.webp"),
        icon("kurozora_connect_dark", "Kurozora Connect (Dark)", "apple", "files/icons/Apple/Kurozora Connect/kurozora_connect~dark.webp"),
        icon("kurozora_support", "Kurozora Support", "apple", "files/icons/Apple/Kurozora Support/kurozora_support.webp"),
        icon("kurozora_support_inverted", "Kurozora Support Inverted", "apple", "files/icons/Apple/Kurozora Support Inverted/kurozora_support_inverted.webp"),
        icon("kurozora_support_inverted_dark", "Kurozora Support Inverted (Dark)", "apple", "files/icons/Apple/Kurozora Support Inverted/kurozora_support_inverted~dark.webp"),
    )),
    AppIconCategory("desserts", "Desserts", listOf(
        icon("cookiezora", "Cookiezora", "desserts", "files/icons/Desserts/Cookiezora/cookiezora.webp"),
        icon("kuro_caramel", "Kuro Caramel", "desserts", "files/icons/Desserts/Kuro Caramel/kuro_caramel.webp"),
        icon("kurolicious", "Kurolicious", "desserts", "files/icons/Desserts/Kurolicious/kurolicious.webp"),
    )),
    AppIconCategory("event", "Event", listOf(
        icon("eggatha", "Eggatha", "event", "files/icons/Event/Eggatha/eggatha.webp"),
        icon("eggstein", "Eggstein", "event", "files/icons/Event/Eggstein/eggstein.webp"),
        icon("hanabi", "Hanabi", "event", "files/icons/Event/Hanabi/hanabi.webp"),
        icon("john", "John", "event", "files/icons/Event/John/john.webp"),
        icon("john_dark", "John (Dark)", "event", "files/icons/Event/John/john~dark.webp"),
        icon("love_bug", "Love Bug", "event", "files/icons/Event/Love Bug/love_bug.webp"),
        icon("melting_kiss", "Melting Kiss", "event", "files/icons/Event/Melting Kiss/melting_kiss.webp"),
        icon("sweet_return", "Sweet Return", "event", "files/icons/Event/Sweet Return/sweet_return.webp"),
        icon("white_of_crime", "White of Crime", "event", "files/icons/Event/White of Crime/white_of_crime.webp"),
    )),
    AppIconCategory("gems", "Gems", listOf(
        icon("amber", "Amber", "gems", "files/icons/Gems/Amber/amber.webp"),
        icon("amber_dark", "Amber (Dark)", "gems", "files/icons/Gems/Amber/amber~dark.webp"),
        icon("amethyst", "Amethyst", "gems", "files/icons/Gems/Amethyst/amethyst.webp"),
        icon("amethyst_dark", "Amethyst (Dark)", "gems", "files/icons/Gems/Amethyst/amethyst~dark.webp"),
        icon("emerald", "Emerald", "gems", "files/icons/Gems/Emerald/emerald.webp"),
        icon("emerald_dark", "Emerald (Dark)", "gems", "files/icons/Gems/Emerald/emerald~dark.webp"),
        icon("onyx", "Onyx", "gems", "files/icons/Gems/Onyx/onyx.webp"),
        icon("ruby", "Ruby", "gems", "files/icons/Gems/Ruby/ruby.webp"),
        icon("ruby_dark", "Ruby (Dark)", "gems", "files/icons/Gems/Ruby/ruby~dark.webp"),
        icon("sapphire", "Sapphire", "gems", "files/icons/Gems/Sapphire/sapphire.webp"),
        icon("sapphire_dark", "Sapphire (Dark)", "gems", "files/icons/Gems/Sapphire/sapphire~dark.webp"),
    )),
    AppIconCategory("nature", "Nature", listOf(
        icon("fall", "Fall", "nature", "files/icons/Nature/Fall/fall.webp"),
        icon("flame", "Flame", "nature", "files/icons/Nature/Flame/flame.webp"),
        icon("sakura", "Sakura", "nature", "files/icons/Nature/Sakura/sakura.webp"),
        icon("spring", "Spring", "nature", "files/icons/Nature/Spring/spring.webp"),
        icon("summer", "Summer", "nature", "files/icons/Nature/Summer/summer.webp"),
        icon("thunder", "Thunder", "nature", "files/icons/Nature/Thunder/thunder.webp"),
        icon("wind", "Wind", "nature", "files/icons/Nature/Wind/wind.webp"),
        icon("winter", "Winter", "nature", "files/icons/Nature/Winter/winter.webp"),
    )),
    AppIconCategory("premium", "Premium", listOf(
        icon("kurozora_coral", "Coral", "premium", "files/icons/Premium/Coral/kurozora_coral.webp"),
        icon("kurozora_dutch", "Dutch Orange", "premium", "files/icons/Premium/Dutch Orange/kurozora_dutch.webp"),
        icon("kurozora_green", "Green", "premium", "files/icons/Premium/Green/kurozora_green.webp"),
        icon("kurozora_ocean_blue", "Ocean Blue", "premium", "files/icons/Premium/Ocean Blue/kurozora_ocean_blue.webp"),
        icon("kurozora_peach_orange", "Peach Orange", "premium", "files/icons/Premium/Peach Orange/kurozora_peach_orange.webp"),
        icon("kurozora_red", "Red", "premium", "files/icons/Premium/Red/kurozora_red.webp"),
        icon("kurozora_rose_gold", "Rose Gold", "premium", "files/icons/Premium/Rose Gold/kurozora_rose_gold.webp"),
        icon("kurozora_skye_blue", "Sky Blue", "premium", "files/icons/Premium/Sky Blue/kurozora_skye_blue.webp"),
        icon("kurozora_yellow", "Yellow", "premium", "files/icons/Premium/Yellow/kurozora_yellow.webp"),
    )),
    AppIconCategory("special_edition", "Special Edition", listOf(
        icon("kurogram", "Kurogram", "special_edition", "files/icons/Special Edition/Kurogram/kurogram.webp"),
        icon("kuromorphism", "Kuromorphism", "special_edition", "files/icons/Special Edition/Kuromorphism/kuromorphism.webp"),
        icon("kuromorphism_dark", "Kuromorphism (Dark)", "special_edition", "files/icons/Special Edition/Kuromorphism/kuromorphism~dark.webp"),
        icon("kurozora_red_special", "Kurozora (RED)", "special_edition", "files/icons/Special Edition/Kurozora (RED)/kurozora_red.webp"),
        icon("mini_kuroways", "Mini Kuroways", "special_edition", "files/icons/Special Edition/Mini Kuroways/mini_kuroways.webp"),
        icon("monozora", "Monozora", "special_edition", "files/icons/Special Edition/Monozora/monozora.webp"),
        icon("strikeout", "Strikeout", "special_edition", "files/icons/Special Edition/Strikeout/strikeout.webp"),
    )),
    AppIconCategory("trends", "Trends", listOf(
        icon("kurozora_brat", "Brat Green", "trends", "files/icons/Trends/Brat Green/kurozora_brat.webp"),
        icon("gen_z_purple", "Gen Z Purple", "trends", "files/icons/Trends/Gen Z Purple/gen_z_purple.webp"),
        icon("millenial_pink", "Millenial Pink", "trends", "files/icons/Trends/Millenial Pink/millenial_pink.webp"),
    )),
    AppIconCategory("standard", "Standard", listOf(
        icon("kurozora_local", "Kurozora Local", "standard", "files/icons/kurozora_local.webp"),
    )),
)

fun findAppIconByIdentifier(identifier: String): AppIconData? {
    return getAllAppIconCategories().flatMap { it.icons }.find { it.identifier == identifier }
}

fun getDisplayNameForIcon(identifier: String): String {
    return findAppIconByIdentifier(identifier)?.displayName ?: identifier.replaceFirstChar { it.uppercase() }
}
