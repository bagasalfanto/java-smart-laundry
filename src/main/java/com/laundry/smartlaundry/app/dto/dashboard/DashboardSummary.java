package com.laundry.smartlaundry.app.dto.dashboard;

public record DashboardSummary(
		long totalStaff,
		long activeStaff,
		long totalCustomers,
		long activeMembers,
		long activeServices,
		long inventoryItems,
		long totalTransactions,
		long queuedOrders,
		long processingOrders,
		long completedOrders,
		long paidTransactions) {
}
