package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.entity.SkinType;
import com.ttt.CosmeticStore.repository.SkinTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkinTypeService {
    private final SkinTypeRepository skinTypeRepository;

    public List<SkinType> getAllSkinTypes() {
        return skinTypeRepository.findAll();
    }

    public SkinType getSkinTypeById(Long id) {
        return skinTypeRepository.findById(id).orElse(null);
    }

    public SkinType saveSkinType(SkinType skinType) {
        return skinTypeRepository.save(skinType);
    }

    public void deleteSkinType(Long id) {
        skinTypeRepository.deleteById(id);
    }
}
