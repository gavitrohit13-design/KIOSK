package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DocumentPreviewer(
    fileName: String,
    fileType: String,
    pageCount: Int,
    colorMode: String,
    duplexMode: String,
    orientation: String,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(1) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    val isColor = colorMode == "COLOR"
    val isLandscape = orientation == "LANDSCAPE"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Preview Controls Header (Zoom, Rotate, Page counter)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page indicator
                Text(
                    text = "Page $currentPage of $pageCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                // Zoom & Rotate toolbar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (zoomLevel > 0.7f) zoomLevel -= 0.15f },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = "${(zoomLevel * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF334155)
                    )

                    IconButton(
                        onClick = { if (zoomLevel < 1.4f) zoomLevel += 0.15f },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { rotationAngle = (rotationAngle + 90f) % 360f },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90°", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Sheet Representation with shadows and realistic text lines
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFCBD5E1))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                val sheetWidth = if (isLandscape) 180.dp else 140.dp
                val sheetHeight = if (isLandscape) 130.dp else 190.dp

                Box(
                    modifier = Modifier
                        .size(sheetWidth, sheetHeight)
                        .scale(zoomLevel)
                        .rotate(rotationAngle)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFF94A3B8), RoundedCornerShape(4.dp))
                        .padding(10.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(sheetHeight)) {
                        val headerColor = if (isColor) Color(0xFF2563EB) else Color(0xFF1E293B)
                        val accentColor = if (isColor) Color(0xFF10B981) else Color(0xFF64748B)
                        val textColor = if (isColor) Color(0xFF475569) else Color(0xFF64748B)

                        // Title Bar
                        drawRoundRect(
                            color = headerColor,
                            topLeft = Offset(4f, 4f),
                            size = Size(size.width * 0.7f, 8f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )

                        // Subtitle
                        drawRoundRect(
                            color = accentColor,
                            topLeft = Offset(4f, 18f),
                            size = Size(size.width * 0.45f, 5f),
                            cornerRadius = CornerRadius(2f, 2f)
                        )

                        // Text Lines (simulated document body)
                        var yOffset = 32f
                        val lineCount = if (isLandscape) 7 else 12
                        for (i in 0 until lineCount) {
                            val lineWidth = if (i % 3 == 0) size.width * 0.6f else size.width * 0.9f
                            drawRoundRect(
                                color = textColor.copy(alpha = 0.6f),
                                topLeft = Offset(4f, yOffset),
                                size = Size(lineWidth, 4f),
                                cornerRadius = CornerRadius(1f, 1f)
                            )
                            yOffset += 11f
                        }

                        // Bottom Page Number stamp
                        drawRoundRect(
                            color = Color(0xFF94A3B8),
                            topLeft = Offset(size.width / 2f - 12f, size.height - 12f),
                            size = Size(24f, 4f),
                            cornerRadius = CornerRadius(1f, 1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Arrows (< Prev | Next >)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentPage > 1) currentPage-- },
                    enabled = currentPage > 1,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Page")
                }

                Text(
                    text = "$fileName • ${if (duplexMode == "DOUBLE") "Duplex Sheet" else "Single Sided"}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    fontWeight = FontWeight.Medium
                )

                IconButton(
                    onClick = { if (currentPage < pageCount) currentPage++ },
                    enabled = currentPage < pageCount,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Page")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thumbnail Slider Row
            Text(
                text = "Page Thumbnails",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items((1..pageCount).toList()) { p ->
                    val isSelected = p == currentPage
                    Box(
                        modifier = Modifier
                            .size(38.dp, 48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .background(if (isSelected) Color(0xFFEFF6FF) else Color.White)
                            .clickable { currentPage = p },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp, 2.dp)
                                    .background(if (isColor) Color(0xFF2563EB) else Color(0xFF64748B))
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(20.dp, 1.5.dp)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$p",
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF2563EB) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            }
        }
    }
}
