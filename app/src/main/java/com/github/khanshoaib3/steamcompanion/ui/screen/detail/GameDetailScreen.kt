package com.github.khanshoaib3.steamcompanion.ui.screen.detail

import android.content.res.Configuration
import android.icu.text.NumberFormat
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.khanshoaib3.steamcompanion.R
import com.github.khanshoaib3.steamcompanion.data.model.detail.Category
import com.github.khanshoaib3.steamcompanion.data.model.detail.Requirements
import com.github.khanshoaib3.steamcompanion.data.model.detail.SteamWebApiAppDetailsResponse
import com.github.khanshoaib3.steamcompanion.ui.components.CenterAlignedSelectableText
import com.github.khanshoaib3.steamcompanion.ui.theme.SteamCompanionTheme
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import java.util.Currency

@Composable
fun GameDetailScreen(
    modifier: Modifier = Modifier,
    appId: Int?,
) {
    val viewModel = hiltViewModel<GameDetailViewModel>()
    val gameData by viewModel.gameData.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(scope) {
        viewModel.updateAppId(appId)
    }

    if (gameData.content == null || gameData.content?.data == null || gameData.content?.success != true) {
        if (appId != null && appId != 0) CenterAlignedSelectableText("Unable to get data for app with id $appId")
        return
    }

    GameDetailCard(modifier = modifier.verticalScroll(scrollState), gameData = gameData)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameDetailCard(
    modifier: Modifier = Modifier,
    gameData: GameData,
) {
    val tabs = listOf<String>("About", "System Requirements", "Deals", "Player Stats")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Box(modifier = modifier) {
        OutlinedCard {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                    modifier = Modifier
                        .padding(vertical = dimensionResource(R.dimen.padding_small))
                        .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                        .padding(bottom = dimensionResource(R.dimen.padding_medium))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = gameData.content!!.data!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                        Row {
                            IconButton(onClick = { /* TODO Add behaviour */ }) {
                                Icon(
                                    Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark app"
                                )
                            }
                            IconButton(onClick = { /* TODO Add behaviour */ }) {
                                Icon(Icons.Default.Share, contentDescription = "Share app")
                            }
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(0.25f)) {
                            AsyncImage(
                                model = ImageRequest.Builder(context = LocalContext.current)
                                    .data("https://cdn.cloudflare.steamstatic.com/steam/apps/${gameData.content!!.data!!.steamAppId}/library_600x900.jpg")
                                    .build(),
                                contentDescription = "Hero capsule for ${gameData.content.data.name}",
                                placeholder = painterResource(R.drawable.preview_image_300x450),
                                modifier = Modifier.clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                            )
                        }
                        Spacer(Modifier.width(dimensionResource(R.dimen.padding_medium)))
                        Column(modifier = Modifier.weight(0.75f)) {
                            Row {
                                Column(modifier = Modifier.weight(0.45f)) {
                                    Text("App ID", fontWeight = FontWeight.Bold)
                                    Text("App Type", fontWeight = FontWeight.Bold)
                                    Text("Developer", fontWeight = FontWeight.Bold)
                                    Text("Publisher", fontWeight = FontWeight.Bold)
                                    Text("Supported Systems", fontWeight = FontWeight.Bold)
                                }
                                Column(modifier = Modifier.weight(0.55f)) {
                                    Text("${gameData.content!!.data!!.steamAppId}")
                                    Text(gameData.content.data.type)
                                    gameData.content.data
                                    Text(
                                        gameData.content.data.developers?.joinToString(", ")
                                            ?: "Not found!"
                                    )
                                    Text(
                                        gameData.content.data.publishers?.joinToString(", ")
                                            ?: "Not found!"
                                    )
                                    Row {
                                        if (gameData.content.data.platforms.windows) {
                                            Icon(
                                                painter = painterResource(R.drawable.windows_icon),
                                                contentDescription = "Windows",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        if (gameData.content.data.platforms.linux) {
                                            Icon(
                                                painter = painterResource(R.drawable.linux_icon),
                                                contentDescription = "Linux",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        if (gameData.content.data.platforms.mac) {
                                            Icon(
                                                painter = painterResource(R.drawable.mac_icon),
                                                contentDescription = "MacOS",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Price
                        if (gameData.content?.data?.isFree == false) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(0.4f)
                                    .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                            ) {
                                Surface(color = MaterialTheme.colorScheme.secondary) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(dimensionResource(R.dimen.padding_medium))
                                    ) {
                                        val currentPrice = NumberFormat.getNumberInstance().format(
                                            gameData.content.data.priceOverview?.finalPrice?.toBigDecimal()
                                                ?.movePointLeft(2) ?: 0
                                        )
                                        val originalPrice = NumberFormat.getNumberInstance().format(
                                            gameData.content.data.priceOverview?.initial?.toBigDecimal()
                                                ?.movePointLeft(2) ?: 0
                                        )
                                        val currencySymbol = Currency.getInstance(
                                            gameData.content.data.priceOverview?.currency ?: "INR"
                                        ).symbol
                                        Text(
                                            text = "$currencySymbol $currentPrice",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Original: $currencySymbol $originalPrice",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        // Rating
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_medium)))
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_very_small))
                        ) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .padding(dimensionResource(R.dimen.padding_small)),
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.Center
                            ) {
                                gameData.content?.data?.categories?.forEach { category ->
                                    CategoryChip(
                                        category = category,
                                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small))
                                    )
                                }
                            }
                        }
                    }
                    CenterAlignedSelectableText(
                        gameData.content?.data?.shortDescription ?: "Empty Description"
                    )
                    if (gameData.content?.data?.isFree == false) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledIconButton(
                                onClick = { /* TODO Add tracking functionality */ },
                                modifier = Modifier.fillMaxWidth(0.8f)
                            ) {
                                Text(
                                    text = "Track Price",
                                    fontWeight = FontWeight.Medium,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    HorizontalDivider()
                    ScrollableTabRow(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        selectedTabIndex = selectedTabIndex
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = index == selectedTabIndex,
                                onClick = { selectedTabIndex = index },
                                text = { Text(text = tab) }
                            )
                        }
                    }
                    when (selectedTabIndex) {
                        0 -> RenderHtmlContent(
                            html = gameData.content?.data?.detailedDescription ?: "Empty"
                        )

                        1 -> Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))) {
                            val json = Json { ignoreUnknownKeys = true }
                            if (gameData.content?.data?.platforms?.windows == true
                                && gameData.content.data.pcRequirements is JsonObject
                            ) {
                                Text(
                                    "Windows Requirements",
                                    textDecoration = TextDecoration.Underline
                                )
                                val pcRequirements =
                                    json.decodeFromJsonElement<Requirements>(gameData.content.data.pcRequirements)
                                if (pcRequirements.minimum != null) {
                                    RenderHtmlContent(
                                        html = pcRequirements.minimum,
                                        removeLineBreaks = true
                                    )
                                }
                                if (pcRequirements.recommended != null) {
                                    RenderHtmlContent(
                                        html = pcRequirements.recommended,
                                        removeLineBreaks = true
                                    )
                                }
                            }
                            if (gameData.content?.data?.platforms?.linux == true
                                && gameData.content.data.linuxRequirements is JsonObject
                            ) {
                                Text(
                                    "Linux Requirements",
                                    textDecoration = TextDecoration.Underline
                                )
                                val linuxRequirements =
                                    json.decodeFromJsonElement<Requirements>(gameData.content.data.linuxRequirements)
                                if (linuxRequirements.minimum != null) {
                                    RenderHtmlContent(
                                        html = linuxRequirements.minimum,
                                        removeLineBreaks = true
                                    )
                                }
                                if (linuxRequirements.recommended != null) {
                                    RenderHtmlContent(
                                        html = linuxRequirements.recommended,
                                        removeLineBreaks = true
                                    )
                                }
                            }
                            if (gameData.content?.data?.platforms?.mac == true
                                && gameData.content.data.macRequirements is JsonObject
                            ) {
                                Text(
                                    "MacOS Requirements",
                                    textDecoration = TextDecoration.Underline
                                )
                                val macRequirements =
                                    json.decodeFromJsonElement<Requirements>(gameData.content.data.macRequirements)
                                if (macRequirements.minimum != null) {
                                    RenderHtmlContent(
                                        html = macRequirements.minimum,
                                        removeLineBreaks = true
                                    )
                                }
                                if (macRequirements.recommended != null) {
                                    RenderHtmlContent(
                                        html = macRequirements.recommended,
                                        removeLineBreaks = true
                                    )
                                }
                            }
                        }
                        2 -> CenterAlignedSelectableText("Deals Page")
                        3 -> CenterAlignedSelectableText("Player Stats Page")
                        else -> Text("Unknown Page")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(category: Category, modifier: Modifier = Modifier) {
    var isOpen by remember { mutableStateOf(false) }
    val view = LocalView.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(true, onClick = {
                isOpen = !isOpen
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            })
            .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
            .background(color = if (isOpen) MaterialTheme.colorScheme.secondary else Color.Unspecified)
            .animateContentSize()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data("https://steamdb.info/static/img/categories/${category.id}.png")
                .build(),
            contentDescription = category.description,
            placeholder = painterResource(R.drawable.windows_icon),
            modifier = Modifier.size(28.dp)
        )
        AnimatedVisibility(isOpen) {
            Text(category.description, maxLines = 1, color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GameDetailScreenPreview() {
    val gameRawData =
        "{\"success\":true,\"data\":{\"type\":\"game\",\"name\":\"Half-Life 2\",\"steam_appid\":220,\"required_age\":0,\"is_free\":false,\"controller_support\":\"full\",\"dlc\":[323140],\"detailed_description\":\"<p class=\\\"bb_paragraph\\\"><i>The Seven Hour War is lost. Earth has surrendered. The Black Mesa incident is a distant memory.<\\/i> The player again picks up the crowbar of research scientist Gordon Freeman, who finds himself on an alien-infested Earth being picked to the bone, its resources depleted, its populace dwindling. Freeman is thrust into the unenviable role of rescuing the world from the wrong he unleashed back at Black Mesa. And a lot of people he cares about are counting on him.<\\/p><p class=\\\"bb_paragraph\\\">Half-Life 2 is the landmark first-person shooter that “forged the framework for the next generation of games” (PC Gamer). Experience a thrilling campaign packed with unprecedented levels of immersive world-building, boundary-pushing physics, and exhilarating combat.<\\/p><p class=\\\"bb_paragraph\\\"> <\\/p><h2 class=\\\"bb_tag\\\">Includes the Half-Life 2 Episode One and Two Expansions<\\/h2><p class=\\\"bb_paragraph\\\">The story of Half-Life 2 continues with Episodes One and Two, full-featured Half-Life adventures set during the aftermath of the base game. They are accessible from the main menu, and you will automatically advance to the next episode after completing each one.<\\/p><p class=\\\"bb_paragraph\\\"><img class=\\\"bb_img\\\" src=\\\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/extras\\/boxes.png?t=1737139959\\\" \\/><\\/p><h2 class=\\\"bb_tag\\\"> Half-Life 2: Deathmatch<\\/h2><p class=\\\"bb_paragraph\\\">Fast multiplayer action set in the Half-Life 2 universe! HL2's physics adds a new dimension to deathmatch play. Play straight deathmatch or try Combine vs. Resistance teamplay. Toss a toilet at your friend today! <i>Included in your Steam library with purchase of Half-Life 2.<\\/i><\\/p><p class=\\\"bb_paragraph\\\"><img class=\\\"bb_img\\\" src=\\\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/extras\\/deathmatch.png?t=1737139959\\\" \\/><\\/p><h2 class=\\\"bb_tag\\\">Steam Workshop<\\/h2><p class=\\\"bb_paragraph\\\">Play entire campaigns or replace weapons, enemies, UI, and more with content created by the community.<\\/p>\",\"about_the_game\":\"<p class=\\\"bb_paragraph\\\"><i>The Seven Hour War is lost. Earth has surrendered. The Black Mesa incident is a distant memory.<\\/i> The player again picks up the crowbar of research scientist Gordon Freeman, who finds himself on an alien-infested Earth being picked to the bone, its resources depleted, its populace dwindling. Freeman is thrust into the unenviable role of rescuing the world from the wrong he unleashed back at Black Mesa. And a lot of people he cares about are counting on him.<\\/p><p class=\\\"bb_paragraph\\\">Half-Life 2 is the landmark first-person shooter that “forged the framework for the next generation of games” (PC Gamer). Experience a thrilling campaign packed with unprecedented levels of immersive world-building, boundary-pushing physics, and exhilarating combat.<\\/p><p class=\\\"bb_paragraph\\\"> <\\/p><h2 class=\\\"bb_tag\\\">Includes the Half-Life 2 Episode One and Two Expansions<\\/h2><p class=\\\"bb_paragraph\\\">The story of Half-Life 2 continues with Episodes One and Two, full-featured Half-Life adventures set during the aftermath of the base game. They are accessible from the main menu, and you will automatically advance to the next episode after completing each one.<\\/p><p class=\\\"bb_paragraph\\\"><img class=\\\"bb_img\\\" src=\\\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/extras\\/boxes.png?t=1737139959\\\" \\/><\\/p><h2 class=\\\"bb_tag\\\"> Half-Life 2: Deathmatch<\\/h2><p class=\\\"bb_paragraph\\\">Fast multiplayer action set in the Half-Life 2 universe! HL2's physics adds a new dimension to deathmatch play. Play straight deathmatch or try Combine vs. Resistance teamplay. Toss a toilet at your friend today! <i>Included in your Steam library with purchase of Half-Life 2.<\\/i><\\/p><p class=\\\"bb_paragraph\\\"><img class=\\\"bb_img\\\" src=\\\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/extras\\/deathmatch.png?t=1737139959\\\" \\/><\\/p><h2 class=\\\"bb_tag\\\">Steam Workshop<\\/h2><p class=\\\"bb_paragraph\\\">Play entire campaigns or replace weapons, enemies, UI, and more with content created by the community.<\\/p>\",\"short_description\":\"Reawakened from stasis in the occupied metropolis of City 17, Gordon Freeman is joined by Alyx Vance as he leads a desperate human resistance. Experience the landmark first-person shooter packed with immersive world-building, boundary-pushing physics, and exhilarating combat.\",\"supported_languages\":\"English<strong>*<\\/strong>, French<strong>*<\\/strong>, German<strong>*<\\/strong>, Italian<strong>*<\\/strong>, Korean<strong>*<\\/strong>, Spanish - Spain<strong>*<\\/strong>, Russian<strong>*<\\/strong>, Simplified Chinese, Traditional Chinese, Dutch, Danish, Finnish, Japanese, Norwegian, Polish, Portuguese - Portugal, Swedish, Thai, Bulgarian, Czech, Greek, Hungarian, Portuguese - Brazil, Romanian, Spanish - Latin America, Turkish, Ukrainian<br><strong>*<\\/strong>languages with full audio support\",\"header_image\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/header.jpg?t=1737139959\",\"capsule_image\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/951c6aca52bf32d7c95e9f8b3c04fa95e9a735ea\\/capsule_231x87.jpg?t=1737139959\",\"capsule_imagev5\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/951c6aca52bf32d7c95e9f8b3c04fa95e9a735ea\\/capsule_184x69.jpg?t=1737139959\",\"website\":\"http:\\/\\/www.half-life.com\\/halflife2\",\"pc_requirements\":{\"minimum\":\"<strong>Minimum:<\\/strong><br><ul class=\\\"bb_ul\\\"><li><strong>OS *:<\\/strong> Windows 7, Vista, XP<br><\\/li><li><strong>Processor:<\\/strong> 1.7 Ghz<br><\\/li><li><strong>Memory:<\\/strong> 512 MB RAM<br><\\/li><li><strong>Graphics:<\\/strong> DirectX 8.1 level Graphics Card (requires support for SSE)<br><\\/li><li><strong>Storage:<\\/strong> 6500 MB available space<\\/li><\\/ul>\"},\"mac_requirements\":{\"minimum\":\"<strong>Minimum:<\\/strong><br><ul class=\\\"bb_ul\\\"><li><strong>OS:<\\/strong> Leopard 10.5.8, Snow Leopard 10.6.3, or higher<br><\\/li><li><strong>Memory:<\\/strong> 1 GB RAM<br><\\/li><li><strong>Graphics:<\\/strong> Nvidia GeForce8 or higher, ATI X1600 or higher, Intel HD 3000 or higher<\\/li><\\/ul>\"},\"linux_requirements\":[],\"developers\":[\"Valve\"],\"publishers\":[\"Valve\"],\"demos\":[{\"appid\":219,\"description\":\"\"}],\"price_overview\":{\"currency\":\"INR\",\"initial\":48000,\"final\":9600,\"discount_percent\":80,\"initial_formatted\":\"₹ 480\",\"final_formatted\":\"₹ 96\"},\"packages\":[36,289444,469],\"package_groups\":[{\"name\":\"default\",\"title\":\"Buy Half-Life 2\",\"description\":\"\",\"selection_text\":\"Select a purchase option\",\"save_text\":\"\",\"display_type\":0,\"is_recurring_subscription\":\"false\",\"subs\":[{\"packageid\":469,\"percent_savings_text\":\"-90% \",\"percent_savings\":0,\"option_text\":\"The Orange Box - <span class=\\\"discount_original_price\\\">₹ 880<\\/span> ₹ 88\",\"option_description\":\"\",\"can_get_free_license\":\"0\",\"is_free_license\":false,\"price_in_cents_with_discount\":8800},{\"packageid\":36,\"percent_savings_text\":\"-80% \",\"percent_savings\":0,\"option_text\":\"Half-Life 2 - <span class=\\\"discount_original_price\\\">₹ 480<\\/span> ₹ 96\",\"option_description\":\"\",\"can_get_free_license\":\"0\",\"is_free_license\":false,\"price_in_cents_with_discount\":9600},{\"packageid\":289444,\"percent_savings_text\":\" \",\"percent_savings\":0,\"option_text\":\"Half-Life 2 - Commercial License - ₹ 349\",\"option_description\":\"\",\"can_get_free_license\":\"0\",\"is_free_license\":false,\"price_in_cents_with_discount\":34900}]}],\"platforms\":{\"windows\":true,\"mac\":false,\"linux\":true},\"metacritic\":{\"score\":96,\"url\":\"https:\\/\\/www.metacritic.com\\/game\\/pc\\/half-life-2?ftag=MCD-06-10aaa1f\"},\"categories\":[{\"id\":2,\"description\":\"Single-player\"},{\"id\":22,\"description\":\"Steam Achievements\"},{\"id\":28,\"description\":\"Full controller support\"},{\"id\":29,\"description\":\"Steam Trading Cards\"},{\"id\":13,\"description\":\"Captions available\"},{\"id\":30,\"description\":\"Steam Workshop\"},{\"id\":23,\"description\":\"Steam Cloud\"},{\"id\":16,\"description\":\"Includes Source SDK\"},{\"id\":14,\"description\":\"Commentary available\"},{\"id\":41,\"description\":\"Remote Play on Phone\"},{\"id\":42,\"description\":\"Remote Play on Tablet\"},{\"id\":44,\"description\":\"Remote Play Together\"},{\"id\":62,\"description\":\"Family Sharing\"},{\"id\":63,\"description\":\"Steam Timeline\"}],\"genres\":[{\"id\":\"1\",\"description\":\"Action\"}],\"screenshots\":[{\"id\":0,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_47b4105b396de408cb8b6b4f358c69e5e2a62dae.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_47b4105b396de408cb8b6b4f358c69e5e2a62dae.1920x1080.jpg?t=1737139959\"},{\"id\":1,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_0e499071a60a20b24149ad65a8edb769250f2921.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_0e499071a60a20b24149ad65a8edb769250f2921.1920x1080.jpg?t=1737139959\"},{\"id\":2,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_ffb00abd45012680e4f209355ec81f961b6dd1fb.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_ffb00abd45012680e4f209355ec81f961b6dd1fb.1920x1080.jpg?t=1737139959\"},{\"id\":3,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_b822a29b3804e05ab9517cac99a5d978d109a32b.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_b822a29b3804e05ab9517cac99a5d978d109a32b.1920x1080.jpg?t=1737139959\"},{\"id\":4,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_c400361f185800786ea984e795f2a0dd4afee990.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_c400361f185800786ea984e795f2a0dd4afee990.1920x1080.jpg?t=1737139959\"},{\"id\":5,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_a2aeefb3ad34c46af5c381ff03ac0973892f5530.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_a2aeefb3ad34c46af5c381ff03ac0973892f5530.1920x1080.jpg?t=1737139959\"},{\"id\":6,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_394f4ad714937db2cc90545972b318ddb6db7231.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_394f4ad714937db2cc90545972b318ddb6db7231.1920x1080.jpg?t=1737139959\"},{\"id\":7,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_412b0e9f8285d3695d0b39840da41c184dff591a.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_412b0e9f8285d3695d0b39840da41c184dff591a.1920x1080.jpg?t=1737139959\"},{\"id\":8,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_93d1112a93572b2826a02456db4195c07bd2221a.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_93d1112a93572b2826a02456db4195c07bd2221a.1920x1080.jpg?t=1737139959\"},{\"id\":9,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_ed5532325f508728f8481f0109d662352a519e0a.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_ed5532325f508728f8481f0109d662352a519e0a.1920x1080.jpg?t=1737139959\"},{\"id\":10,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_4e76506add2af0c438d3c4bc810ccb823353fd13.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_4e76506add2af0c438d3c4bc810ccb823353fd13.1920x1080.jpg?t=1737139959\"},{\"id\":11,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_8d6f9f74b33e2b0b296c6bff9836085e063b2d2f.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_8d6f9f74b33e2b0b296c6bff9836085e063b2d2f.1920x1080.jpg?t=1737139959\"},{\"id\":12,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_1ee62eeed05669128167c2f28c5ece55aa683191.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_1ee62eeed05669128167c2f28c5ece55aa683191.1920x1080.jpg?t=1737139959\"},{\"id\":13,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_1a36c58cd035de49493962da7bb929501a4b3bcc.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_1a36c58cd035de49493962da7bb929501a4b3bcc.1920x1080.jpg?t=1737139959\"},{\"id\":14,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_06d5f06190db60187bb7128ae44902d676efef10.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_06d5f06190db60187bb7128ae44902d676efef10.1920x1080.jpg?t=1737139959\"},{\"id\":15,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_b3de6987b384b2db61b5dcad2dd6460fd2969612.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_b3de6987b384b2db61b5dcad2dd6460fd2969612.1920x1080.jpg?t=1737139959\"},{\"id\":16,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_4b9943f1961a35f0cdbeceed2f48c70cb05d791a.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_4b9943f1961a35f0cdbeceed2f48c70cb05d791a.1920x1080.jpg?t=1737139959\"},{\"id\":17,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_18841cb9a8fc2cf67039317b601d10c4059b6fa8.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_18841cb9a8fc2cf67039317b601d10c4059b6fa8.1920x1080.jpg?t=1737139959\"},{\"id\":18,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_fe894d70bdfa75236a4b451efbeea7d4ce3e0174.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_fe894d70bdfa75236a4b451efbeea7d4ce3e0174.1920x1080.jpg?t=1737139959\"},{\"id\":19,\"path_thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_70af5835953e5367fe536d90a8ddf2a26c2668dc.600x338.jpg?t=1737139959\",\"path_full\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/ss_70af5835953e5367fe536d90a8ddf2a26c2668dc.1920x1080.jpg?t=1737139959\"}],\"movies\":[{\"id\":257074217,\"name\":\"Half-Life 2 Trailer\",\"thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/257074217\\/2a5d40f3f7cd4a644849a7dae7ee9f9d7fb2074d\\/movie_600x337.jpg?t=1732069594\",\"webm\":{\"480\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/257074217\\/movie480_vp9.webm?t=1732069594\",\"max\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/257074217\\/movie_max_vp9.webm?t=1732069594\"},\"mp4\":{\"480\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/257074217\\/movie480.mp4?t=1732069594\",\"max\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/257074217\\/movie_max.mp4?t=1732069594\"},\"highlight\":true},{\"id\":904,\"name\":\"Half-Life 2 Trailer\",\"thumbnail\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/904\\/003a9a6063e5f154f7244aa5b55e16ddeb390dc4\\/movie_600x337.jpg?t=1732069598\",\"webm\":{\"480\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/904\\/movie480_vp9.webm?t=1732069598\",\"max\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/904\\/movie_max_vp9.webm?t=1732069598\"},\"mp4\":{\"480\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/904\\/movie480.mp4?t=1732069598\",\"max\":\"http:\\/\\/video.akamai.steamstatic.com\\/store_trailers\\/904\\/movie_max.mp4?t=1732069598\"},\"highlight\":false}],\"recommendations\":{\"total\":177653},\"achievements\":{\"total\":69,\"highlighted\":[{\"name\":\"Defiant\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_hit_cancop_withcan.jpg\"},{\"name\":\"Submissive\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_put_canintrash.jpg\"},{\"name\":\"Malcontent\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_escape_apartmentraid.jpg\"},{\"name\":\"What cat?\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_break_miniteleporter.jpg\"},{\"name\":\"Trusty Hardware\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_get_crowbar.jpg\"},{\"name\":\"Barnacle Bowling\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_kill_barnacleswithbarrel.jpg\"},{\"name\":\"Anchor's Aweigh!\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_get_airboat.jpg\"},{\"name\":\"Heavy Weapons\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_get_airboatgun.jpg\"},{\"name\":\"Vorticough\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_find_vortigauntcave.jpg\"},{\"name\":\"Revenge!\",\"path\":\"https:\\/\\/cdn.akamai.steamstatic.com\\/steamcommunity\\/public\\/images\\/apps\\/220\\/hl2_kill_chopper.jpg\"}]},\"release_date\":{\"coming_soon\":false,\"date\":\"16 Nov, 2004\"},\"support_info\":{\"url\":\"http:\\/\\/steamcommunity.com\\/app\\/220\",\"email\":\"\"},\"background\":\"https:\\/\\/store.akamai.steamstatic.com\\/images\\/storepagebackground\\/app\\/220?t=1737139959\",\"background_raw\":\"https:\\/\\/shared.akamai.steamstatic.com\\/store_item_assets\\/steam\\/apps\\/220\\/page_bg_raw.jpg?t=1737139959\",\"content_descriptors\":{\"ids\":[2,5],\"notes\":\"Half-Life 2 includes violence throughout the game.\"},\"ratings\":{\"usk\":{\"rating\":\"18\"},\"agcom\":{\"rating\":\"16\",\"descriptors\":\"Violenza\\r\\nPaura\"},\"dejus\":{\"rating_generated\":\"1\",\"rating\":\"14\",\"required_age\":\"14\",\"banned\":\"0\",\"use_age_gate\":\"0\",\"descriptors\":\"Violência\"},\"steam_germany\":{\"rating_generated\":\"1\",\"rating\":\"16\",\"required_age\":\"16\",\"banned\":\"0\",\"use_age_gate\":\"0\",\"descriptors\":\"Drastische Gewalt\"}}}}"
    val json = Json { ignoreUnknownKeys = true }
    val gameData = json.decodeFromString<SteamWebApiAppDetailsResponse>(gameRawData)
    SteamCompanionTheme {
        GameDetailCard(gameData = GameData(content = gameData))
    }
}