package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.entity.SkinType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SkinTypeMapper {


    public List<SkinType> toEntityList(List<SkinType> skinTypes) {
        if (skinTypes == null) {
            return null;
        }

        return skinTypes.stream()
                .collect(Collectors.toList());
    }


    public SkinType prepareForSave(SkinType skinType) {
        if (skinType == null) {
            throw new RuntimeException("Skin type không được để trống");
        }

        if (skinType.getName() == null || skinType.getName().trim().isEmpty()) {
            throw new RuntimeException("Tên loại da không được để trống");
        }

        // Trim and normalize the name
        skinType.setName(skinType.getName().trim());

        return skinType;
    }

    public SkinType copyEntity(SkinType original) {
        if (original == null) {
            return null;
        }

        SkinType copy = new SkinType();
        copy.setId(original.getId());
        copy.setName(original.getName());

        return copy;
    }
}