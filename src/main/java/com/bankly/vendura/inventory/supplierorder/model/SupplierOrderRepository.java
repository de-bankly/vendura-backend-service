package com.bankly.vendura.inventory.supplierorder.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface SupplierOrderRepository extends MongoRepository<SupplierOrder, String> {
    
    /**
     * Find supplier orders by status
     */
    Page<SupplierOrder> findByOrderStatus(SupplierOrder.OrderStatus orderStatus, Pageable pageable);
    
    /**
     * Find supplier orders by multiple statuses
     */
    List<SupplierOrder> findByOrderStatusIn(List<SupplierOrder.OrderStatus> orderStatuses);
    
    /**
     * Find supplier orders by supplier
     */
    Page<SupplierOrder> findBySupplier_Id(String supplierId, Pageable pageable);
    
    /**
     * Find supplier orders by expected delivery date range
     */
    Page<SupplierOrder> findByExpectedDeliveryDateBetween(Date startDate, Date endDate, Pageable pageable);
    
    /**
     * Find automatic supplier orders
     */
    Page<SupplierOrder> findByAutomaticOrder(boolean isAutomaticOrder, Pageable pageable);
}
