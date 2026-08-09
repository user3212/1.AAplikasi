package com.example.ui.components
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.filled.Info
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AbsenABg
import com.example.ui.theme.AbsenAText
import com.example.ui.theme.AbsenHBg
import com.example.ui.theme.AbsenHText
import com.example.ui.theme.AbsenIBg
import com.example.ui.theme.AbsenIText
import com.example.ui.theme.AbsenSBg
import com.example.ui.theme.AbsenSText
import com.example.ui.theme.AbsenTBg
import com.example.ui.theme.AbsenTText

// Section H & I: HERO BANNER & GLASSMORPHISM ICON
@Composable
fun ModuleHeaderBanner(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String? = "TAHFIZ",
    startColor: Color = Color(0xFF064E3B),
    endColor: Color = Color(0xFF047857),
    borderColor: Color = Color(0xFF10B981),
    rightActions: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = startColor,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(startColor, endColor)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Title block with Icon and Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 19.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (badgeText != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                                        .border(1.dp, Color(0xFF34D399).copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        maxLines = 1,
                                        softWrap = false,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF6EE7B7),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                            Text(
                                text = subtitle,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                // Right Actions
                if (rightActions != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    rightActions()
                }
            }
        }
    }
}

@Composable
fun HeroBanner(
    title: String,
    description: String,
    icon: ImageVector,
    startColor: Color,
    endColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(startColor, endColor)
                )
            )
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        // Section H: Decorative Glowing Orb (Top-Right)
        Box(
            modifier = Modifier
                .size(128.dp)
                .align(Alignment.TopEnd)
                .offset(x = 30.dp, y = (-30).dp)
                .blur(32.dp)
                .background(Color.White.copy(alpha = 0.12f), CircleShape)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Section I: Glassmorphism Icon Container
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

// Section C & P: PESANTREN CARD WITH ACCENT VERTICAL BAR
@Composable
fun PesantrenCard(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    accentColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(androidx.compose.foundation.layout.IntrinsicSize.Min)) {
            // Section J: Accent Card Vertical Bar on left edge
            if (accentColor != null) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterStart)
                        .background(accentColor)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = if (accentColor != null) 14.dp else 12.dp,
                        end = 12.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
            ) {
                content()
            }
        }
    }
}

// Section C: PESANTREN CARD (Expandable)
@Composable
fun ExpandablePesantrenCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    initiallyExpanded: Boolean = false,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            
            if (expanded) {
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), thickness = 1.dp)
                Box(modifier = Modifier.padding(14.dp)) {
                    content()
                }
            }
        }
    }
}

// Section N: FORM INPUT FIELD
@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    focusAccentColor: Color = MaterialTheme.colorScheme.primary,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = focusAccentColor,
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

// Section O: CUSTOM DROPDOWN SELECT
@Composable
fun <T> CustomDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionToString: (T) -> String,
    focusAccentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    testTag: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredOptions = remember(options, searchQuery, optionToString) {
        if (searchQuery.isBlank()) options
        else options.filter { optionToString(it).contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = if (expanded) focusAccentColor else Color(0xFFCBD5E1),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag(testTag)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedOption?.let { optionToString(it) } ?: if (placeholder.isNotBlank()) placeholder else if (label.isNotBlank()) "Pilih $label..." else "Pilih...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (selectedOption != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Dropdown Modal / Dialog for reliable Z-Index (Z-60)
        if (expanded) {
            Dialog(onDismissRequest = { expanded = false }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Pilih $label",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Search input within dropdown
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.background,
                                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                                focusedBorderColor = focusAccentColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        // Option list with Zebra row striping (Section O)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            itemsIndexed(filteredOptions) { index, item ->
                                val isSelected = item == selectedOption
                                val isEven = index % 2 == 0
                                val rowBg = when {
                                    isSelected -> focusAccentColor.copy(alpha = 0.1f)
                                    isEven -> MaterialTheme.colorScheme.surface
                                    else -> MaterialTheme.colorScheme.background
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(rowBg)
                                        .clickable {
                                            onOptionSelected(item)
                                            expanded = false
                                            searchQuery = ""
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = optionToString(item),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = if (isSelected) focusAccentColor else MaterialTheme.colorScheme.onBackground,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = focusAccentColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Section M: ABSENSI INDICATOR BADGE & SELECTOR
@Composable
fun AttendanceBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "H", "HADIR" -> Triple(AbsenHBg, AbsenHText, "HADIR")
        "T", "TELAT" -> Triple(AbsenTBg, AbsenTText, "TELAT")
        "I", "IZIN" -> Triple(AbsenIBg, AbsenIText, "IZIN")
        "S", "SAKIT" -> Triple(AbsenSBg, AbsenSText, "SAKIT")
        "A", "ALPA" -> Triple(AbsenABg, AbsenAText, "ALPA")
        else -> Triple(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.onSurfaceVariant, status)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun AttendanceSelector(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses = listOf(
        Triple("H", "Hadir", AbsenHText to AbsenHBg),
        Triple("T", "Telat", AbsenTText to AbsenTBg),
        Triple("I", "Izin", AbsenIText to AbsenIBg),
        Triple("S", "Sakit", AbsenSText to AbsenSBg),
        Triple("A", "Alpa", AbsenAText to AbsenABg)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        statuses.forEach { (code, label, colors) ->
            val isSelected = selectedStatus.equals(code, ignoreCase = true)
            val (textColor, bgColor) = colors

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) bgColor else MaterialTheme.colorScheme.background)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) textColor else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onStatusSelected(code) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) textColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) textColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

// Section G & J: TOAST NOTIFICATION BANNER (Z-100)
@Composable
fun ToastNotification(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon_transition")
    val neonColor by infiniteTransition.animateColor(
        initialValue = accentColor,
        targetValue = Color(0xFF00FFCC), // Neon Cyan
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_color"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(600)) + expandVertically(animationSpec = tween(600), expandFrom = Alignment.Top),
            exit = fadeOut(animationSpec = tween(600)) + shrinkVertically(animationSpec = tween(600), shrinkTowards = Alignment.Top)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(22.dp), // Sudut melengkung
                color = MaterialTheme.colorScheme.onBackground,
                shadowElevation = 8.dp, // Lebih 3D dan smooth
                border = BorderStroke(1.5.dp, neonColor) // Transisi warna neon di outline
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = neonColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

// Section E: EXCEL SMART PARSER DIALOG (EdTech Professional Grade)
@Composable
fun ExcelSmartParserDialog(
    title: String = "Smart Excel Parser",
    subtitle: String = "Ekstrak & Kelompokkan Rombel Otomatis",
    samplePresets: List<Pair<String, String>> = listOf(
        "Sample 7A - 7C" to "Ahmad Yazid, 7A\nAl Faiz Zoka, 7A\nAlvin Pratista, 7A\nAnanda Ryla Kenzi, 7B\nArifqy, 7B\nFadlan Al Fatirh, 7C\nGhiran Ramadani, 7C",
        "Sample 8A - 8B" to "Budi Santoso, 8A\nCandra Wijaya, 8A\nDedi Mulyadi, 8B\nEko Prasetyo, 8B"
    ),
    onDismiss: () -> Unit,
    onImport: (List<Pair<String, String>>) -> Unit
) {
    var rawText by remember {
        mutableStateOf(samplePresets.firstOrNull()?.second ?: "")
    }
    var selectedTabRombel by remember { mutableStateOf("Semua") }

    // Parse logic
    val parsedRecords = remember(rawText) {
        val list = mutableListOf<Pair<String, String>>()
        rawText.lines().forEach { line ->
            val clean = line.trim()
            if (clean.isNotBlank()) {
                val parts = if (clean.contains(",")) clean.split(",")
                else if (clean.contains("\t")) clean.split("\t")
                else clean.split(" ")

                if (parts.size >= 2) {
                    val nama = parts.dropLast(1).joinToString(" ").trim()
                    val rombel = parts.last().trim().uppercase()
                    if (nama.isNotBlank() && rombel.isNotBlank()) {
                        list.add(nama to rombel)
                    }
                } else if (parts.size == 1) {
                    list.add(parts[0].trim() to "7A")
                }
            }
        }
        list
    }

    // Distinct Rombels for Tabsheet grouping (no duplicates!)
    val distinctRombels = remember(parsedRecords) {
        listOf("Semua") + parsedRecords.map { it.second }.distinct().sorted()
    }

    val filteredRecords = remember(parsedRecords, selectedTabRombel) {
        if (selectedTabRombel == "Semua") parsedRecords
        else parsedRecords.filter { it.second == selectedTabRombel }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Banner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${parsedRecords.size} SISWA DETEKSI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // Preset Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    samplePresets.forEach { (presetLabel, presetContent) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outline)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .clickable {
                                    rawText = presetContent
                                    selectedTabRombel = "Semua"
                                }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡ $presetLabel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                // Input Text area for raw CSV / Excel lines
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PASTE TEKS EXCEL / CSV (Format: Nama, Kelas):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )

                    OutlinedTextField(
                        value = rawText,
                        onValueChange = { rawText = it },
                        singleLine = false,
                        maxLines = 4,
                        placeholder = { Text("Ahmad Yazid, 7A\nAl Faiz Zoka, 7A", fontSize = 11.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedBorderColor = Color(0xFF10B981)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }

                // Rombel TabSheets Grouping Bar (Smart Deduplication)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "TABEL PRATINJAU ROMBEL (TAB SHEET SMART PARSER):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        distinctRombels.take(5).forEach { r ->
                            val isSel = selectedTabRombel == r
                            val count = if (r == "Semua") parsedRecords.size else parsedRecords.count { it.second == r }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) Color(0xFF047857) else MaterialTheme.colorScheme.outline)
                                    .clickable { selectedTabRombel = r }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (r == "Semua") "Semua ($count)" else "Kelas $r ($count)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Preview List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(filteredRecords) { idx, pair ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${idx + 1}. ${pair.first}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFECFDF5))
                                    .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = pair.second,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF047857),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Import Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { onDismiss() }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Batal", style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF047857))
                            .clickable {
                                if (parsedRecords.isNotEmpty()) {
                                    onImport(parsedRecords)
                                    onDismiss()
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "↑ Impor Data (${parsedRecords.size} Siswa)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun AlarmPopupNotification(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LaunchedEffect(isVisible) {
        if (isVisible) {
            com.example.ui.util.SoundHelper.playSound(context, "Nada 1", 3)
        } else {
            com.example.ui.util.SoundHelper.stopSound()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = androidx.compose.animation.slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF2F2), // Light red bg
                shadowElevation = 16.dp,
                modifier = Modifier
                    .shadow(16.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                    .clickable { onDismiss() }
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Alarm",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Pengingat Jadwal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingOverlay(
    message: String,
    isVisible: Boolean
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(animationSpec = tween(400, delayMillis = 100)),
                exit = scaleOut(animationSpec = tween(300))
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF10B981),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ModernDatePickerDialog(
    initialDateMillis: Long?,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnClickOutside = true
        )
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .scale(0.9f)
                .shadow(24.dp, androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color.White,
                            androidx.compose.ui.graphics.Color(0xFFF0FDF4)
                        )
                    )
                )
                .border(2.dp, androidx.compose.ui.graphics.Color(0xFF6EE7B7).copy(alpha=0.5f), androidx.compose.foundation.shape.RoundedCornerShape(24.dp))
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                // Header 3D Effect
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    androidx.compose.ui.graphics.Color(0xFF10B981),
                                    androidx.compose.ui.graphics.Color(0xFF047857)
                                )
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "Pilih Tanggal",
                        color = androidx.compose.ui.graphics.Color.White,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                                offset = androidx.compose.ui.geometry.Offset(1f, 2f),
                                blurRadius = 3f
                            )
                        )
                    )
                }

                androidx.compose.material3.DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    colors = androidx.compose.material3.DatePickerDefaults.colors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        titleContentColor = androidx.compose.ui.graphics.Color(0xFF064E3B),
                        headlineContentColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        todayDateBorderColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                        todayContentColor = androidx.compose.ui.graphics.Color(0xFF10B981),
                        selectedDayContainerColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        selectedDayContentColor = androidx.compose.ui.graphics.Color.White,
                        dayContentColor = androidx.compose.ui.graphics.Color.DarkGray,
                        currentYearContentColor = androidx.compose.ui.graphics.Color(0xFF047857),
                        selectedYearContainerColor = androidx.compose.ui.graphics.Color(0xFF047857)
                    ),
                    modifier = androidx.compose.ui.Modifier.padding(top = 8.dp)
                )

                androidx.compose.foundation.layout.Row(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = onDismiss,
                        modifier = androidx.compose.ui.Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.Text("Batal", color = androidx.compose.ui.graphics.Color.Gray, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                    }
                    
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))

                    androidx.compose.material3.Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                            onDismiss()
                        },
                        modifier = androidx.compose.ui.Modifier
                            .weight(1f)
                            .shadow(6.dp, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFF10B981)
                        ),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
                    ) {
                        androidx.compose.material3.Text("Pilih", color = androidx.compose.ui.graphics.Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }
    }
}
