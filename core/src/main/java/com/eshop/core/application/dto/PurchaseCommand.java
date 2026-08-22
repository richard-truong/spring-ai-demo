package com.eshop.core.application.dto;

import java.util.List;

public record PurchaseCommand(
    String userId,
    List<PurchaseItem> items
) {
}
