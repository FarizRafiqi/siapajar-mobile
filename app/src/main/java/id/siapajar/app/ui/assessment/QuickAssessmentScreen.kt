package id.siapajar.app.ui.assessment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import id.siapajar.app.domain.model.InstrumentType
import id.siapajar.app.theme.*
import id.siapajar.app.ui.components.StudentAvatar
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAssessmentScreen(
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: AssessmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showStudentPicker by remember { mutableStateOf(false) }

    // State for Camera Capture File & Uri
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess && currentPhotoFile != null && currentPhotoFile!!.exists() && currentPhotoFile!!.length() > 0) {
            viewModel.setPhotoPath(currentPhotoFile!!.absolutePath)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            tryLaunchCamera(context, { currentPhotoFile = it }, { takePictureLauncher.launch(it) })
        } else {
            Toast.makeText(context, "Izin kamera diperlukan untuk mengambil foto asesmen", Toast.LENGTH_SHORT).show()
        }
    }

    fun openCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            tryLaunchCamera(context, { currentPhotoFile = it }, { takePictureLauncher.launch(it) })
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val photosDir = File(context.cacheDir, "photos").apply { mkdirs() }
                val targetFile = File(photosDir, "gallery_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.setPhotoPath(targetFile.absolutePath)
            } catch (_: Exception) {
                viewModel.setPhotoPath(uri.toString())
            }
        }
    }

    val storagePermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Izin akses foto diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGallery() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, storagePermission) == PackageManager.PERMISSION_GRANTED) {
            galleryLauncher.launch("image/*")
        } else {
            requestStoragePermissionLauncher.launch(storagePermission)
        }
    }

    if (showStudentPicker) {
        AlertDialog(
            onDismissRequest = { showStudentPicker = false },
            title = { Text("Pilih Siswa Terkait", fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                if (uiState.availableStudents.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PeopleOutline,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum Ada Siswa Terdaftar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Data siswa belum tersedia atau belum disinkronkan.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        uiState.availableStudents.forEach { student ->
                            val isSelected = uiState.selectedStudentIds.contains(student.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.toggleStudentSelection(student.id) }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { viewModel.toggleStudentSelection(student.id) },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                StudentAvatar(
                                    name = student.name,
                                    photoUrl = student.photoUrl,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = student.name,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStudentPicker = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Selesai")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Catat Asesmen",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    Surface(
                        color = CanvasBackground,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudDone,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tersimpan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp),
                color = Color.White
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.saveAssessment(onSaveSuccess) },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Simpan Asesmen",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Photo Hero Container (Tappable to Take Photo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFE2E8F0))
                    .clickable { openCamera() },
                contentAlignment = Alignment.Center
            ) {
                val photoModel = uiState.photoPath ?: "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=800&auto=format&fit=crop&q=80"
                AsyncImage(
                    model = photoModel,
                    contentDescription = "Foto Asesmen",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Quick camera overlay badge
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Ambil Foto",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Photo Action Buttons (Ulangi Foto / Ambil dari Galeri)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, EmeraldLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { openCamera() },
                    color = MintSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ambil Foto",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, EmeraldLight.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { openGallery() },
                    color = MintSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dari Galeri",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Instrument Type Segmented Selector
            Text(
                text = "Instrumen Asesmen",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = CanvasBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InstrumentType.values().take(3).forEach { type ->
                        val isSelected = uiState.selectedInstrument == type
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(9.dp))
                                .clickable { viewModel.setInstrument(type) },
                            color = if (isSelected) EmeraldPrimary else Color.Transparent,
                            shadowElevation = if (isSelected) 2.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = type.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Tagged Students Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Siswa Terkait (${uiState.selectedStudentIds.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Kelas TK B1",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (uiState.availableStudents.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    color = CanvasBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSlate)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MintSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PeopleOutline,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Belum Ada Data Siswa",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Data siswa di kelas ini belum tersedia atau belum ditambahkan.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiState.availableStudents) { student ->
                        val isTagged = uiState.selectedStudentIds.contains(student.id)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.toggleStudentSelection(student.id) }
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                StudentAvatar(
                                    name = student.name,
                                    photoUrl = student.photoUrl,
                                    size = 54.dp,
                                    borderWidth = if (isTagged) 2.5.dp else 1.dp,
                                    borderColor = if (isTagged) EmeraldPrimary else BorderSlate
                                )

                                if (isTagged) {
                                    Surface(
                                        color = EmeraldPrimary,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .offset(x = 2.dp, y = 2.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.padding(2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = student.name.split(" ").firstOrNull() ?: student.name,
                                fontSize = 12.sp,
                                fontWeight = if (isTagged) FontWeight.Bold else FontWeight.Medium,
                                color = if (isTagged) TextPrimary else TextMuted
                            )
                        }
                    }

                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showStudentPicker = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, EmeraldLight.copy(alpha = 0.6f), CircleShape)
                                    .background(MintSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Siswa",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tambah",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Notes & Observations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Catatan Pengamatan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Mic,
                        contentDescription = "Voice Dictation",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.setNotes(it) },
                placeholder = {
                    Text(
                        text = "Ceritakan proses dan respons anak saat kegiatan...\n(Contoh: Anak mampu merangkai daun mint dan mengenali aromanya secara mandiri)",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = BorderSlate,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Targeted Learning Objectives (TP) Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MintSurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MintContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EmeraldPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = uiState.tpCode,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Target Capaian Hari Ini",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark
                        )
                        Text(
                            text = uiState.tpTitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun tryLaunchCamera(
    context: Context,
    onFileCreated: (File) -> Unit,
    onLaunch: (Uri) -> Unit
) {
    try {
        val photosDir = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File(photosDir, "assessment_${System.currentTimeMillis()}.jpg")
        onFileCreated(file)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        onLaunch(uri)
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Gagal menyiapkan kamera: ${e.localizedMessage ?: "Terjadi kesalahan"}",
            Toast.LENGTH_SHORT
        ).show()
    }
}

