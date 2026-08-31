package com.smartmobilehub.catalog.service;

import com.smartmobilehub.catalog.dto.request.CreateBrandRequest;
import com.smartmobilehub.catalog.dto.response.BrandResponse;
import com.smartmobilehub.catalog.entity.Brand;
import com.smartmobilehub.catalog.exception.BusinessException;
import com.smartmobilehub.catalog.exception.ResourceNotFoundException;
import com.smartmobilehub.catalog.mapper.CatalogMapper;
import com.smartmobilehub.catalog.repository.BrandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final CatalogMapper mapper;

    public BrandService(BrandRepository brandRepository, CatalogMapper mapper) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
    }

    public List<BrandResponse> getAllBrands() {
        return brandRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(mapper::toBrandResponse)
                .collect(Collectors.toList());
    }

    public BrandResponse getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        return mapper.toBrandResponse(brand);
    }

    @Transactional
    public BrandResponse createBrand(CreateBrandRequest request) {
        String slug = generateSlug(request.getName());
        if (brandRepository.existsBySlug(slug)) {
            throw new BusinessException("Brand with this name already exists", "BRAND_EXISTS");
        }

        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setSlug(slug);
        brand.setLogoUrl(request.getLogoUrl());
        brand.setDescription(request.getDescription());

        Brand saved = brandRepository.save(brand);
        return mapper.toBrandResponse(saved);
    }

    @Transactional
    public BrandResponse updateBrand(Long id, CreateBrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));

        String newSlug = generateSlug(request.getName());
        if (!newSlug.equals(brand.getSlug()) && brandRepository.existsBySlug(newSlug)) {
            throw new BusinessException("Brand with this name already exists", "BRAND_EXISTS");
        }

        brand.setName(request.getName());
        brand.setSlug(newSlug);
        brand.setLogoUrl(request.getLogoUrl());
        brand.setDescription(request.getDescription());

        Brand saved = brandRepository.save(brand);
        return mapper.toBrandResponse(saved);
    }

    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand", id));
        brand.setActive(false);
        brandRepository.save(brand);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
