package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.entity.SkinType;
import com.ttt.CosmeticStore.mapper.SkinTypeMapper;
import com.ttt.CosmeticStore.repository.SkinTypeRepository;
import com.ttt.CosmeticStore.service.SkinTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SkinTypeServiceImpl implements SkinTypeService {

    private final SkinTypeRepository skinTypeRepository;
    private final SkinTypeMapper skinTypeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SkinType> getAllSkinTypes() {
        try {
            List<SkinType> skinTypes = skinTypeRepository.findAll();
            return skinTypeMapper.toEntityList(skinTypes);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách loại da: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SkinType getSkinTypeById(Long id) {
        if (id == null) {
            throw new RuntimeException("ID loại da không được để trống");
        }

        try {
            return skinTypeRepository.findById(id).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm loại da với ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public SkinType saveSkinType(SkinType skinType) {
        try {
            SkinType preparedSkinType = skinTypeMapper.prepareForSave(skinType);

            validateUniqueNameOnSave(preparedSkinType);

            return skinTypeRepository.save(preparedSkinType);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lưu loại da: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteSkinType(Long id) {
        if (id == null) {
            throw new RuntimeException("ID loại da không được để trống");
        }

        try {
            if (!skinTypeRepository.existsById(id)) {
                throw new RuntimeException("Không tìm thấy loại da với ID: " + id);
            }
            skinTypeRepository.deleteById(id);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("Lỗi khi xóa loại da với ID " + id + ": " + e.getMessage(), e);
        }
    }


    private void validateUniqueNameOnSave(SkinType skinType) {

        List<SkinType> existingSkinTypes = skinTypeRepository.findAll();
        boolean nameExists = existingSkinTypes.stream()
                .anyMatch(existing ->
                        existing.getName().equalsIgnoreCase(skinType.getName()) &&
                                !existing.getId().equals(skinType.getId())
                );

        if (nameExists) {
            throw new RuntimeException("Tên loại da '" + skinType.getName() + "' đã tồn tại");
        }
    }

}