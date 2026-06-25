package com.laundry.smartlaundry.app.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.laundry.smartlaundry.app.services.admin.ActivityLogService;

@Controller
@RequestMapping("/admin/activity-logs")
public class ActivityLogController {

	private final ActivityLogService activityLogService;

	public ActivityLogController(ActivityLogService activityLogService) {
		this.activityLogService = activityLogService;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("logs", activityLogService.getAllLogs());
		return "admin/activity-logs/index";
	}
}
