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
import androidx.compose.material.icons.filled.Favorite
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
import org.orion.browser.ui.theme.DividerDark
import org.orion.browser.ui.theme.ShieldAmber
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
    onSearchClick: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    onSupportDevClick: () -> Unit = {}
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

        // Privacy Stats Grid (Odometer)
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

        Spacer(modifier = Modifier.height(28.dp))

        // Developer & Support Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSupportDevClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TorSlate),
            border = androidx.compose.foundation.BorderStroke(1.dp, DividerDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ShieldAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = ShieldAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Built by Abhinav Santhosh",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% Free & Private • Tap to Support 💛",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
                Text(
                    text = "Donate →",
                    color = TorPurpleBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
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
