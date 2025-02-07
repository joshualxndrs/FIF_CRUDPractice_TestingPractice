package com.fifgroup.astra.customer_management.service;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.model.Vehicle;
import com.fifgroup.astra.customer_management.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private User testUser;
    private Vehicle testVehicle;
    private MultipartFile testFile;

    private static final String UPLOAD_DIR = "uploads/";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(9L);
        testUser.setKtpNumber("5678910");

        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setLicensePlate("B 1234 XYZ");
        testVehicle.setBrand("Toyota");
        testVehicle.setModel("Zenix");
        testVehicle.setRegistrationId("REG-001");
        testVehicle.setOwner(testUser);
        testVehicle.setVehicleImage("1_test-image.png");

        testFile = new MockMultipartFile("file", "test-image.png",
                "image/png", "dummy image content".getBytes());
    }

    @Test
    void shouldUploadVehicleImage() throws IOException {
        when(vehicleRepository.findByIdAndOwner_Id(1L, 9L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.uploadVehicleImage(1L, 9L, testFile);

        assertNotNull(updatedVehicle);
        assertNotNull(updatedVehicle.getVehicleImage());
        assertTrue(updatedVehicle.getVehicleImage().contains("1_test-image.png"));

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void shouldDeleteVehicleImage() throws IOException {
        when(vehicleRepository.findByIdAndOwner_Id(1L, 9L)).thenReturn(Optional.of(testVehicle));

        boolean deleted = vehicleService.deleteVehicleImage(1L, 9L);

        assertTrue(deleted);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void shouldUpdateVehicleImage() throws IOException {
        when(vehicleRepository.findByIdAndOwner_Id(1L, 9L)).thenReturn(Optional.of(testVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle updatedVehicle = vehicleService.updateVehicleImage(1L, 9L, testFile);

        assertNotNull(updatedVehicle);
        assertNotNull(updatedVehicle.getVehicleImage());
        assertTrue(updatedVehicle.getVehicleImage().contains("1_test-image.png"));

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }
}
