package com.shopgrid.inventory.mapper;

import com.shopgrid.inventory.domain.dto.response.InventoryResponse;
import com.shopgrid.inventory.domain.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryResponse toResponse(Inventory inventory);
}
