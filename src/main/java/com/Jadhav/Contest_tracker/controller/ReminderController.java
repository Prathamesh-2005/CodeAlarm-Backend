package com.Jadhav.Contest_tracker.controller;

import com.Jadhav.Contest_tracker.Model.Reminder;
import com.Jadhav.Contest_tracker.Model.User;
import com.Jadhav.Contest_tracker.service.ReminderService;
import com.Jadhav.Contest_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = {
    "https://code-alarm-contest.vercel.app",
    "http://localhost:5173"
})
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private UserService userService;

    @PostMapping("/set/{contestId}")
    public ResponseEntity<?> setReminder(@RequestBody Reminder reminder, @PathVariable String contestId) {
        try {
            System.out.println("=== POST /set/{contestId} endpoint hit ===");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("User not authenticated in POST endpoint!");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            String username = authentication.getName();
            System.out.println("Username from auth: " + username);

            User user = userService.findByUsername(username).orElseThrow();
            System.out.println("Found user: " + user.getUsername());

            reminder.setUser(user);
            System.out.println("Received Reminder: " + reminder);

            Reminder savedReminder = reminderService.saveReminder(reminder, contestId);
            System.out.println("Successfully saved reminder with ID: " + savedReminder.getId());

            return ResponseEntity.ok(savedReminder);
        } catch (Exception e) {
            System.err.println("Error in setReminder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating reminder: " + e.getMessage());
        }
    }

    @GetMapping("/my-reminders")
    public ResponseEntity<?> getMyReminders(HttpServletRequest request) {
        try {
            System.out.println("=== GET /my-reminders endpoint hit ===");

            String authHeader = request.getHeader("Authorization");
            System.out.println("Authorization header: " + authHeader);
            System.out.println("Authorization header exists: " + (authHeader != null));
            System.out.println("Authorization header starts with Bearer: " + (authHeader != null && authHeader.startsWith("Bearer ")));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication object: " + authentication);
            System.out.println("Authentication class: " + (authentication != null ? authentication.getClass().getSimpleName() : "null"));
            System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));
            System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
            System.out.println("Principal class: " + (authentication != null && authentication.getPrincipal() != null ? authentication.getPrincipal().getClass().getSimpleName() : "null"));

            if (authentication == null) {
                System.out.println("Authentication is null - JWT filter might not be working");
                return ResponseEntity.status(401).body("Authentication is null");
            }

            if (!authentication.isAuthenticated()) {
                System.out.println("User not authenticated!");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            String username = authentication.getName();
            System.out.println("Username from auth: " + username);

            if (username == null || username.equals("anonymousUser")) {
                System.out.println("Username is null or anonymous - JWT processing failed");
                return ResponseEntity.status(401).body("Invalid authentication");
            }

            User user = userService.findByUsername(username).orElse(null);
            if (user == null) {
                System.out.println("User not found in database for username: " + username);
                return ResponseEntity.status(404).body("User not found");
            }

            System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")");

            List<Reminder> reminders = reminderService.getUserReminders(user);
            System.out.println("Found " + reminders.size() + " reminders for user");
            for (Reminder reminder : reminders) {
                System.out.println("Reminder ID: " + reminder.getId());
                System.out.println("Contest: " + (reminder.getContest() != null ? reminder.getContest().getContestName() : "NULL"));
                System.out.println("Contest ID: " + (reminder.getContest() != null ? reminder.getContest().getContestId() : "NULL"));
            }

            return ResponseEntity.ok(reminders);

        } catch (Exception e) {
            System.err.println("Error in getMyReminders: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<?> deleteReminder(@PathVariable String reminderId) {
        try {
            System.out.println("=== DELETE /{reminderId} endpoint hit ===");
            System.out.println("Reminder ID to delete: " + reminderId);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication: " + authentication);
            System.out.println("Is Authenticated: " + (authentication != null && authentication.isAuthenticated()));

            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("User not authenticated in DELETE endpoint!");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            String username = authentication.getName();
            System.out.println("Username from auth: " + username);

            User user = userService.findByUsername(username).orElseThrow();
            System.out.println("Found user: " + user.getUsername());

            reminderService.deleteUserReminder(reminderId, user);
            System.out.println("Successfully deleted reminder with ID: " + reminderId);

            return ResponseEntity.ok().body("Reminder deleted successfully");

        } catch (Exception e) {
            System.err.println("Error in deleteReminder: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error deleting reminder: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request) {
        System.out.println("=== GET /test endpoint hit ===");

        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header in test: " + authHeader);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in test: " + authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Reminder Controller is working!");
        response.put("hasAuthHeader", authHeader != null);
        response.put("authHeaderValue", authHeader);
        response.put("hasAuthentication", authentication != null);
        response.put("isAuthenticated", authentication != null && authentication.isAuthenticated());
        response.put("principal", authentication != null ? authentication.getPrincipal() : null);
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }
}
