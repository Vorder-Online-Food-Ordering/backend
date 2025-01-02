package com.example.backend.service;

import com.example.backend.model.IngredientCategory;
import com.example.backend.model.IngredientsItem;
import com.example.backend.model.Restaurant;
import com.example.backend.repository.IngredientCategoryRepository;
import com.example.backend.repository.IngredientsItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientServiceImpl implements IngredientsService{
    private final RestaurantService restaurantService;
    private IngredientsItemRepository ingredientsItemRepository;
    private IngredientCategoryRepository ingredientCategoryRepository;

    @Autowired
    public IngredientServiceImpl(IngredientCategoryRepository ingredientCategoryRepository, IngredientsItemRepository ingredientsItemRepository, RestaurantService restaurantService) {
        this.ingredientCategoryRepository = ingredientCategoryRepository;
        this.ingredientsItemRepository = ingredientsItemRepository;
        this.restaurantService = restaurantService;
    }

    @Override
    public IngredientCategory createIngredientCategory(String name, Long restaurantId) throws Exception {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        IngredientCategory ingredientCategory = new IngredientCategory();
        ingredientCategory.setName(name);
        ingredientCategory.setRestaurant(restaurant);

        return ingredientCategoryRepository.save(ingredientCategory);
    }

    @Override
    public IngredientCategory findIngredientCategoryById(Long id) throws Exception {
        Optional<IngredientCategory> opt = ingredientCategoryRepository.findById(id);
        if(opt.isEmpty()){
            throw new Exception("Ingredient Category not found");
        }

        return opt.get();
    }

    @Override
    public List<IngredientCategory> findIngredientCategoryByRestaurantId(Long id) throws Exception {
        restaurantService.findRestaurantById(id);
        return ingredientCategoryRepository.findByRestaurantId(id);
    }

    @Override
    public IngredientsItem createIngredientsItem(Long restaurantId, String ingredientName, Long categoryId) throws Exception {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        IngredientCategory category = findIngredientCategoryById(categoryId);

        IngredientsItem ingredientsItem = new IngredientsItem();
        ingredientsItem.setRestaurant(restaurant);
        ingredientsItem.setName(ingredientName);
        ingredientsItem.setCategory(category);

        IngredientsItem saved  = ingredientsItemRepository.save(ingredientsItem);
        category.getIngredients().add(saved);

        return saved;
    }

    @Override
    public List<IngredientsItem> findRestaurantsIngredients(Long restaurantId) throws Exception {
        return ingredientsItemRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public IngredientsItem updateStock(Long id) throws Exception {
        Optional<IngredientsItem> opt = ingredientsItemRepository.findById(id);
        if(opt.isEmpty()){
            throw new Exception("Ingredient item not found");
        }

        IngredientsItem ingredientsItem = opt.get();
        ingredientsItem.setInStoke(!ingredientsItem.isInStoke());

        return ingredientsItemRepository.save(ingredientsItem);
    }
}
