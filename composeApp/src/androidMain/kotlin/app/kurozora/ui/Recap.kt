//package app.kurozora.ui
//
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.Spring
//import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.spring
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.aspectRatio
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.pager.PagerDefaults
//import androidx.compose.foundation.pager.VerticalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.State
//import androidx.compose.runtime.derivedStateOf
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.draw.blur
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.ColorMatrix
//import androidx.compose.ui.graphics.DefaultAlpha
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.decodeToImageBitmap
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.util.lerp
//import coil3.compose.AsyncImage
//import io.kamel.image.KamelImage
//import io.kamel.image.asyncPainterResource
//import kotlinx.coroutines.delay
//import kurozora.composeapp.generated.resources.Res
//import kotlin.math.PI
//import kotlin.math.absoluteValue
//import kotlin.random.Random
//
//// ─────────────────────────────────────────────────────────────
////  DATA MODELS  (kurozorakit models mapped for the UI layer)
//// ─────────────────────────────────────────────────────────────
//
//data class RecapMediaItem(
//    val rank: Int,
//    val title: String,
//    val subtitle: String,          // originalTitle or tagline
//    val posterUrl: String?,
//    val accentColor: Color,        // parsed from library/stats or a default palette
//)
//
//data class RecapGenreItem(
//    val name: String,
//    val symbol: String?,           // emoji / symbol URL
//    val color1: Color,             // backgroundColor1
//    val color2: Color,             // backgroundColor2
//)
//
//data class RecapThemeItem(
//    val name: String,
//    val symbol: String?,
//    val color1: Color,
//    val color2: Color,
//)
//
///**
// * Top-level data class for the whole Re:CAP screen.
// * Built from [RecapItem] + its relationships (shows, literatures, genres, themes).
// */
//data class RecapScreenData(
//    val year: Int,
//    val type: String,               // "SHOWS" | "LITERATURES"
//    val totalSeriesCount: Int,
//    val totalPartsCount: Int,       // episodes or chapters
//    val totalPartsDuration: Int,    // minutes
//    val topPercentile: Double,
//
//    val topShows: List<RecapMediaItem>,
//    val topLiteratures: List<RecapMediaItem>,
//    val topGenres: List<RecapGenreItem>,
//    val topThemes: List<RecapThemeItem>,
//)
//
//// ─────────────────────────────────────────────────────────────
////  PALETTE HELPERS
//// ─────────────────────────────────────────────────────────────
//
//private val defaultShowPalette = listOf(
//    Color(0xFFE85D04), Color(0xFF7B2FBE), Color(0xFFF9C74F),
//    Color(0xFF4A5568), Color(0xFF48CAE4),
//)
//private val defaultLitPalette = listOf(
//    Color(0xFFF77F00), Color(0xFF48CAE4), Color(0xFFF9C74F), Color(0xFF9B2335),
//)
//
//// ─────────────────────────────────────────────────────────────
////  ANIMATED COUNTER
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun rememberCounterValue(target: Int, enabled: Boolean, durationMs: Int = 2000): State<Int> {
//    val animatable = remember { Animatable(0f) }
//    LaunchedEffect(enabled) {
//        if (enabled) {
//            animatable.animateTo(
//                target.toFloat(),
//                animationSpec = tween(durationMs, easing = FastOutSlowInEasing)
//            )
//        }
//    }
//    return remember { derivedStateOf { animatable.value.toInt() } }
//}
//
//// ─────────────────────────────────────────────────────────────
////  STAR FIELD
//// ─────────────────────────────────────────────────────────────
//
//private data class Star(val x: Float, val y: Float, val radius: Float)
//
//@Composable
//private fun StarField(modifier: Modifier = Modifier) {
//    val infiniteTransition = rememberInfiniteTransition(label = "stars")
//    val alpha by infiniteTransition.animateFloat(
//        initialValue = 0f, targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            tween(3000, easing = LinearEasing), RepeatMode.Reverse
//        ), label = "starAlpha"
//    )
//
//    val starCount = 60
//    val stars = remember {
//        List(starCount) { i ->
//            Star(
//                x = Random.nextFloat(),
//                y = Random.nextFloat(),
//                radius = if (i % 5 == 0) 3f else 1.5f
//            )
//        }
//    }
//
//    Canvas(modifier = modifier) {
//        stars.forEachIndexed { i, star ->
//            drawCircle(
//                color = Color.White,
//                radius = star.radius,
//                center = Offset(star.x * size.width, star.y * size.height),
//                alpha = 0.1f + (alpha * 0.5f * ((i % 10) / 10f))
//            )
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 0 — CINEMATIC HERO
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideHero(year: Int, active: Boolean) {
//    val logoScale by animateFloatAsState(
//        targetValue = if (active) 1f else 0.5f,
//        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "logoScale"
//    )
//    val logoAlpha by animateFloatAsState(
//        targetValue = if (active) 1f else 0f,
//        animationSpec = tween(800), label = "logoAlpha"
//    )
//    val titleOffset by animateDpAsState(
//        targetValue = if (active) 0.dp else 40.dp,
//        animationSpec = tween(1000, 400), label = "titleOffset"
//    )
//    val titleAlpha by animateFloatAsState(
//        targetValue = if (active) 1f else 0f,
//        animationSpec = tween(1000, 400), label = "titleAlpha"
//    )
//
//    var logo: ByteArray? by remember { mutableStateOf(null) }
//    LaunchedEffect(Unit) {
//        logo = Res.readBytes("files/static/icon/logo.webp")
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF050810)),
//        contentAlignment = Alignment.Center
//    ) {
//        StarField(Modifier.matchParentSize())
//
//        // Glow orbs
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(Color(0xFF63B3ED).copy(0.08f), Color.Transparent),
//                    center = Offset(size.width / 2, size.height * 0.1f),
//                    radius = 400f
//                ),
//                radius = 400f, center = Offset(size.width / 2, size.height * 0.1f)
//            )
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(Color(0xFFFF6432).copy(0.06f), Color.Transparent),
//                    center = Offset(size.width * -0.1f, size.height * 1.1f),
//                    radius = 350f
//                ),
//                radius = 350f, center = Offset(size.width * -0.1f, size.height * 1.1f)
//            )
//        }
//
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier.padding(horizontal = 28.dp)
//        ) {
//            logo?.decodeToImageBitmap()?.let {
//                Image(
//                    bitmap = it,
//                    contentDescription = "Kurozora Logo",
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(RoundedCornerShape(16.dp))
//                        .shadow(
//                            elevation = 24.dp, shape = RoundedCornerShape(24.dp),
//                            ambientColor = Color(0xFF7B2FBE), spotColor = Color(0xFF7B2FBE)
//                        ),
//                    contentScale = ContentScale.Crop,
//                    alpha = DefaultAlpha,
//                    alignment = Alignment.Center
//                )
//            }
//            Spacer(Modifier.height(20.dp))
//
//            Text(
//                text = "KUROZORA · RE:CAP",
//                color = Color.White.copy(0.4f),
//                fontSize = 11.sp,
//                fontWeight = FontWeight.Medium,
//                letterSpacing = 6.sp,
//                modifier = Modifier.alpha(logoAlpha)
//            )
//
//            Spacer(Modifier.height(16.dp))
//
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier
//                    .offset(y = titleOffset)
//                    .alpha(titleAlpha)
//            ) {
//                Text("Your", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Black, lineHeight = 52.sp)
//                Text(
//                    text = "$year",
//                    fontSize = 72.sp,
//                    fontWeight = FontWeight.Black,
//                    lineHeight = 64.sp,
//                    style = TextStyle(
//                        brush = Brush.linearGradient(
//                            listOf(Color(0xFFF5C518), Color(0xFFF5C518))
//                        )
//                    )
//                )
//                Text("Recap", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.Black, lineHeight = 52.sp)
//            }
//
//            Spacer(Modifier.height(24.dp))
//
//            Text(
//                text = "SWIPE TO RELIVE YOUR YEAR ↓",
//                color = Color.White.copy(0.35f),
//                fontSize = 10.sp,
//                letterSpacing = 2.sp,
//                modifier = Modifier.alpha(logoAlpha)
//            )
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 1 — TOP ANIME / SHOWS
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideTopShows(items: List<RecapMediaItem>, totalCount: Int) {
//    var activeIndex by remember { mutableStateOf(0) }
//    val cur = items.getOrNull(activeIndex) ?: return
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val bgAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(600), label = "showBg")
//    val titleOffset by animateDpAsState(targetValue = if (visible) 0.dp else 20.dp, animationSpec = tween(700, 100), label = "showTitle")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        // Dynamic BG
//        Box(
//            modifier = Modifier
//                .matchParentSize()
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(Color(0xFF080C14), Color(0xFF0D1220)),
//                        start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY)
//                    )
//                )
//        )
//        // Blurred poster color glow
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(
//                color = cur.accentColor.copy(0.13f),
//                radius = size.width * 0.6f,
//                center = Offset(size.width * 0.3f, size.height * 0.5f)
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 28.dp, vertical = 56.dp)
//        ) {
//            // Section label
//            Text(
//                text = "TOP ANIME · $totalCount SERIES",
//                color = Color.White.copy(0.35f),
//                fontSize = 10.sp,
//                letterSpacing = 4.sp,
//                modifier = Modifier.alpha(bgAlpha)
//            )
//            Spacer(Modifier.height(8.dp))
//
//            // Headline
//            Column(modifier = Modifier.offset(y = titleOffset).alpha(bgAlpha)) {
//                Text("Your most", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
//                Text("watched", color = cur.accentColor, fontSize = 36.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
//                Text("anime", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
//            }
//
//            Spacer(Modifier.height(28.dp))
//
//            // Big active card
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .alpha(bgAlpha)
//                    .fillMaxWidth()
//            ) {
//                Box {
////                    AsyncImage(
////                        model = cur.posterUrl,
////                        contentDescription = cur.title,
////                        contentScale = ContentScale.Crop,
////                        modifier = Modifier
////                            .width(110.dp)
////                            .aspectRatio(2f / 3f)
////                            .clip(RoundedCornerShape(16.dp))
////                            .shadow(elevation = 24.dp, shape = RoundedCornerShape(16.dp),
////                                ambientColor = cur.accentColor, spotColor = cur.accentColor)
////                    )
//                    KamelImage({ asyncPainterResource(cur.posterUrl.orEmpty()) },
//                        contentDescription = cur.title,
//                        modifier = Modifier
//                            .width(110.dp)
//                            .aspectRatio(2f / 3f)
//                            .clip(RoundedCornerShape(16.dp))
//                            .shadow(
//                                elevation = 24.dp,
//                                shape = RoundedCornerShape(16.dp),
//                                ambientColor = cur.accentColor,
//                                spotColor = cur.accentColor
//                            ),
//                        alignment = Alignment.Center,
//                        contentScale = ContentScale.Crop,
//                        alpha = DefaultAlpha,
//                        contentAlignment = Alignment.Center
//                    )
//                    // Rank badge
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier
//                            .offset((-8).dp, (-8).dp)
//                            .size(34.dp)
//                            .background(cur.accentColor, RoundedCornerShape(10.dp))
//                    ) {
//                        Text("#${cur.rank}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
//                    }
//                }
//                Spacer(Modifier.width(20.dp))
//                Column {
//                    Text(cur.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                    Spacer(Modifier.height(4.dp))
//                    Text(cur.subtitle, color = Color.White.copy(0.45f), fontSize = 11.sp, letterSpacing = 1.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                    Spacer(Modifier.height(12.dp))
//                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                        repeat(5) { i ->
//                            Box(
//                                modifier = Modifier
//                                    .size(6.dp)
//                                    .clip(CircleShape)
//                                    .background(if (i < cur.rank) Color.White.copy(0.15f) else cur.accentColor)
//                            )
//                        }
//                    }
//                }
//            }
//
//            Spacer(Modifier.height(24.dp))
//
//            // Selector pills
//            items.forEachIndexed { i, item ->
//                val isActive = i == activeIndex
//                val pillAlpha by animateFloatAsState(
//                    targetValue = if (visible) 1f else 0f,
//                    animationSpec = tween(500, (300 + i * 70)),
//                    label = "pill$i"
//                )
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .alpha(pillAlpha)
//                        .padding(bottom = 8.dp)
//                        .clip(RoundedCornerShape(40.dp))
//                        .background(if (isActive) item.accentColor else Color.White.copy(0.05f))
//                        .border(
//                            width = if (isActive) 0.dp else 1.dp,
//                            color = Color.White.copy(0.08f),
//                            shape = RoundedCornerShape(40.dp)
//                        )
//                        .clickable { activeIndex = i }
//                        .padding(horizontal = 14.dp, vertical = 8.dp),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    KamelImage({ asyncPainterResource(item.posterUrl.orEmpty()) },
//                        contentDescription = item.title,
//                        modifier = Modifier
//                            .size(22.dp)
//                            .clip(RoundedCornerShape(6.dp)),
//                        alignment = Alignment.Center,
//                        contentScale = ContentScale.Crop,
//                        alpha = DefaultAlpha,
//                        contentAlignment = Alignment.Center
//                    )
//                    Text(
//                        item.title,
//                        color = if (isActive) Color.White else Color.White.copy(0.45f),
//                        fontSize = 11.sp,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 2 — TOP MANGA / LITERATURES
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideTopLiteratures(items: List<RecapMediaItem>, totalCount: Int) {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val bgAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(600), label = "litBg")
//    val titleOffset by animateDpAsState(targetValue = if (visible) 0.dp else 24.dp, animationSpec = tween(700, 100), label = "litTitle")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF08040F))
//    ) {
//        // Ambient glows
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(Color(0xFF7B2FBE).copy(0.2f), size.width * 0.6f, Offset(size.width * 0.7f, size.height * 0.2f))
//            drawCircle(Color(0xFFF72585).copy(0.15f), size.width * 0.5f, Offset(size.width * 0.1f, size.height * 0.8f))
//        }
//
//        // Floating bg posters
//        items.take(4).forEachIndexed { i, item ->
//            val leftPcts = listOf(0.05f, 0.55f, 0.20f, 0.65f)
//            val topPcts  = listOf(0.10f, 0.05f, 0.55f, 0.50f)
//            val rots     = listOf(-8f, 6f, -4f, 10f)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(0.22f)
//                    .align(Alignment.TopStart)
//                    .offset(
//                        x = (leftPcts[i] * 360).dp,
//                        y = (topPcts[i] * 700).dp
//                    )
//                    .graphicsLayer { rotationZ = rots[i] }
//                    .alpha(0.07f + i * 0.01f)
//                    .blur(2.dp)
//            ) {
//                KamelImage({ asyncPainterResource(item.posterUrl.orEmpty()) },
//                    contentDescription = null,
//                    modifier = Modifier
//                        .aspectRatio(2f / 3f)
//                        .clip(RoundedCornerShape(8.dp)),
//                    alignment = Alignment.Center,
//                    contentScale = ContentScale.Crop,
//                    alpha = DefaultAlpha,
//                    contentAlignment = Alignment.Center
//                )
//            }
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 28.dp, vertical = 64.dp)
//        ) {
//            Text(
//                "TOP MANGA · $totalCount SERIES",
//                color = Color.White.copy(0.35f),
//                fontSize = 10.sp,
//                letterSpacing = 4.sp,
//                modifier = Modifier.alpha(bgAlpha)
//            )
//            Spacer(Modifier.height(8.dp))
//
//            Column(modifier = Modifier.offset(y = titleOffset).alpha(bgAlpha)) {
//                Text("Pages that", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp)
//                Text("kept you", color = Color(0xFFC77DFF), fontSize = 34.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp)
//                Text("up at night", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, lineHeight = 34.sp)
//            }
//
//            Spacer(Modifier.height(32.dp))
//
//            items.forEachIndexed { i, item ->
//                val itemAlpha by animateFloatAsState(
//                    targetValue = if (visible) 1f else 0f,
//                    animationSpec = tween(600, (150 + i * 100)),
//                    label = "litItem$i"
//                )
//                val itemOffset by animateDpAsState(
//                    targetValue = if (visible) 0.dp else if (i % 2 == 0) (-40).dp else 40.dp,
//                    animationSpec = tween(600, (150 + i * 100)),
//                    label = "litItemOff$i"
//                )
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .alpha(itemAlpha)
//                        .offset(x = itemOffset)
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                        .clip(RoundedCornerShape(20.dp))
//                        .background(Color.White.copy(0.04f))
//                        .border(1.dp, Color.White.copy(0.06f), RoundedCornerShape(20.dp))
//                        .padding(12.dp, 12.dp, 16.dp, 12.dp)
//                ) {
//                    KamelImage(
//                        resource = { asyncPainterResource(item.posterUrl.orEmpty()) },
//                        contentDescription = item.title,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .width(52.dp)
//                            .height(72.dp)
//                            .clip(RoundedCornerShape(10.dp)),
//                        alignment = Alignment.Center,
//                        alpha = DefaultAlpha,
//                        contentAlignment = Alignment.Center
//                    )
//                    Spacer(Modifier.width(16.dp))
//                    Column(Modifier.weight(1f)) {
//                        Text(item.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                        Spacer(Modifier.height(3.dp))
//                        Text(item.subtitle, color = Color.White.copy(0.4f), fontSize = 10.sp, letterSpacing = 1.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                    }
//                    Text(
//                        "#${item.rank}",
//                        fontSize = 26.sp,
//                        fontWeight = FontWeight.Black,
//                        color = item.accentColor.copy(0.4f)
//                    )
//                }
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 3 — GENRES
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideGenres(genres: List<RecapGenreItem>) {
//    var curIndex by remember { mutableStateOf(0) }
//    val g = genres.getOrNull(curIndex) ?: return
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val bgAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(500), label = "genreBg")
//    val nameOffset by animateDpAsState(targetValue = if (visible) 0.dp else 30.dp, animationSpec = tween(600, 150), label = "genreOffset")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        // Full gradient background
//        Box(
//            modifier = Modifier
//                .matchParentSize()
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            Color(0xFF050810),
//                            g.color1.copy(0.13f),
//                            g.color2.copy(0.09f)
//                        ),
//                        start = Offset(0f, 0f),
//                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
//                    )
//                )
//        )
//
//        // Big symbol watermark
//        g.symbol?.let { sym ->
//            Text(
//                text = sym,
//                fontSize = 180.sp,
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(end = 12.dp, bottom = 80.dp)
//                    .alpha(0.06f)
//                    .blur(4.dp)
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .padding(horizontal = 28.dp, vertical = 64.dp)
//        ) {
//            Text("YOUR TOP GENRES", color = Color.White.copy(0.35f), fontSize = 10.sp, letterSpacing = 4.sp, modifier = Modifier.alpha(bgAlpha))
//            Spacer(Modifier.height(8.dp))
//
//            // Big genre display
//            Box(
//                modifier = Modifier
//                    .weight(1f, fill = false)
//                    .fillMaxWidth()
//                    .padding(vertical = 32.dp)
//            ) {
//                Column {
//                    val iconScale by animateFloatAsState(
//                        targetValue = if (visible) 1f else 0.6f,
//                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
//                        label = "iconScale"
//                    )
//                    g.symbol?.let { sym ->
//                        Text(
//                            text = sym,
//                            fontSize = 64.sp,
//                            modifier = Modifier
//                                .alpha(bgAlpha)
//                                .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
//                        )
//                        Spacer(Modifier.height(8.dp))
//                    }
//
//                    Text(
//                        text = g.name,
//                        fontSize = 68.sp,
//                        fontWeight = FontWeight.Black,
//                        lineHeight = 60.sp,
//                        letterSpacing = (-2).sp,
//                        style = TextStyle(
//                            brush = Brush.linearGradient(listOf(g.color1, g.color2))
//                        ),
//                        modifier = Modifier.offset(y = nameOffset).alpha(bgAlpha)
//                    )
//
//                    Spacer(Modifier.height(12.dp))
//                    Text(
//                        "RANK #${curIndex + 1} OF ${genres.size}",
//                        color = Color.White.copy(0.35f),
//                        fontSize = 11.sp,
//                        letterSpacing = 2.sp,
//                        modifier = Modifier.alpha(bgAlpha)
//                    )
//                    Spacer(Modifier.height(20.dp))
//
//                    // Progress bar
//                    Box(
//                        modifier = Modifier
//                            .height(4.dp)
//                            .fillMaxWidth((genres.size - curIndex).toFloat() / genres.size)
//                            .clip(RoundedCornerShape(2.dp))
//                            .background(Brush.horizontalGradient(listOf(g.color1, g.color2)))
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(24.dp))
//
//            // Selector
//            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//                genres.forEachIndexed { i, gg ->
//                    val isActive = i == curIndex
//                    val btnAlpha by animateFloatAsState(
//                        targetValue = if (visible) 1f else 0f,
//                        animationSpec = tween(500, 300 + i * 60),
//                        label = "genreBtn$i"
//                    )
//                    val btnSize by animateDpAsState(
//                        targetValue = if (isActive) 48.dp else 36.dp,
//                        animationSpec = spring(Spring.DampingRatioMediumBouncy),
//                        label = "btnSize$i"
//                    )
//
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.alpha(btnAlpha).clickable { curIndex = i }
//                    ) {
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier
//                                .size(btnSize)
//                                .clip(RoundedCornerShape(14.dp))
//                                .background(
//                                    if (isActive)
//                                        Brush.linearGradient(listOf(gg.color1, gg.color2))
//                                    else
//                                        Brush.linearGradient(listOf(Color.White.copy(0.04f), Color.White.copy(0.04f)))
//                                )
//                                .border(
//                                    width = if (isActive) 0.dp else 1.dp,
//                                    color = if (isActive) gg.color1 else Color.White.copy(0.1f),
//                                    shape = RoundedCornerShape(14.dp)
//                                )
//                        ) {
//                            gg.symbol?.let { sym -> Text(sym, fontSize = if (isActive) 22.sp else 16.sp) }
//                                ?: Text("${i + 1}", fontSize = if (isActive) 18.sp else 14.sp, color = if (isActive) Color.White else Color.White.copy(0.4f))
//                        }
//                        Spacer(Modifier.height(4.dp))
//                        Text(
//                            gg.name.take(5).uppercase(),
//                            fontSize = 8.sp,
//                            letterSpacing = 1.sp,
//                            color = if (isActive) Color.White else Color.White.copy(0.3f)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 4 — THEMES
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideThemes(themes: List<RecapThemeItem>) {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val bgAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, tween(500), label = "themeBg")
//    val titleOffset by animateDpAsState(targetValue = if (visible) 0.dp else 20.dp, tween(700, 100), label = "themeTitle")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF06040E))
//    ) {
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(Color(0xFFF72585).copy(0.10f), size.width * 0.7f, Offset(size.width / 2, 0f))
//        }
//
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(horizontal = 28.dp, vertical = 64.dp)) {
//            Text("YOUR TOP THEMES", color = Color.White.copy(0.35f), fontSize = 10.sp, letterSpacing = 4.sp, modifier = Modifier.alpha(bgAlpha))
//            Spacer(Modifier.height(8.dp))
//
//            Column(modifier = Modifier.offset(y = titleOffset).alpha(bgAlpha)) {
//                Text("The threads", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//                Text("that wove", color = Color(0xFFF72585), fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//                Text("your story", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//            }
//
//            Spacer(Modifier.height(36.dp))
//
//            themes.forEachIndexed { i, t ->
//                val itemAlpha by animateFloatAsState(
//                    targetValue = if (visible) 1f else 0f,
//                    animationSpec = tween(600, i * 100),
//                    label = "themeItem$i"
//                )
//                val itemScale by animateFloatAsState(
//                    targetValue = if (visible) 1f else 0.95f,
//                    animationSpec = spring(Spring.DampingRatioMediumBouncy, stiffness = 200f),
//                    label = "themeScale$i"
//                )
//
//                Box(
//                    modifier = Modifier
//                        .alpha(itemAlpha)
//                        .graphicsLayer { scaleX = itemScale; scaleY = itemScale }
//                        .fillMaxWidth()
//                        .padding(bottom = 16.dp)
//                        .clip(RoundedCornerShape(20.dp))
//                        .background(
//                            Brush.linearGradient(listOf(t.color1.copy(0.18f), t.color2.copy(0.08f)))
//                        )
//                        .border(
//                            1.dp,
//                            Brush.linearGradient(listOf(t.color1.copy(0.3f), t.color2.copy(0.1f))),
//                            RoundedCornerShape(20.dp)
//                        )
//                        .padding(18.dp, 18.dp, 20.dp, 18.dp)
//                ) {
//                    // Rank watermark
//                    Text(
//                        "${i + 1}",
//                        fontSize = 72.sp,
//                        fontWeight = FontWeight.Black,
//                        color = Color.White.copy(0.04f),
//                        modifier = Modifier.align(Alignment.CenterEnd)
//                    )
//
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier
//                                .size(52.dp)
//                                .clip(RoundedCornerShape(16.dp))
//                                .background(Brush.linearGradient(listOf(t.color1, t.color2)))
//                        ) {
//                            t.symbol?.let { sym -> Text(sym, fontSize = 24.sp) }
//                                ?: Text("${i + 1}", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
//                        }
//
//                        Spacer(Modifier.width(16.dp))
//
//                        Column {
//                            Text(t.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
//                            Spacer(Modifier.height(6.dp))
//                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
//                                Box(
//                                    modifier = Modifier
//                                        .height(3.dp)
//                                        .width(((5 - i) * 18 + 20).dp)
//                                        .clip(RoundedCornerShape(2.dp))
//                                        .background(Brush.horizontalGradient(listOf(t.color1, t.color2)))
//                                )
//                                Text("#${i + 1}", color = Color.White.copy(0.3f), fontSize = 9.sp, letterSpacing = 1.sp)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 5 — MILESTONES
//// ─────────────────────────────────────────────────────────────
//
//private data class MilestoneData(
//    val label: String,
//    val value: Int,
//    val unit: String,
//    val symbol: String,
//    val color1: Color,
//    val color2: Color,
//    val desc: String,
//)
//
//@Composable
//private fun MilestoneCard(m: MilestoneData, index: Int, visible: Boolean) {
//    val cardAlpha by animateFloatAsState(
//        targetValue = if (visible) 1f else 0f,
//        animationSpec = tween(600, index * 120),
//        label = "card$index"
//    )
//    val cardScale by animateFloatAsState(
//        targetValue = if (visible) 1f else 0.85f,
//        animationSpec = spring(Spring.DampingRatioMediumBouncy),
//        label = "cardScale$index"
//    )
//    val counter by rememberCounterValue(m.value, visible, 2000 + index * 200)
//
//    // Arc progress
//    val arcProgress = remember { Animatable(0f) }
//    LaunchedEffect(visible) {
//        if (visible) {
//            delay((300 + index * 150).toLong())
//            arcProgress.animateTo(0.75f, animationSpec = tween(1800, easing = FastOutSlowInEasing))
//        }
//    }
//
//    val displayValue = if (counter >= 1000) {
//        if (counter >= 10000) "${counter / 1000}k" else "${"%.1f".format(counter / 1000f)}k"
//    } else counter.toString()
//
//    Box(
//        modifier = Modifier
//            .alpha(cardAlpha)
//            .graphicsLayer { scaleX = cardScale; scaleY = cardScale }
//            .clip(RoundedCornerShape(24.dp))
//            .background(Brush.linearGradient(listOf(m.color1.copy(0.22f), m.color2.copy(0.11f))))
//            .border(1.dp, Brush.linearGradient(listOf(m.color1.copy(0.33f), m.color2.copy(0.1f))), RoundedCornerShape(24.dp))
//            .padding(20.dp)
//    ) {
//        // Top accent line
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(3.dp)
//                .align(Alignment.TopCenter)
//                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
//                .background(Brush.horizontalGradient(listOf(m.color1, m.color2)))
//        )
//
//        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 8.dp)) {
//            Text("MILESTONE", color = Color.White.copy(0.35f), fontSize = 9.sp, letterSpacing = 2.sp)
//            Spacer(Modifier.height(16.dp))
//
//            // Arc ring
//            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
//                val progress = arcProgress.value
//                Canvas(Modifier.size(110.dp)) {
//                    val r = 48.dp.toPx()
//                    val cx = size.width / 2
//                    val cy = size.height / 2
//                    val circumference = 2 * PI.toFloat() * r
//                    val startAngle = -90f
//
//                    // Background arc
//                    drawArc(
//                        color = Color.White.copy(0.06f),
//                        startAngle = startAngle,
//                        sweepAngle = 360f,
//                        useCenter = false,
//                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
//                        topLeft = Offset(cx - r, cy - r),
//                        size = Size(r * 2, r * 2)
//                    )
//                    // Progress arc
//                    drawArc(
//                        brush = Brush.linearGradient(listOf(m.color1, m.color2)),
//                        startAngle = startAngle,
//                        sweepAngle = progress * 360f,
//                        useCenter = false,
//                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round),
//                        topLeft = Offset(cx - r, cy - r),
//                        size = Size(r * 2, r * 2)
//                    )
//                }
//
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text(m.symbol, fontSize = 8.sp)
//                    Text(
//                        displayValue,
//                        fontSize = 22.sp,
//                        fontWeight = FontWeight.Black,
//                        style = TextStyle(
//                            brush = Brush.linearGradient(listOf(m.color1, m.color2))
//                        )
//                    )
//                    Text(m.unit.uppercase(), color = Color.White.copy(0.4f), fontSize = 8.sp, letterSpacing = 1.sp)
//                }
//            }
//
//            Spacer(Modifier.height(12.dp))
//            Text(m.label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
//            Spacer(Modifier.height(4.dp))
//            Text(m.desc, color = Color.White.copy(0.3f), fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
//        }
//    }
//}
//
//@Composable
//private fun SlideMilestones(data: RecapScreenData) {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val bgAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, tween(500), label = "msBg")
//    val titleOffset by animateDpAsState(targetValue = if (visible) 0.dp else 20.dp, tween(700, 100), label = "msTitle")
//
//    val isShows = data.type.equals("SHOWS", true)
//    val milestones = buildList {
//        add(MilestoneData("Minutes Watched", data.totalPartsDuration, "min", "▶️",
//            Color(0xFFFF006E), Color(0xFF8338EC), "That's pure screen time"))
//        add(MilestoneData(if (isShows) "Episodes Watched" else "Chapters Read",
//            data.totalPartsCount, if (isShows) "eps" else "ch", if (isShows) "📺" else "📖",
//            Color(0xFFFB5607), Color(0xFFFFBE0B), if (isShows) "Episode after episode, no regrets" else "Pages flew by in the night"))
//        add(MilestoneData("Series Completed", data.totalSeriesCount, "series", "🏆",
//            Color(0xFF06D6A0), Color(0xFF118AB2), "Every series a new world"))
//        add(MilestoneData("Top Percentile", (data.topPercentile * 100).toInt(), "%", "⭐",
//            Color(0xFF7209B7), Color(0xFFF72585), "You're among the elite"))
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF040810))
//    ) {
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(Color(0xFFFF006E).copy(0.08f), size.width * 0.6f, Offset(size.width * 0.8f, size.height * 0.6f))
//            drawCircle(Color(0xFF8338EC).copy(0.10f), size.width * 0.5f, Offset(size.width * 0.2f, size.height * 0.3f))
//        }
//
//        Column(modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//            .padding(28.dp, 64.dp, 28.dp, 48.dp)) {
//            Text("YOUR MILESTONES", color = Color.White.copy(0.35f), fontSize = 10.sp, letterSpacing = 4.sp, modifier = Modifier.alpha(bgAlpha))
//            Spacer(Modifier.height(8.dp))
//            Column(modifier = Modifier.offset(y = titleOffset).alpha(bgAlpha)) {
//                Text("These numbers", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//                Text("marked your", color = Color(0xFFFF006E), fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//                Text("season finale", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black, lineHeight = 30.sp)
//            }
//            Spacer(Modifier.height(32.dp))
//
//            // 2x2 grid
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
//                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
//                    MilestoneCard(milestones[0], 0, visible)
//                    MilestoneCard(milestones[2], 2, visible)
//                }
//                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
//                    MilestoneCard(milestones[1], 1, visible)
//                    MilestoneCard(milestones[3], 3, visible)
//                }
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  SLIDE 6 — FINALE
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//private fun SlideFinale(year: Int, allMedia: List<RecapMediaItem>) {
//    var visible by remember { mutableStateOf(false) }
//    LaunchedEffect(Unit) { delay(100); visible = true }
//
//    val contentAlpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, tween(1000, 200), label = "finaleAlpha")
//    val contentOffset by animateDpAsState(targetValue = if (visible) 0.dp else 40.dp, tween(1000, 200), label = "finaleOffset")
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF020508))
//    ) {
//        // Poster mosaic background
//        val repeated = (allMedia + allMedia + allMedia).take(12)
//        Column(
//            modifier = Modifier.matchParentSize().alpha(0.35f),
//            verticalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            listOf(
//                repeated.take(4),
//                repeated.drop(4).take(4),
//                repeated.drop(8).take(4)
//            ).forEachIndexed { row, rowItems ->
//                Row(
//                    modifier = Modifier.fillMaxWidth().weight(1f),
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    rowItems.forEach { item ->
//                        KamelImage(
//                            resource = asyncPainterResource(item.posterUrl.orEmpty()),
//                            contentDescription = null,
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier
//                                .weight(1f)
//                                .fillMaxHeight(),
//                            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0.7f) }),
//                            // Opsiyonel: Resim yüklenirken veya hata oluştuğunda boş görünmemesi için
//                            onLoading = { Box(Modifier.fillMaxSize().background(Color.DarkGray)) },
//                            onFailure = { Box(Modifier.fillMaxSize().background(Color.Black)) }
//                        )
//                    }
//                }
//            }
//        }
//
//        // Gradient overlay
//        Box(
//            modifier = Modifier
//                .matchParentSize()
//                .background(
//                    Brush.verticalGradient(
//                        listOf(
//                            Color(0xFF020508).copy(0.3f),
//                            Color(0xFF020508).copy(0.6f),
//                            Color(0xFF020508).copy(0.9f)
//                        )
//                    )
//                )
//        )
//
//        Canvas(Modifier.matchParentSize()) {
//            drawCircle(Color(0xFFF5C518).copy(0.05f), size.width * 0.6f, Offset(size.width / 2, size.height / 2))
//        }
//
//        // Content
//        Column(
//            modifier = Modifier
//                .align(Alignment.BottomStart)
//                .padding(28.dp, 0.dp, 28.dp, 64.dp)
//                .offset(y = contentOffset)
//                .alpha(contentAlpha)
//        ) {
//            Text("UNTIL NEXT TIME", color = Color.White.copy(0.4f), fontSize = 10.sp, letterSpacing = 4.sp)
//            Spacer(Modifier.height(16.dp))
//
//            Text("See you in", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Black, lineHeight = 48.sp, letterSpacing = (-2).sp)
//            Text("${year + 1}", color = Color(0xFFF5C518), fontSize = 68.sp, fontWeight = FontWeight.Black, lineHeight = 60.sp, letterSpacing = (-2).sp)
//
//            Spacer(Modifier.height(16.dp))
//
//            Text(
//                "Every anime watched.\nEvery chapter read.\nEvery genre explored.\nThis was your arc.",
//                color = Color.White.copy(0.4f),
//                fontSize = 11.sp,
//                letterSpacing = 1.5.sp,
//                lineHeight = 20.sp
//            )
//
//            Spacer(Modifier.height(32.dp))
//
//            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .size(36.dp)
//                        .clip(RoundedCornerShape(10.dp))
//                        .background(Brush.linearGradient(listOf(Color(0xFF1A237E), Color(0xFF7B2FBE), Color(0xFFF5C518))))
//                ) {
//                    Text("K", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
//                }
//                Column {
//                    Text("Kurozora", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
//                    Text("RE:CAP $year", color = Color.White.copy(0.3f), fontSize = 9.sp, letterSpacing = 1.sp)
//                }
//            }
//        }
//    }
//}
//
//
//
//// ─────────────────────────────────────────────────────────────
////  ROOT SCREEN
//// ─────────────────────────────────────────────────────────────
//
//@Composable
//fun RecapScreen(data: RecapScreenData) {
//    val coroutineScope = rememberCoroutineScope()
//
//    val pagerState = rememberPagerState(
//        initialPage = 0,
//        pageCount = { 7 }
//    )
//
//    val allMedia = (data.topShows + data.topLiteratures)
//    val currentPage by remember { derivedStateOf { pagerState.currentPage } }
//
//    // Fluent scrolling behavior
//    val flingBehavior = PagerDefaults.flingBehavior(
//        state = pagerState,
//        snapAnimationSpec = tween(
//            durationMillis = 300,
//            easing = FastOutSlowInEasing
//        )
//    )
//
//    VerticalPager(
//        state = pagerState,
//        modifier = Modifier.fillMaxSize(),
//        userScrollEnabled = true,
//        pageSpacing = 0.dp,
//        contentPadding = PaddingValues(0.dp),
//        flingBehavior = flingBehavior,
//        key = { page -> page }
//    ) { page ->
//        val isActive = remember(page, currentPage) { page == currentPage }
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFF050810))
//                .graphicsLayer {
//                    // Smooth parallax/fade transitions between slides
//                    val pageOffset = (
//                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
//                            ).absoluteValue
//
//                    alpha = lerp(
//                        start = 0.5f,
//                        stop = 1f,
//                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                    )
//
//                    val scale = lerp(
//                        start = 0.95f,
//                        stop = 1f,
//                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                    )
//                    scaleX = scale
//                    scaleY = scale
//                }
//        ) {
//            when (page) {
//                0 -> SlideHero(year = data.year, active = isActive)
//                1 -> if (data.topShows.isNotEmpty()) {
//                    SlideTopShows(items = data.topShows, totalCount = data.totalSeriesCount)
//                } else {
//                    SlideTopLiteratures(items = data.topLiteratures, totalCount = data.totalSeriesCount)
//                }
//                2 -> if (data.topLiteratures.isNotEmpty()) {
//                    SlideTopLiteratures(items = data.topLiteratures, totalCount = data.totalSeriesCount)
//                } else {
//                    SlideTopShows(items = data.topShows, totalCount = data.totalSeriesCount)
//                }
//                3 -> if (data.topGenres.isNotEmpty()) {
//                    SlideGenres(genres = data.topGenres)
//                } else {
//                    SlideThemes(themes = data.topThemes)
//                }
//                4 -> if (data.topThemes.isNotEmpty()) {
//                    SlideThemes(themes = data.topThemes)
//                } else {
//                    SlideGenres(genres = data.topGenres)
//                }
//                5 -> SlideMilestones(data = data)
//                6 -> SlideFinale(year = data.year, allMedia = allMedia.ifEmpty { data.topShows })
//            }
//        }
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
////  PREVIEW  (Android Studio)
//// ─────────────────────────────────────────────────────────────
//
//@Preview(
//    name = "Re:CAP Screen – Dark",
//    showBackground = true,
//    backgroundColor = 0xFF050810,
//)
//@Composable
//private fun RecapScreenPreview() {
//    RecapScreen(
//        data = RecapScreenData(
//            year = 2025,
//            type = "SHOWS",
//            totalSeriesCount = 7,
//            totalPartsCount = 87,
//            totalPartsDuration = 1450,
//            topPercentile = 0.05,
//
//            topShows = listOf(
//                RecapMediaItem(1, "Demon Slayer", "Kimetsu no Yaiba",
//                    "https://cdn.myanimelist.net/images/anime/1286/99889.jpg", Color(0xFFE85D04)),
//                RecapMediaItem(2, "Jujutsu Kaisen", "Cursed Energy",
//                    "https://cdn.myanimelist.net/images/anime/1171/109222.jpg", Color(0xFF7B2FBE)),
//                RecapMediaItem(3, "One Piece", "King of Pirates",
//                    "https://cdn.myanimelist.net/images/anime/6/73245.jpg", Color(0xFFF9C74F)),
//                RecapMediaItem(4, "Attack on Titan", "Final Chapter",
//                    "https://cdn.myanimelist.net/images/anime/10/47347.jpg", Color(0xFF4A5568)),
//                RecapMediaItem(5, "Bleach: TYBW", "Blood War Saga",
//                    "https://cdn.myanimelist.net/images/anime/1764/126627.jpg", Color(0xFF48CAE4)),
//            ),
//
//            topLiteratures = listOf(
//                RecapMediaItem(1, "Naruto", "Legend of Konoha",
//                    "https://cdn.myanimelist.net/images/manga/3/117681.jpg", Color(0xFFF77F00)),
//                RecapMediaItem(2, "Bleach", "Soul Society",
//                    "https://cdn.myanimelist.net/images/manga/3/163729.jpg", Color(0xFF48CAE4)),
//                RecapMediaItem(3, "One Piece", "Grand Line",
//                    "https://cdn.myanimelist.net/images/manga/2/253146.jpg", Color(0xFFF9C74F)),
//                RecapMediaItem(4, "Tokyo Ghoul", "Kagune Rising",
//                    "https://cdn.myanimelist.net/images/manga/3/114037.jpg", Color(0xFF9B2335)),
//            ),
//
//            topGenres = listOf(
//                RecapGenreItem("Action", "⚔️", Color(0xFFFF4500), Color(0xFFFF8C00)),
//                RecapGenreItem("Shounen", "🔥", Color(0xFFFF6B35), Color(0xFFFFD700)),
//                RecapGenreItem("Fantasy", "✨", Color(0xFF7B2FBE), Color(0xFFC77DFF)),
//                RecapGenreItem("Adventure", "🧭", Color(0xFF00B4D8), Color(0xFF0077B6)),
//                RecapGenreItem("Supernatural", "👁️", Color(0xFF2D6A4F), Color(0xFF74C69D)),
//            ),
//
//            topThemes = listOf(
//                RecapThemeItem("Super Power", "⚡", Color(0xFFF72585), Color(0xFF7209B7)),
//                RecapThemeItem("Martial Arts", "🥋", Color(0xFFE63946), Color(0xFF457B9D)),
//                RecapThemeItem("Mythology", "🏛️", Color(0xFFD4A017), Color(0xFF8B5E3C)),
//                RecapThemeItem("Historical", "📜", Color(0xFF6B4226), Color(0xFFC49A6C)),
//                RecapThemeItem("School", "🎒", Color(0xFF06D6A0), Color(0xFF118AB2)),
//            ),
//        )
//    )
//}