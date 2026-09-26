package org.orion.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.orion.browser.rewards.SponsoredCard
import org.orion.browser.ui.theme.DividerDark
import org.orion.browser.ui.theme.ShieldGreen
import org.orion.browser.ui.theme.TextMuted
import org.orion.browser.ui.theme.TextPrimary
import org.orion.browser.ui.theme.TextSecondary
import org.orion.browser.ui.theme.TorObsidian
import org.orion.browser.ui.theme.TorPurple
import org.orion.browser.ui.theme.TorPurpleBright
import org.orion.browser.ui.theme.TorSlate
import org.orion.browser.ui.theme.TorSlateLight

@Composable
fun NewTabPageView(
    adsBlockedTotal: Int,
    trackersBlockedTotal: Int,
    userPoints: Int,
    estimatedValue: String,
    sponsoredCards: List<SponsoredCard>,
    onSearchClick: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    onSponsoredCardClick: (SponsoredCard) -> Unit,
    onClaimDailyReward: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TorObsidian)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Orion Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(TorPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "ORION",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 3.sp,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brave-style Privacy Stats Grid (Odometer)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(TorSlate)
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val totalBlocked = adsBlockedTotal + trackersBlockedTotal
            StatItem(
                value = "$totalBlocked",
                label = "Trackers Blocked",
                icon = Icons.Default.Shield,
                color = ShieldGreen
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(DividerDark)
            )
            StatItem(
                value = "${(adsBlockedTotal * 0.15).toInt()} MB",
                label = "Bandwidth Saved",
                icon = Icons.Default.Language,
                color = TorPurpleBright
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(36.dp)
                    .background(DividerDark)
            )
            StatItem(
                value = "${(adsBlockedTotal * 0.05).toInt()}s",
                label = "Time Saved",
                icon = Icons.Default.Timer,
                color = Color(0xFF3498DB)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Input Bar (Prominent center search)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(TorSlate)
                .border(1.dp, DividerDark, RoundedCornerShape(26.dp))
                .clickable { onSearchClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TorPurpleBright,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search or type URL…",
                color = TextMuted,
                fontSize = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Bookmarks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickBookmarkItem("DuckDuckGo", Icons.Default.Search, "https://duckduckgo.com", onBookmarkClick)
            QuickBookmarkItem("Tor Project", Icons.Default.Security, "https://torproject.org", onBookmarkClick)
            QuickBookmarkItem("Wikipedia", Icons.Default.Public, "https://wikipedia.org", onBookmarkClick)
            QuickBookmarkItem("GitHub", Icons.Default.Code, "https://github.com", onBookmarkClick)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Orion Rewards Ledger Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TorSlate),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF39C12).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF39C12),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Orion Rewards",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$userPoints Points ($estimatedValue USD)",
                            color = Color(0xFFF39C12),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(TorPurple)
                        .clickable { onClaimDailyReward() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+25 Daily",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Non-intrusive Compliant Sponsored Card (Policy Compliant)
        if (sponsoredCards.isNotEmpty()) {
            val sponsor = sponsoredCards.first()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSponsoredCardClick(sponsor) },
                colors = CardDefaults.cardColors(containerColor = TorSlateLight),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPONSORED PARTNER",
                            color = TorPurpleBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "+${sponsor.rewardPoints} Points",
                            color = ShieldGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = sponsor.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = sponsor.description,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickBookmarkItem(
    title: String,
    icon: ImageVector,
    url: String,
    onClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick(url) }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(TorSlate),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp
        )
    }
}
