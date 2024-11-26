package com.mapnaom.foodreservation.services;

import com.mapnaom.foodreservation.dtos.MenuDto;
import com.mapnaom.foodreservation.exceptions.ResourceNotFoundException;
import com.mapnaom.foodreservation.mappers.MenuMapper;
import com.mapnaom.foodreservation.models.Menu;
import com.mapnaom.foodreservation.repositories.MenuRepository;
import com.mapnaom.foodreservation.searchForms.MenuSearchForm;
import com.mapnaom.foodreservation.specifications.MenuSpecification;
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
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuMapper menuMapper;

    /**
     * دریافت تمام منوها به صورت صفحه‌بندی شده با شرایط جستجو
     *
     * @param searchForm فرم جستجو
     * @param pageable   اطلاعات صفحه‌بندی و مرتب‌سازی
     * @return صفحه‌ای از MenuDto
     */
    @Transactional(readOnly = true)
    public Page<MenuDto> findAll(MenuSearchForm searchForm, Pageable pageable) {
        Specification<Menu> specification = MenuSpecification.getMenuSpecification(searchForm);
        Page<Menu> menuPage = menuRepository.findAll(specification, pageable);
        List<MenuDto> menuDtos = menuPage.stream()
                .map(menuMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(menuDtos, pageable, menuPage.getTotalElements());
    }

    /**
     * دریافت یک منو بر اساس شناسه
     *
     * @param id شناسه منو
     * @return MenuDto
     * @throws ResourceNotFoundException اگر منو با شناسه داده شده یافت نشد
     */
    @Transactional(readOnly = true)
    public MenuDto findById(Long id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("منو با شناسه " + id + " یافت نشد."));
        return menuMapper.toDto(menu);
    }

    /**
     * ایجاد یک منوی جدید
     *
     * @param menuDto داده‌های منوی جدید
     * @return MenuDto ایجاد شده
     */
    @Transactional
    public MenuDto create(MenuDto menuDto) {
        Menu menu = menuMapper.toEntity(menuDto);

        Menu savedMenu = menuRepository.save(menu);
        return menuMapper.toDto(savedMenu);
    }

    /**
     * به‌روزرسانی یک منوی موجود
     *
     * @param id      شناسه منوی مورد نظر برای به‌روزرسانی
     * @param menuDto داده‌های جدید برای به‌روزرسانی
     * @return MenuDto به‌روز شده
     * @throws ResourceNotFoundException اگر منو با شناسه داده شده یافت نشد
     */
    @Transactional
    public MenuDto update(Long id, MenuDto menuDto) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("منو با شناسه " + id + " یافت نشد."));

        menuMapper.partialUpdate(menuDto, existingMenu);

        Menu updatedMenu = menuRepository.save(existingMenu);
        return menuMapper.toDto(updatedMenu);
    }

    /**
     * حذف یک منو بر اساس شناسه
     *
     * @param id شناسه منوی مورد نظر برای حذف
     * @throws ResourceNotFoundException اگر منو با شناسه داده شده یافت نشد
     */
    @Transactional
    public void delete(Long id) {
        if (!menuRepository.existsById(id)) {
            throw new ResourceNotFoundException("منو با شناسه " + id + " یافت نشد.");
        }
        menuRepository.deleteById(id);
    }
}
