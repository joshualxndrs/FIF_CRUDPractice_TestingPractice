package com.fifgroup.astra.customer_management.controller;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.model.Vehicle;
import com.fifgroup.astra.customer_management.service.UserService;
import com.fifgroup.astra.customer_management.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private VehicleController vehicleController;

    private User testUser;
    private Vehicle testVehicle;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(25L);
        testUser.setName("Bernardyo Rikbo");
        testUser.setAddress("Surabaya, Indonesia");
        testUser.setKtpNumber("5678910");

        testVehicle = new Vehicle();
        testVehicle.setId(5L);
        testVehicle.setLicensePlate("B 888 ARS");
        testVehicle.setRegistrationId("REG-004");
        testVehicle.setBrand("VeWe");
        testVehicle.setModel("Srilocco");
        testVehicle.setOwner(testUser);
        testVehicle.setVehicleImage("5_CAKE ADDICT.png");

        testFile = new MockMultipartFile(
                "file",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "test-image-content".getBytes()
        );
    }

    @Test
    void shouldReturnAllVehiclesForUser() {
        when(vehicleService.getVehiclesByUser(testUser.getId())).thenReturn(List.of(testVehicle));

        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehiclesByUser(testUser.getId());

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(vehicleService, times(1)).getVehiclesByUser(testUser.getId());
    }

    @Test
    void shouldReturnVehicleById() {
        when(vehicleService.getVehicleByIdAndUser(testVehicle.getId(), testUser.getId()))
                .thenReturn(Optional.of(testVehicle));

        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(testUser.getId(), testVehicle.getId());

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VeWe", response.getBody().getBrand());
        verify(vehicleService, times(1)).getVehicleByIdAndUser(testVehicle.getId(), testUser.getId());
    }

    @Test
    void shouldReturnNotFoundWhenVehicleDoesNotExist() {
        when(vehicleService.getVehicleByIdAndUser(testVehicle.getId(), testUser.getId()))
                .thenReturn(Optional.empty());

        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(testUser.getId(), testVehicle.getId());

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(vehicleService, times(1)).getVehicleByIdAndUser(testVehicle.getId(), testUser.getId());
    }

    @Test
    void shouldCreateVehicles() {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(vehicleService.createVehiclesForUser(eq(testUser), anyList())).thenReturn(List.of(testVehicle));

        ResponseEntity<?> response = vehicleController.createVehicles(testUser.getId(), List.of(testVehicle));

        assertEquals(OK, response.getStatusCode());
        verify(vehicleService, times(1)).createVehiclesForUser(eq(testUser), anyList());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.empty());

        ResponseEntity<?> response = vehicleController.createVehicles(testUser.getId(), List.of(testVehicle));

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(vehicleService, never()).createVehiclesForUser(any(), anyList());
    }

    @Test
    void shouldUploadVehicleImage() throws Exception {
        when(vehicleService.getVehicleByIdAndUser(testVehicle.getId(), testUser.getId()))
                .thenReturn(Optional.of(testVehicle));

        ResponseEntity<?> response = vehicleController.uploadVehicleImage(testUser.getId(), testVehicle.getId(), testFile);

        assertEquals(OK, response.getStatusCode());
        verify(vehicleService, times(1)).saveVehicle(any(Vehicle.class));
    }

    @Test
    void shouldReturnUnsupportedMediaTypeForInvalidFileUpload() throws Exception {
        MultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
        );

        ResponseEntity<?> response = vehicleController.uploadVehicleImage(testUser.getId(), testVehicle.getId(), invalidFile);

        assertEquals(UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        verify(vehicleService, never()).saveVehicle(any(Vehicle.class));
    }

    @Test
    void shouldReturnNotFoundWhenUploadingImageForNonexistentVehicle() throws Exception {
        when(vehicleService.getVehicleByIdAndUser(testVehicle.getId(), testUser.getId()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = vehicleController.uploadVehicleImage(testUser.getId(), testVehicle.getId(), testFile);

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(vehicleService, never()).saveVehicle(any(Vehicle.class));
    }

    @Test
    void shouldDeleteVehicleImage() {
        when(vehicleService.deleteVehicleImage(testVehicle.getId(), testUser.getId())).thenReturn(true);

        ResponseEntity<?> response = vehicleController.deleteVehicleImage(testUser.getId(), testVehicle.getId());

        assertEquals(OK, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicleImage(testVehicle.getId(), testUser.getId());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentImage() {
        when(vehicleService.deleteVehicleImage(testVehicle.getId(), testUser.getId())).thenReturn(false);

        ResponseEntity<?> response = vehicleController.deleteVehicleImage(testUser.getId(), testVehicle.getId());

        assertEquals(NOT_FOUND, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicleImage(testVehicle.getId(), testUser.getId());
    }

    @Test
    void shouldUpdateVehicleImage() throws Exception {
        when(vehicleService.updateVehicleImage(testVehicle.getId(), testUser.getId(), testFile))
                .thenReturn(testVehicle);

        ResponseEntity<?> response = vehicleController.updateVehicleImage(testUser.getId(), testVehicle.getId(), testFile);

        assertEquals(OK, response.getStatusCode());
        verify(vehicleService, times(1)).updateVehicleImage(testVehicle.getId(), testUser.getId(), testFile);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentVehicleImage() throws Exception {
        when(vehicleService.updateVehicleImage(testVehicle.getId(), testUser.getId(), testFile))
                .thenThrow(new RuntimeException("Vehicle not found"));

        ResponseEntity<?> response = vehicleController.updateVehicleImage(testUser.getId(), testVehicle.getId(), testFile);

        assertEquals(NOT_FOUND, response.getStatusCode());
    }
}
