package com.example.sirius.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sirius.R
import com.example.sirius.model.ClinicalRecord
import com.example.sirius.viewmodel.ClinicalRecordViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.example.sirius.ui.theme.Green3a50
import kotlinx.coroutines.launch

@Composable
fun ClinicalRecord(id: Int?) {
    val clinicalRecordVm: ClinicalRecordViewModel = viewModel(factory = ClinicalRecordViewModel.factory)
    val clinicalRecords by clinicalRecordVm.getClinicalRecordsForAnimal(id ?: -1).collectAsState(emptyList())
    var selectedAppointment by remember { mutableStateOf<ClinicalRecord?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            item {
                MedicalAppointments(
                    animalId = id ?: -1,
                    clinicalRecordVm,
                    clinicalRecords.filter { it.appointmentDate.isNotEmpty() }) { appointment ->
                    selectedAppointment = appointment
                }
            }

            item {
                Prescriptions(selectedAppointment?.let { listOf(it) } ?: emptyList())
            }

            item {
                Comments(selectedAppointment?.let { listOf(it) } ?: emptyList())
            }

            item {
                Illnesses(clinicalRecords.filter { it.illnesses.isNotEmpty() })
            }

            item {
                Vaccines(clinicalRecords.filter { it.vaccines.isNotEmpty() })
            }
        }
    }
}

@Composable
fun MedicalAppointments(
    animalId: Int,
    clinicalRecordVm: ClinicalRecordViewModel,
    medicalAppointments: List<ClinicalRecord>,
    onAppointmentClick: (ClinicalRecord) -> Unit,
) {
    val appointmentsInColumns = medicalAppointments.chunked(2)
    var editMode by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<ClinicalRecord?>(null) }
    var showDialogCreateRecord by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.medical_app),
                style = MaterialTheme.typography.h5
            )
            SmallFloatingActionButton(
                onClick = { showDialogCreateRecord = true },
                modifier = Modifier
                    .padding(3.dp)
                    .padding(start = 8.dp)
                    .size(25.dp),
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }

        appointmentsInColumns.forEach { appointmentsRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                appointmentsRow.forEach { appointment ->
                    Button(
                        onClick = {
                            onAppointmentClick(appointment)
                            selectedAppointment = appointment
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(Green3a50),
                        contentPadding = PaddingValues(5.dp)
                    ) {
                        if (selectedAppointment == appointment) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier
                                    .clickable { editMode = true }
                                    .size(17.dp)
                                    .padding(end = 2.dp)
                            )
                        }
                        TextWithSplit(
                            text = appointment.appointmentDate,
                            color = Color.White,
                        )
                    }
                }
            }
        }
    }
    if (editMode) {
        selectedAppointment?.let {
            ClinicalRecordDialog(
                onDismissRequest = { editMode = false },
                clinicalRecord = it,
                clinicalRecordVm = clinicalRecordVm,
                animalId = animalId,
                isNewRecord = false,
            )
        }
    }
    if (showDialogCreateRecord) {
        ClinicalRecordDialog(
            onDismissRequest = { showDialogCreateRecord = false },
            clinicalRecord = null,
            clinicalRecordVm = clinicalRecordVm,
            animalId = animalId,
            isNewRecord = true,
        )
    }
}

@Composable
fun Illnesses(
    clinicalRecords: List<ClinicalRecord>
) {
    if (clinicalRecords.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.illnesses),
                style = MaterialTheme.typography.h5
            )
            clinicalRecords.forEach { illness ->
                Text(
                    text = illness.illnesses,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun Prescriptions(
    clinicalRecords: List<ClinicalRecord>
) {
    if (clinicalRecords.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.prescriptions),
                style = MaterialTheme.typography.h5
            )
            clinicalRecords.forEach { record ->
                Text(
                    text = record.prescriptions,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun Vaccines(
    clinicalRecords: List<ClinicalRecord>
) {
    if (clinicalRecords.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.vaccines),
                style = MaterialTheme.typography.h5
            )
            clinicalRecords.forEach { record ->
                Text(
                    text = record.vaccines,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun Comments(
    clinicalRecords: List<ClinicalRecord>
) {
    if (clinicalRecords.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.medical_comments),
                style = MaterialTheme.typography.h5
            )
            clinicalRecords.forEach { record ->
                Text(
                    text = record.comments,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}

@Composable
fun ClinicalRecordDialog(
    onDismissRequest: () -> Unit,
    clinicalRecord: ClinicalRecord?,
    clinicalRecordVm: ClinicalRecordViewModel,
    animalId: Int,
    isNewRecord: Boolean
){
    var editedRecord by remember { mutableStateOf(clinicalRecord ?:
        ClinicalRecord(0, animalId, "", "", "", "", "", "")) }

    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                Text(
                    text = stringResource(id = if(isNewRecord) R.string.new_record else R.string.edit_record),
                    style = MaterialTheme.typography.h5
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                TextField(
                    value = editedRecord.appointmentDate,
                    onValueChange = { editedRecord = editedRecord.copy(appointmentDate = it) },
                    label = { Text(text = stringResource(id = R.string.appointment_date)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = editedRecord.illnesses,
                    onValueChange = { editedRecord = editedRecord.copy(illnesses = it) },
                    label = { Text(text = stringResource(id = R.string.illnesses)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = editedRecord.prescriptions,
                    onValueChange = { editedRecord = editedRecord.copy(prescriptions = it) },
                    label = { Text(text = stringResource(id = R.string.prescriptions)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = editedRecord.prescriptionEndDate,
                    onValueChange = { editedRecord = editedRecord.copy(prescriptionEndDate = it) },
                    label = { Text(text = stringResource(id = R.string.prescription_end_date)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = editedRecord.vaccines,
                    onValueChange = { editedRecord = editedRecord.copy(vaccines = it) },
                    label = { Text(text = stringResource(id = R.string.vaccines)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = editedRecord.comments,
                    onValueChange = { editedRecord = editedRecord.copy(comments = it) },
                    label = { Text(text = stringResource(id = R.string.medical_comments)) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    clinicalRecordVm.viewModelScope.launch {
                        if (isNewRecord) clinicalRecordVm.insertClinicalRecord(clinicalRecord = editedRecord)
                        else clinicalRecordVm.updateClinicalRecord(clinicalRecord = editedRecord)
                    }
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}