package com.smartiadev.dispute_service.dto;

public record ResolveDisputeRequest(
        String decision,
        String action // NONE, DEACTIVATE_ITEM, SUSPEND_USER
) {}

