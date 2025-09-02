package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.entity.Ingredient;

import java.util.List;


public interface IngredientService {

    public List<Ingredient> getAllIngredients();


    public Ingredient getIngredientById(Long id);

    public Ingredient saveIngredient(Ingredient ingredient);
    public void deleteIngredient(Long id);
}
