@file:Suppress("AnimateAsStateLabel")

package com.prime.media

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.NavigationRail
import androidx.compose.material.SelectableChipColors
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.prime.media.about.AboutUs
import com.prime.media.console.Console
import com.prime.media.console.PopupMedia
import com.prime.media.core.Anim
import com.prime.media.core.ContentPadding
import com.prime.media.core.NightMode
import com.prime.media.core.compose.Channel
import com.prime.media.core.compose.LocalNavController
import com.prime.media.core.compose.LocalSystemFacade
import com.prime.media.core.compose.LocalWindowSize
import com.prime.media.core.compose.NavigationBarItem
import com.prime.media.core.compose.NavigationDrawerItem
import com.prime.media.core.compose.NavigationItemDefaults
import com.prime.media.core.compose.NavigationRailItem
import com.prime.media.core.compose.NavigationSuiteScaffold
import com.prime.media.core.compose.Placeholder
import com.prime.media.core.compose.Range
import com.prime.media.core.compose.WindowSize
import com.prime.media.core.compose.current
import com.prime.media.core.compose.preference
import com.prime.media.core.playback.MediaItem
import com.prime.media.directory.playlists.Members
import com.prime.media.directory.playlists.MembersViewModel
import com.prime.media.directory.playlists.Playlists
import com.prime.media.directory.playlists.PlaylistsViewModel
import com.prime.media.directory.store.Albums
import com.prime.media.directory.store.AlbumsViewModel
import com.prime.media.directory.store.Artists
import com.prime.media.directory.store.ArtistsViewModel
import com.prime.media.directory.store.Audios
import com.prime.media.directory.store.AudiosViewModel
import com.prime.media.directory.store.Folders
import com.prime.media.directory.store.FoldersViewModel
import com.prime.media.directory.store.Genres
import com.prime.media.directory.store.GenresViewModel
import com.prime.media.editor.TagEditor
import com.prime.media.effects.AudioFx
import com.prime.media.impl.AudioFxViewModel
import com.prime.media.impl.ConsoleViewModel
import com.prime.media.impl.LibraryViewModel
import com.prime.media.impl.SettingsViewModel
import com.prime.media.impl.TagEditorViewModel
import com.prime.media.library.Library
import com.prime.media.settings.Settings
import com.primex.core.Amber
import com.primex.core.BlueLilac
import com.primex.core.DahliaYellow
import com.primex.core.OrientRed
import com.primex.core.SignalWhite
import com.primex.core.TrafficBlack
import com.primex.core.UmbraGrey
import com.primex.core.blend
import com.primex.core.hsl
import com.primex.core.textResource
import com.primex.material2.Label
import com.primex.material2.OutlinedButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ln

private const val TAG = "Home"

/**
 * A short-hand alias of [MaterialTheme]
 */
typealias Material = MaterialTheme

/**
 * A variant of caption.
 */
private val caption2 =
    TextStyle(fontWeight = FontWeight.Normal, fontSize = 10.sp, letterSpacing = 0.4.sp)

/**
 * A variant of [caption] with a smaller font size and tighter letter spacing.
 * Use this style for captions that require less emphasis or in situations where space is constrained.
 *
 * @see caption
 */
val Typography.caption2 get() = com.prime.media.caption2

/**
 * The alpha value for the container colors.
 *
 * This constant value represents the alpha (transparency) of the container colors in the current
 * [MaterialTheme]. The value is a Float between 0.0 and 1.0, where 0.0 is completely transparent
 * and 1.0 is completely opaque. This value can be used to adjust the transparency of container
 * backgrounds and other elements in your app that use the container color.
 */
@Deprecated("The reason for deprivation is that it is cumbersome to use.")
val MaterialTheme.CONTAINER_COLOR_ALPHA get() = 0.15f

/**
 * A variant of [MaterialTheme.shapes.small] with a corner radius of 8dp.
 */
private val small2 = RoundedCornerShape(8.dp)

/**
 * A variant of [MaterialTheme.shapes.small] with a radius of 8dp.
 */
val Shapes.small2 get() = com.prime.media.small2

/**
 * The overlay color used for backgrounds and shadows.
 * The color is black with alpha 0.04 on light themes and white with alpha 0.04 on dark themes.
 */
val Colors.overlay
    @Composable inline get() = if (isLight) Color.Black.copy(0.04f) else Color.White.copy(0.01f)

/**
 * The outline color used in the light/dark theme.
 *
 * The color is semi-transparent white/black, depending on the current theme, with an alpha of 0.12.
 */
inline val Colors.outline
    get() = (if (isLight) Color.Black else Color.White).copy(0.12f)

val Colors.lightShadowColor
    @Composable inline get() = if (isLight) Color.White else Color.White.copy(0.025f)
val Colors.darkShadowColor
    @Composable inline get() = if (isLight) Color(0xFFAEAEC0).copy(0.7f) else Color.Black.copy(0.6f)

/**
 * Computes the tonal color at different elevation levels for the [background] color.
 *
 * This function calculates the tonal elevation effect by adjusting the alpha of the
 * [Colors.primary] color overlaid on the [background] color. The resulting color is
 * influenced by the logarithmic function.
 *
 * @param background The base color on which the tonal elevation is applied.
 * @param elevation  Elevation value used to compute the alpha of the color overlay layer.
 *
 * @return The [background] color with an alpha overlay of the [Colors.primary] color.
 * @see applyTonalElevation
 */
private fun Colors.applyTonalElevation(
    background: Color,
    elevation: Dp
) = primary.copy(alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f).compositeOver(background)

/**
 * @see applyTonalElevation
 */
fun Colors.surfaceColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return surface
    return applyTonalElevation(surface, elevation)
}

/**
 * @see applyTonalElevation
 */
fun Colors.backgroundColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return background
    return applyTonalElevation(background, elevation)
}

/**
 * Returns true if the system bars are required to be light-themed, false otherwise.
 * @see WindowInsetsControllerCompat.isAppearanceLightStatusBars
 */
inline val Colors.isAppearanceLightSystemBars
    @Composable inline get() = isLight && !preference(key = Settings.COLOR_STATUS_BAR).value

/**
 * A simple composable that helps in resolving the current app theme as suggested by the [Gallery.NIGHT_MODE]
 */
@Composable
@NonRestartableComposable
private fun isPrefDarkTheme(): Boolean {
    val mode by preference(key = Settings.NIGHT_MODE)
    return when (mode) {
        NightMode.YES -> true
        NightMode.FOLLOW_SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
        else -> false
    }
}

// Default Enter/Exit Transitions.
@OptIn(ExperimentalAnimationApi::class)
private val EnterTransition =
    scaleIn(tween(220, 90), 0.98f) + fadeIn(tween(700))
private val ExitTransition = fadeOut(tween(700))

/**
 * The route to permission screen.
 */
private const val PERMISSION_ROUTE = "_route_storage_permission"

/**
 * The permission screen.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Permission() {
    val controller = LocalNavController.current
    // Compose the permission state.
    // Once granted set different route like folders as start.
    // Navigate from here to there.
    val permission = rememberPermissionState(permission = Audiofy.STORAGE_PERMISSION) {
        if (!it) return@rememberPermissionState
        controller.graph.setStartDestination(Library.route)
        controller.navigate(Library.route) { popUpTo(PERMISSION_ROUTE) { inclusive = true } }
    }
    Placeholder(
        iconResId = R.raw.lt_permission,
        title = stringResource(R.string.permission_screen_title),
        message = stringResource(R.string.permission_screen_desc),
        vertical = LocalWindowSize.current.widthRange == Range.Compact
    ) {
        OutlinedButton(
            onClick = { permission.launchPermissionRequest() },
            modifier = Modifier.size(width = 200.dp, height = 46.dp),
            elevation = null,
            label = stringResource(R.string.allow),
            border = ButtonDefaults.outlinedBorder,
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent)
        )
    }
}

private val DefaultColorSpec = tween<Color>(Anim.DefaultDurationMillis)

/**
 * Defines theme for app.
 *
 * @param darkTheme Whether to use the dark theme.
 * @param content The content to be displayed.
 */
@Composable
private fun Material(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val background by animateColorAsState(
        targetValue = if (darkTheme) Color(0xFF0E0E0F) else Color(0xFFF5F5FA),
        animationSpec = DefaultColorSpec
    )
    val surface by animateColorAsState(
        targetValue = if (darkTheme) Color.TrafficBlack else Color.White,
        animationSpec = DefaultColorSpec
    )

    val primary = if (darkTheme) Color.Amber else Color.BlueLilac
    val secondary = if (darkTheme) Color.DahliaYellow else Color(0xFF008000)
    val colors = Colors(
        primary = if (darkTheme) Color.Amber else Color.BlueLilac,
        secondary = if (darkTheme) Color.DahliaYellow else Color(0xFF008000),
        background = background,
        surface = surface,
        primaryVariant = primary.hsl(lightness = 0.25f), // make a bit darker.
        secondaryVariant = secondary.hsl(lightness = 0.25f),
        onPrimary = Color.SignalWhite,
        onSurface = if (darkTheme) Color.SignalWhite else Color.UmbraGrey,
        onBackground = if (darkTheme) Color.SignalWhite else Color.UmbraGrey,
        error = Color.OrientRed,
        onSecondary = Color.SignalWhite,
        onError = Color.SignalWhite,
        isLight = !darkTheme
    )

    // Actual theme compose; in future handle fonts etc.
    MaterialTheme(
        colors = colors,
        content = content,
        typography = Typography(Settings.DefaultFontFamily)
    )

    // This block handles the logic of color of SystemBars.
    val view = LocalView.current
    // If the application is in edit mode, we do not need to handle status_bar related tasks, so we return early.
    if (view.isInEditMode) return@Material
    // Update the system bars appearance with a delay to avoid splash screen issue.
    // Use flag to avoid hitting delay multiple times.
    var isFirstPass by remember { mutableStateOf(true) }
    val colorSystemBars by preference(key = Settings.COLOR_STATUS_BAR)
    val hideStatusBar by preference(key = Settings.HIDE_STATUS_BAR)
    val color = when (colorSystemBars) {
        false -> Color.Transparent
        else -> colors.primaryVariant
    }
    val isAppearanceLightSystemBars = !darkTheme && !colorSystemBars
    LaunchedEffect(isAppearanceLightSystemBars, hideStatusBar) {
        // A Small Delay to override the change of system bar after splash screen.
        // This is a workaround for a problem with using sideEffect to hideSystemBars.
        if (isFirstPass) {
            delay(2500)
            isFirstPass = false
        }
        val window = (view.context as Activity).window
        // Obtain the controller for managing the insets of the window.
        val controller = WindowCompat.getInsetsController(window, view)
        window.navigationBarColor = color.toArgb()
        window.statusBarColor = color.toArgb()
        // Set the color of the navigation bar and the status bar to the determined color.
        controller.isAppearanceLightStatusBars = isAppearanceLightSystemBars
        controller.isAppearanceLightNavigationBars = isAppearanceLightSystemBars
        // Hide or show the status bar based on the user's preference.
        if (hideStatusBar)
            controller.hide(WindowInsetsCompat.Type.statusBars())
        else
            controller.show(WindowInsetsCompat.Type.statusBars())
    }
}

/**
 * A simple structure of the NavGraph.
 */
@NonRestartableComposable
@Composable
private fun NavGraph(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Load start destination based on if storage permission is set or not.
    val startDestination =
        when (ContextCompat.checkSelfPermission(context, Audiofy.STORAGE_PERMISSION)) {
            PackageManager.PERMISSION_GRANTED -> Library.route
            else -> PERMISSION_ROUTE
        }
    // In order to navigate and remove the need to pass controller below UI components.
    NavHost(
        navController = LocalNavController.current,
        modifier = modifier,
        startDestination = startDestination, //
        enterTransition = { EnterTransition },
        exitTransition = { ExitTransition },
        builder = {
            //AboutUs
            composable(AboutUs.route){
                AboutUs()
            }
            //Permission
            composable(PERMISSION_ROUTE) {
                Permission()
            }
            composable(Library.route) {
                val viewModel = hiltViewModel<LibraryViewModel>()
                Library(viewModel)
            }
            composable(Settings.route) {
                val viewModel = hiltViewModel<SettingsViewModel>()
                Settings(state = viewModel)
            }

            composable(Albums.route) {
                val viewModel = hiltViewModel<AlbumsViewModel>()
                Albums(viewModel = viewModel)
            }

            composable(Artists.route) {
                val viewModel = hiltViewModel<ArtistsViewModel>()
                Artists(viewModel = viewModel)
            }

            composable(Audios.route) {
                val viewModel = hiltViewModel<AudiosViewModel>()
                Audios(viewModel = viewModel)
            }

            composable(Folders.route) {
                val viewModel = hiltViewModel<FoldersViewModel>()
                Folders(viewModel = viewModel)
            }

            composable(Genres.route) {
                val viewModel = hiltViewModel<GenresViewModel>()
                Genres(viewModel = viewModel)
            }

            composable(Playlists.route) {
                val viewModel = hiltViewModel<PlaylistsViewModel>()
                Playlists(viewModel = viewModel)
            }

            composable(Members.route) {
                val viewModel = hiltViewModel<MembersViewModel>()
                Members(viewModel = viewModel)
            }

            composable(TagEditor.route) {
                val viewModel = hiltViewModel<TagEditorViewModel>()
                TagEditor(state = viewModel)
            }

            dialog(AudioFx.route) {
                val viewModel = hiltViewModel<AudioFxViewModel>()
                AudioFx(state = viewModel)
            }

            composable(Console.route) {
                val viewModel = hiltViewModel<ConsoleViewModel>()
                Console(state = viewModel)
            }
        },
    )
}

/**
 * The array of routes that are required to hide the miniplayer.
 */
private val HIDDEN_DEST_ROUTES =
    arrayOf(Console.route, PERMISSION_ROUTE, AudioFx.route, AboutUs.route)

/**
 * Extension function for the NavController that facilitates navigation to a specified destination route.
 *
 * @param route The destination route to navigate to.
 *
 * This function uses the provided route to navigate using the navigation graph.
 * It includes additional configuration to manage the back stack and ensure a seamless navigation experience.
 * - It pops up to the start destination of the graph to avoid a buildup of destinations on the back stack.
 * - It uses the `launchSingleTop` flag to prevent multiple copies of the same destination when re-selecting an item.
 * - The `restoreState` flag is set to true, ensuring the restoration of state when re-selecting a previously selected item.
 */
private fun NavController.toRoute(route: String) {
    navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = true
    }
}

private const val MIME_TYPE_VIDEO = "video/*"

// The different NavTypes shown in differnrt screen sizes.
private val TYPE_RAIL_NAV = 0
private val TYPE_DRAWER_NAV = 1
private val TYPE_BOTTOM_NAV = 2

/**
 * return the navigation type based on the window size.
 */
private inline val WindowSize.navType
    get() = when {
        widthRange < Range.Medium -> TYPE_BOTTOM_NAV
        widthRange < Range.xLarge -> TYPE_RAIL_NAV
        // FixME - For now return only rail as drawer looks pretty bad.
        else -> TYPE_RAIL_NAV //TYPE_DRAWER_NAV
    }

private val NAV_RAIL_WIDTH = 96.dp
private val NAV_DRAWER_WIDTH = 256.dp

/**
 * Calculates an returns newWindowSizeClass after consuming sapce occupied by  [navType].
 *
 * @return consumed window class.
 * @see [navType]
 */
private inline val WindowSize.remaining
    get() = when {
        widthRange < Range.Medium -> consume(height = 56.dp)
        widthRange < Range.xLarge -> consume(width = NAV_RAIL_WIDTH)
        else -> consume(width = NAV_DRAWER_WIDTH)
    }

/**
 * Represents a navigation item either in a [NavigationRail] (when [isNavRail] is true)
 * or a [BottomNavigation] (when [isNavRail] is false).
 *
 * @param label The text label associated with the navigation item.
 * @param icon The vector graphic icon representing the navigation item.
 * @param onClick The callback function to be executed when the navigation item is clicked.
 * @param modifier The modifier for styling and layout customization of the navigation item.
 * @param checked Indicates whether the navigation item is currently selected.
 * @param isNavRail Specifies whether the navigation item is intended for a [NavigationRail].
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
@NonRestartableComposable
private fun NavigationItem(
    label: CharSequence,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    type: Int = TYPE_BOTTOM_NAV,
    colors: SelectableChipColors = NavigationItemDefaults.navigationItemColors()
) {
    val icon = @Composable {
        Icon(
            imageVector = icon,
            contentDescription = label.toString()
        )
    }
    val label = @Composable {
        Label(
            text = label,
        )
    }
    when (type) {
        TYPE_RAIL_NAV -> NavigationRailItem(
            onClick = onClick,
            icon = icon,
            label = label,
            modifier = modifier.scale(0.83f),
            checked = checked,
            colors = colors,
        )

        TYPE_DRAWER_NAV -> NavigationDrawerItem(
            onClick = onClick,
            icon = icon,
            label = label,
            modifier = modifier.scale(0.85f),
            checked = checked,
            colors = colors,
            shape = Material.shapes.small2
        )

        TYPE_BOTTOM_NAV -> NavigationBarItem(
            onClick = onClick,
            icon = icon,
            label = label,
            modifier = modifier.scale(0.83f),
            checked = checked,
            colors = colors
        )
    }
}

/**
 * A composable function that represents a navigation bar, combining both rail and bottom bar elements.
 *
 * @param type Specifies whether the navigation bar includes a [NavigationRail] or [BottomNavigation] component.
 * @param navController The NavController to manage navigation within the navigation bar.
 * @param modifier The modifier for styling and layout customization of the navigation bar.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
@NonRestartableComposable
private fun NavBar(
    type: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val routes = remember {
        movableContentOf {
            // Get the current navigation destination from NavController
            val current by navController.currentBackStackEntryAsState()
            val colors = NavigationItemDefaults.navigationItemColors(
                contentColor = Material.colors.onSurface,
            )
            // Home
            NavigationItem(
                label = textResource(R.string.home),
                icon = Icons.Outlined.Home,
                checked = current?.destination?.route == Library.route,
                onClick = { navController.toRoute(Library.direction()) },
                type = type,
                colors = colors
            )

            // Audios
            NavigationItem(
                label = textResource(id = R.string.folders),
                icon = Icons.Outlined.FolderCopy,
                checked = current?.destination?.route == Folders.route,
                onClick = { navController.toRoute(Folders.direction()) },
                type = type,
                colors = colors
            )

            // Videos
            val context = LocalContext.current as MainActivity
            val launcher =
                rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
                    if (it == null) return@rememberLauncherForActivityResult
                    val intnet = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(it, MIME_TYPE_VIDEO)
                        this.`package` = context.packageName
                    }
                    context.startActivity(intnet)
                }
            NavigationItem(
                label = textResource(id = R.string.videos),
                icon = Icons.Outlined.VideoLibrary,
                checked = false,
                onClick = { launcher.launch(arrayOf(MIME_TYPE_VIDEO)) },
                type = type,
                colors = colors
            )

            // Playlists
            NavigationItem(
                label = textResource(id = R.string.playlists),
                icon = Icons.Outlined.PlaylistPlay,
                checked = current?.destination?.route == Playlists.route,
                onClick = { navController.toRoute(Playlists.direction()) },
                type = type,
                colors = colors
            )

            // Settings
            NavigationItem(
                label = textResource(id = R.string.settings),
                icon = Icons.Outlined.Settings,
                checked = current?.destination?.route == Settings.route,
                onClick = { navController.toRoute(Settings.route) },
                type = type,
                colors = colors
            )
        }
    }
    when (type) {
        TYPE_BOTTOM_NAV -> BottomAppBar(
            modifier = modifier,
            windowInsets = WindowInsets.navigationBars,
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            contentPadding = PaddingValues(
                horizontal = ContentPadding.normal,
                vertical = ContentPadding.medium
            ),
            content = {
                var expanded by remember { mutableStateOf(false) }
                PopupMedia(
                    expanded = expanded,
                    onRequestToggle = { expanded = !expanded }
                )
                Spacer(Modifier.weight(1f))
                // Display routes at the contre of available space
                routes()
                Spacer(modifier = Modifier.weight(1f))
            }
        )

        else -> NavigationRail(
            modifier = modifier.width(if (type == TYPE_RAIL_NAV) NAV_RAIL_WIDTH else NAV_DRAWER_WIDTH),
            windowInsets = WindowInsets.systemBars,
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            content = {
                // Display routes at the top of the navRail.
                routes()
                // Some Space between naves and Icon.
                Spacer(modifier = Modifier.weight(1f))

                var expanded by remember { mutableStateOf(false) }
                PopupMedia(
                    expanded = expanded,
                    onRequestToggle = { expanded = !expanded },
                    offset = DpOffset(30.dp, (-30).dp),
                    modifier = Modifier
                        .align(if (type == TYPE_RAIL_NAV) Alignment.CenterHorizontally else Alignment.End)
                        .padding(horizontal = if (type == TYPE_RAIL_NAV) 0.dp else ContentPadding.normal)
                )
            },
        )
    }
}

/**
 * The shape of the content inside the scaffold.
 */
private val CONTENT_SHAPE = RoundedCornerShape(8)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Home(channel: Channel) {
    // Determine if the app is in dark mode based on user preferences
    val isDark = isPrefDarkTheme()
    val navController = rememberNavController()
    Material(isDark) {
        // Get the window size class
        val clazz = LocalWindowSize.current
        // Provide the navController, newWindowClass through LocalComposition.
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalWindowSize provides clazz.remaining,
            content = {
                // Determine the navigation type based on the window size class and access the system facade
                val facade = LocalSystemFacade.current
                // Determine whether to hide the navigation bar based on the current destination
                val hideNavigationBar = navController.current in HIDDEN_DEST_ROUTES
                NavigationSuiteScaffold(
                    vertical = clazz.widthRange < Range.Medium,
                    channel = channel,
                    hideNavigationBar = hideNavigationBar,
                    progress = facade.inAppUpdateProgress,
                    background = Material.colors.primary.blend(Material.colors.background, 0.96f),
                    // Set up the navigation bar using the NavBar composable
                    navBar = {
                        NavBar(
                            type = clazz.navType,
                            navController = navController
                        )
                    },
                    // Display the main content of the app using the NavGraph composable
                    content = {
                        NavGraph(
                            modifier = Modifier
                                .clip(CONTENT_SHAPE)
                                .background(Material.colors.background)
                                .fillMaxSize()
                        )
                    }
                )
            }
        )
    }
    // In this section, we handle incoming intents.
    // Intents can be of two types: video or audio. If it's a video intent,
    // we navigate to the video screen; otherwise, we play the media item in the MiniPlayer.
    // In both cases, we trigger a remote action to initiate playback.
    // Create a coroutine scope to handle asynchronous operations.
    val scope = rememberCoroutineScope()
    // Check if the current composition is in inspection mode.
    // Inspection mode is typically used during UI testing or debugging to isolate and analyze
    // specific UI components. If in inspection mode, return to avoid executing the rest of the code.
    if (LocalInspectionMode.current) return
    val activity = LocalView.current.context as MainActivity
    // Construct the DisposableEffect and listen for events.
    DisposableEffect(Unit) {
        // Create a listener for observing changes in incoming intents.
        val listener = listener@{ intent: Intent ->
            // Check if the intent action is not ACTION_VIEW; if so, return.
            if (intent.action != Intent.ACTION_VIEW)
                return@listener
            // Obtain the URI from the incoming intent data.
            val data = intent.data ?: return@listener
            // Use a coroutine to handle the media item construction and playback.
            scope.launch {
                // Construct a MediaItem using the obtained parameters.
                // (Currently, details about playback queue setup are missing.)
                val item = MediaItem(activity, data)
                // Play the media item by replacing the existing queue.
                activity.remote.set(listOf(item))
                activity.remote.play()
            }
            // If the intent is related to video content, navigate to the video player screen.
            navController.navigate(Console.direction())
        }
        val firebase = Firebase.analytics
        // Listen for navDest and log in firebase.
        val navDestChangeListener =
            { _: NavController, destination: NavDestination, _: Bundle? ->
                // create params for the event.
            val params = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, destination.route as String?)
                //putString(FirebaseAnalytics.Param.SCREEN_CLASS, destination.label as String?)
            }
            // Log the event.
            firebase.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params)
        }
        // Register the intent listener with the activity.
        activity.addOnNewIntentListener(listener)
        navController.addOnDestinationChangedListener(navDestChangeListener)
        // Unregister the intent listener when this composable is disposed.
        onDispose {
            activity.removeOnNewIntentListener(listener)
            navController.removeOnDestinationChangedListener(navDestChangeListener)
        }
    }
}

