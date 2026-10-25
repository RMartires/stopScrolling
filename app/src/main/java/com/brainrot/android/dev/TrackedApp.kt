package com.brainrot.android.dev

enum class TrackedApp(
    val appId: String,
    val label: String,
    val packageName: String,
) {
    YOUTUBE("youtube", "YouTube", DevAccessibilityService.YOUTUBE_PACKAGE),
    INSTAGRAM("instagram", "Instagram", DevAccessibilityService.INSTAGRAM_PACKAGE),
    TIKTOK("tiktok", "TikTok", DevAccessibilityService.TIKTOK_PACKAGE),
    ;

    companion object {
        fun fromPackageOrAlt(pkg: String): TrackedApp? = when (pkg) {
            DevAccessibilityService.YOUTUBE_PACKAGE,
            DevAccessibilityService.YOUTUBE_PACKAGE_ALT,
            -> YOUTUBE
            DevAccessibilityService.INSTAGRAM_PACKAGE -> INSTAGRAM
            DevAccessibilityService.TIKTOK_PACKAGE -> TIKTOK
            else -> null
        }
    }
}
