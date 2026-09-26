package org.orion.browser.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.orion.browser.ui.theme.DividerDark
import org.orion.browser.ui.theme.ShieldGreen
import org.orion.browser.ui.theme.TextMuted
import org.orion.browser.ui.theme.TextPrimary
import org.orion.browser.ui.theme.TorObsidian
import org.orion.browser.ui.theme.TorPurple
import org.orion.browser.ui.theme.TorSlate

@Composable
fun BottomNavigationBar(
    currentUrl: String?,
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int,
    adsBlockedCount: Int,
    detectedMediaCount: Int,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onUrlClick: () -> Unit,
    onShieldClick: () -> Unit,
    onTabsClick: () -> Unit,
    onMediaDownloadClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TorObsidian)
    ) {
        // Subtle top divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DividerDark)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Back button
            IconButton(
                onClick = onBack,
                enabled = canGoBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = if (canGoBack) TextPrimary else TextMuted
                )
            }

            // Forward button
            IconButton(
                onClick = onForward,
                enabled = canGoForward,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Forward",
                    tint = if (canGoForward) TextPrimary else TextMuted
                )
            }

            // Search Bar / URL Pill in Center
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(TorSlate)
                    .clickable { onUrlClick() }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shield Icon with Ads blocked count
                Box(
                    modifier = Modifier
                        .clickable { onShieldClick() }
                        .padding(end = 6.dp)
                ) {
                    if (adsBlockedCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = ShieldGreen) {
                                    Text("$adsBlockedCount", fontSize = 9.sp, color = Color.White)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security Shield",
                                tint = ShieldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Security Shield",
                            tint = ShieldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = if (currentUrl.isNullOrEmpty() || currentUrl == "about:blank") "Search or enter address" else currentUrl,
                    color = if (currentUrl.isNullOrEmpty() || currentUrl == "about:blank") TextMuted else TextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // If media was detected, show glowing Video Download Icon
                if (detectedMediaCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = TorPurple) {
                                Text("$detectedMediaCount", fontSize = 10.sp, color = Color.White)
                            }
                        },
                        modifier = Modifier.clickable { onMediaDownloadClick() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = "Download Media",
                            tint = ShieldGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Tab Switcher Button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TorSlate)
                    .clickable { onTabsClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$tabCount",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // More Options Menu
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = TextPrimary
                )
            }
        }
    }
}
