package com.fifgroup.astra.customer_management.controller;

import com.fifgroup.astra.customer_management.model.User;
import com.fifgroup.astra.customer_management.model.Vehicle;
import com.fifgroup.astra.customer_management.service.UserService;
import com.fifgroup.astra.customer_management.service.VehicleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}/vehicles")
public class VehicleController {
    private static final Logger logger = LogManager.getLogger(VehicleController.class);
    private static final String UPLOAD_DIR = "uploads/";

    private final VehicleService vehicleService;
    private final UserService userService;

    public VehicleController(VehicleService vehicleService, UserService userService) {
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createVehicles(@PathVariable Long userId, @RequestBody List<Vehicle> vehicles) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();
        List<Vehicle> savedVehicles = vehicleService.createVehiclesForUser(user, vehicles);
        return ResponseEntity.ok(savedVehicles);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehiclesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long userId, @PathVariable Long id) {
        return vehicleService.getVehicleByIdAndUser(id, userId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Upload Vehicle Image
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<?> uploadVehicleImage(@PathVariable Long userId, @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp"))) {
                logger.warn("Invalid file type uploaded for vehicle ID {}", id);
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only JPEG, PNG, and WEBP images are allowed.");
            }

            // Fetch the vehicle
            Optional<Vehicle> vehicleOptional = vehicleService.getVehicleByIdAndUser(id, userId);
            if (vehicleOptional.isEmpty()) {
                logger.error("Vehicle not found for ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found");
            }

            Vehicle vehicle = vehicleOptional.get();
            String fileName = StringUtils.cleanPath(id + "_" + file.getOriginalFilename());

            // Save file locally
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Update vehicle entity with new file name
            vehicle.setVehicleImage(fileName);
            vehicleService.saveVehicle(vehicle);

            logger.info("Image uploaded successfully for vehicle ID {}", id);
            return ResponseEntity.ok("Image uploaded successfully: " + fileName);
        } catch (IOException e) {
            logger.error("Error uploading image for vehicle ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not upload the file: " + e.getMessage());
        }
    }

    // Retrieve Vehicle Image
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getVehicleImage(@PathVariable Long userId, @PathVariable Long id) {
        Optional<Vehicle> vehicleOptional = vehicleService.getVehicleByIdAndUser(id, userId);
        if (vehicleOptional.isEmpty() || vehicleOptional.get().getVehicleImage() == null) {
            logger.warn("Image not found for vehicle ID {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(vehicleOptional.get().getVehicleImage());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                logger.error("File not found for vehicle ID {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Determine Content-Type dynamically
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback in case MIME type detection fails
            }

            logger.info("Serving image for vehicle ID {} with Content-Type {}", id, contentType);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error retrieving image for vehicle ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Delete Vehicle Image
    @DeleteMapping("/{id}/delete-image")
    public ResponseEntity<?> deleteVehicleImage(@PathVariable Long userId, @PathVariable Long id) {
        try {
            boolean deleted = vehicleService.deleteVehicleImage(id, userId);
            if (deleted) {
                logger.info("Image deleted successfully for vehicle ID {}", id);
                return ResponseEntity.ok("Image deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found or vehicle does not exist.");
            }
        } catch (Exception e) {
            logger.error("Error deleting image for vehicle ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete the file: " + e.getMessage());
        }
    }

    // Update Vehicle Image (Reupload)
    @PutMapping("/{id}/update-image")
    public ResponseEntity<?> updateVehicleImage(@PathVariable Long userId, @PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Vehicle updatedVehicle = vehicleService.updateVehicleImage(id, userId, file);
            logger.info("Image updated successfully for vehicle ID {}", id);
            return ResponseEntity.ok("Image updated successfully: " + updatedVehicle.getVehicleImage());
        } catch (IOException e) {
            logger.error("Error updating image for vehicle ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not update the file: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
