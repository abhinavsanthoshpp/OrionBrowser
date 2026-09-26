package org.orion.browser.rewards

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SponsoredCard(
    val title: String,
    val description: String,
    val sponsorName: String,
    val targetUrl: String,
    val rewardPoints: Int
)

object OrionRewardsManager {

    private const val PREFS_NAME = "orion_rewards_prefs"
    private const val KEY_POINTS = "user_orion_points"
    private const val KEY_TIME_SAVED_SEC = "time_saved_seconds"

    private lateinit var prefs: SharedPreferences

    private val _userPoints = MutableStateFlow(150) // Initial welcome bonus
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    private val _sponsoredCards = MutableStateFlow<List<SponsoredCard>>(emptyList())
    val sponsoredCards: StateFlow<List<SponsoredCard>> = _sponsoredCards.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _userPoints.value = prefs.getInt(KEY_POINTS, 150)

        // Load compliant partner cards for New Tab Page (Brave style)
        _sponsoredCards.value = listOf(
            SponsoredCard(
                title = "Proton VPN - Encrypt Your Internet",
                description = "High-speed Swiss VPN with zero logs and strict privacy laws.",
                sponsorName = "Proton AG",
                targetUrl = "https://protonvpn.com",
                rewardPoints = 25
            ),
            SponsoredCard(
                title = "DuckDuckGo - Privacy, Simplified",
                description = "Search without being tracked by advertisers.",
                sponsorName = "DuckDuckGo",
                targetUrl = "https://duckduckgo.com",
                rewardPoints = 20
            ),
            SponsoredCard(
                title = "Bitwarden - Open Source Password Manager",
                description = "Securely generate and store all your passwords across all devices.",
                sponsorName = "Bitwarden Inc.",
                targetUrl = "https://bitwarden.com",
                rewardPoints = 30
            )
        )
    }

    fun addPoints(amount: Int) {
        val updated = _userPoints.value + amount
        _userPoints.value = updated
        prefs.edit().putInt(KEY_POINTS, updated).apply()
    }

    fun getEstimatedUsdValue(): String {
        // 1000 points = $1.00 USD
        val usd = _userPoints.value / 1000.0
        return String.format("$%.2f", usd)
    }
}
