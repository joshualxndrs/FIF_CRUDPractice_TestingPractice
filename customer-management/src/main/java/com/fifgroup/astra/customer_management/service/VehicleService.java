package com.fifgroup.astra.customer_management.service;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.model.Vehicle;
import com.fifgroup.astra.customer_management.repository.UserRepository;
import com.fifgroup.astra.customer_management.repository.VehicleRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private static final Logger logger = LogManager.getLogger(VehicleService.class);
    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Vehicle> getVehiclesByUser(Long userId) {
        logger.info("Fetching vehicles for user ID {}", userId);
        return vehicleRepository.findByOwner_Id(userId);
    }

    public Optional<Vehicle> getVehicleByIdAndUser(Long vehicleId, Long userId) {
        logger.info("Fetching vehicle ID {} for user ID {}", vehicleId, userId);
        return vehicleRepository.findByIdAndOwner_Id(vehicleId, userId);
    }

    public List<Vehicle> createVehiclesForUser(User user, List<Vehicle> vehicles) {
        logger.info("Creating {} vehicles for user ID {}", vehicles.size(), user.getId());
        vehicles.forEach(vehicle -> vehicle.setOwner(user));
        return vehicleRepository.saveAll(vehicles);
    }

    public Vehicle updateVehicle(Long vehicleId, Long userId, Vehicle vehicleDetails) {
        return vehicleRepository.findByIdAndOwner_Id(vehicleId, userId).map(vehicle -> {
            vehicle.setLicensePlate(vehicleDetails.getLicensePlate());
            vehicle.setRegistrationId(vehicleDetails.getRegistrationId());
            vehicle.setBrand(vehicleDetails.getBrand());
            vehicle.setModel(vehicleDetails.getModel());
            logger.info("Updated vehicle ID {} for user ID {}", vehicleId, userId);
            return vehicleRepository.save(vehicle);
        }).orElseThrow(() -> {
            logger.error("Vehicle ID {} not found for user ID {}", vehicleId, userId);
            return new RuntimeException("Vehicle not found");
        });
    }

    public void deleteVehicle(Long vehicleId, Long userId) {
        vehicleRepository.findByIdAndOwner_Id(vehicleId, userId).ifPresent(vehicle -> {
            vehicleRepository.delete(vehicle);
            logger.warn("Deleted vehicle ID {} for user ID {}", vehicleId, userId);
        });
    }

    // **Upload Vehicle Image**
    public Vehicle uploadVehicleImage(Long vehicleId, Long userId, MultipartFile file) throws IOException {
        logger.info("Uploading image for vehicle ID {} by user ID {}", vehicleId, userId);

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp"))) {
            logger.warn("Invalid file type uploaded for vehicle ID {}", vehicleId);
            throw new IllegalArgumentException("Only JPEG, PNG, and WEBP images are allowed.");
        }

        // Fetch vehicle
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByIdAndOwner_Id(vehicleId, userId);
        if (vehicleOptional.isEmpty()) {
            logger.error("Vehicle not found for ID {}", vehicleId);
            throw new RuntimeException("Vehicle not found");
        }

        Vehicle vehicle = vehicleOptional.get();
        String fileName = StringUtils.cleanPath(vehicleId + "_" + file.getOriginalFilename());

        // Save file locally
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update vehicle entity with new file name
        vehicle.setVehicleImage(fileName);
        logger.info("Image uploaded successfully for vehicle ID {}", vehicleId);
        return vehicleRepository.save(vehicle);
    }

    // **Retrieve Vehicle Image**
    public Resource getVehicleImage(Long vehicleId, Long userId) throws MalformedURLException {
        logger.info("Fetching image for vehicle ID {} by user ID {}", vehicleId, userId);

        Optional<Vehicle> vehicleOptional = vehicleRepository.findByIdAndOwner_Id(vehicleId, userId);
        if (vehicleOptional.isEmpty() || vehicleOptional.get().getVehicleImage() == null) {
            logger.warn("Image not found for vehicle ID {}", vehicleId);
            throw new RuntimeException("Image not found");
        }

        Path filePath = Paths.get(UPLOAD_DIR).resolve(vehicleOptional.get().getVehicleImage());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            logger.error("File not found for vehicle ID {}", vehicleId);
            throw new RuntimeException("File not found");
        }

        logger.info("Serving image for vehicle ID {}", vehicleId);
        return resource;
    }

    public void saveVehicle(Vehicle vehicle) {
        logger.info("Saving vehicle ID {}", vehicle.getId());
        vehicleRepository.save(vehicle);
    }

    // Delete Vehicle Image
    public boolean deleteVehicleImage(Long vehicleId, Long userId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByIdAndOwner_Id(vehicleId, userId);
        if (vehicleOptional.isEmpty() || vehicleOptional.get().getVehicleImage() == null) {
            logger.warn("Image not found for vehicle ID {}", vehicleId);
            return false;
        }

        Vehicle vehicle = vehicleOptional.get();
        Path filePath = Paths.get(UPLOAD_DIR).resolve(vehicle.getVehicleImage());

        try {
            Files.deleteIfExists(filePath); // Delete file from storage
            vehicle.setVehicleImage(null); // Remove reference from database
            vehicleRepository.save(vehicle);
            logger.info("Image deleted successfully for vehicle ID {}", vehicleId);
            return true;
        } catch (IOException e) {
            logger.error("Error deleting image for vehicle ID {}: {}", vehicleId, e.getMessage());
            return false;
        }
    }

    // Update Vehicle Image (Reupload)
    public Vehicle updateVehicleImage(Long vehicleId, Long userId, MultipartFile file) throws IOException {
        logger.info("Updating image for vehicle ID {} by user ID {}", vehicleId, userId);

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp"))) {
            logger.warn("Invalid file type uploaded for vehicle ID {}", vehicleId);
            throw new IllegalArgumentException("Only JPEG, PNG, and WEBP images are allowed.");
        }

        // Fetch vehicle
        Optional<Vehicle> vehicleOptional = vehicleRepository.findByIdAndOwner_Id(vehicleId, userId);
        if (vehicleOptional.isEmpty()) {
            logger.error("Vehicle not found for ID {}", vehicleId);
            throw new RuntimeException("Vehicle not found");
        }

        Vehicle vehicle = vehicleOptional.get();

        // Delete old image before uploading a new one
        if (vehicle.getVehicleImage() != null) {
            Path oldFilePath = Paths.get(UPLOAD_DIR).resolve(vehicle.getVehicleImage());
            Files.deleteIfExists(oldFilePath);
        }

        // Save new file
        String fileName = StringUtils.cleanPath(vehicleId + "_" + file.getOriginalFilename());
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Update vehicle entity with new file name
        vehicle.setVehicleImage(fileName);
        logger.info("Image updated successfully for vehicle ID {}", vehicleId);
        return vehicleRepository.save(vehicle);
    }

}
