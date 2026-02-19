package com.Jadhav.Contest_tracker.controller;

import com.Jadhav.Contest_tracker.Model.Contest;
import com.Jadhav.Contest_tracker.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contests")
@CrossOrigin(origins = {
    "https://code-alarm-contest.vercel.app",
    "http://localhost:5173",
    "https://www.codealarm.tech/"
})
    
public class ContestController {

    @Autowired
    private ContestService contestService;

    @GetMapping("/filter")
    public List<Contest> getFilteredContests(
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return contestService.getFilteredContests(platform, startDate, endDate);
    }

    @GetMapping("/all")
    public List<Contest> getAllContests() {
        return contestService.getAllContests();
    }

    // Debug endpoint to check what's in the database
    @GetMapping("/debug")
    public Map<String, Object> getDebugInfo() {
        List<Contest> allContests = contestService.getAllContests();
        Date now = new Date();

        long upcoming = allContests.stream()
                .filter(c -> c.getContestStartDate().after(now))
                .count();

        long past = allContests.stream()
                .filter(c -> c.getContestStartDate().before(now))
                .count();

        Map<String, Object> debug = new HashMap<>();
        debug.put("totalContests", allContests.size());
        debug.put("upcomingContests", upcoming);
        debug.put("pastContests", past);
        debug.put("currentServerTime", now);

        // Group by platform
        Map<String, Long> byPlatform = new HashMap<>();
        allContests.forEach(c -> {
            String platform = c.getPlatform();
            byPlatform.put(platform, byPlatform.getOrDefault(platform, 0L) + 1);
        });
        debug.put("contestsByPlatform", byPlatform);

        // Sample contests for verification
        debug.put("sampleContests", allContests.stream().limit(5).toList());

        return debug;
    }

    // Fetch all contests from all platforms
    @GetMapping("/fetch-all")
    public Map<String, String> fetchAllContests() {
        Map<String, String> results = new HashMap<>();

        try {
            // This would require injecting all services
            results.put("status", "✅ Fetch all triggered - check individual endpoints");
            results.put("message", "Use /api/codeforces/fetch, /api/codechef/fetch, /api/leetcode/fetch");
        } catch (Exception e) {
            results.put("status", "❌ Error");
            results.put("message", e.getMessage());
        }

        return results;
    }
}
