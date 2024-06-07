package dunbar.mike.mediabrowser.data.user

data class UserData(
    val shouldHideOnboarding: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)

enum class DarkThemeConfig {
    LIGHT,
    DARK,
    SYSTEM_SETTING,
}