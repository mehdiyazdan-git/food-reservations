package com.mapnaom.foodreservation.services;


import com.mapnaom.foodreservation.dtos.OrderDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.OrderMapper;
import com.mapnaom.foodreservation.entities.Order;
import com.mapnaom.foodreservation.repositories.OrderRepository;
import com.mapnaom.foodreservation.searchForms.OrderSearchForm;
import com.mapnaom.foodreservation.specifications.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    /**
     * دریافت تمام سفارش‌ها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از OrderDto
     */
    @Transactional(readOnly = true)
    public Page<OrderDto> findAll(OrderSearchForm searchForm, Pageable pageable) {
        Specification<Order> specification = OrderSpecification.getOrderSpecification(searchForm);
        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        List<OrderDto> orderDtos = orderPage.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(orderDtos, pageable, orderPage.getTotalElements());
    }

    /**
     * دریافت یک سفارش بر اساس شناسه
     *
     * @param id شناسه سفارش
     * @return OrderDto
     * @throws ResourceNotFoundException اگر سفارش با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public OrderDto findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سفارش با شناسه " + id + " یافت نشد."));
        return orderMapper.toDto(order);
    }

    /**
     * ایجاد یک سفارش جدید
     *
     * @param orderDto داده‌های سفارش جدید
     * @return OrderDto ایجاد شده
     */
    @Transactional
    public OrderDto create(OrderDto orderDto) {
        Order order = orderMapper.toEntity(orderDto);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    /**
     * به‌روزرسانی یک سفارش موجود
     *
     * @param id       شناسه سفارش مورد نظر برای به‌روزرسانی
     * @param orderDto داده‌های جدید برای به‌روزرسانی
     * @return OrderDto به‌روز شده
     * @throws ResourceNotFoundException اگر سفارش با شناسه داده شده یافت نشد
     */
    @Transactional
    public OrderDto update(Long id, OrderDto orderDto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سفارش با شناسه " + id + " یافت نشد."));
        orderMapper.partialUpdate(orderDto, existingOrder);
        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }

    /**
     * حذف یک سفارش بر اساس شناسه
     *
     * @param id شناسه سفارش مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر سفارش با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("سفارش با شناسه " + id + " یافت نشد.");
        }
        orderRepository.deleteById(id);
    }
}
