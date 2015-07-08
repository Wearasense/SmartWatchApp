package io.wearasense.wearasense.Models;

/**
 * Created by goofyahead on 8/07/15.
 */
public class Category {
    private String categoryName;
    private int resourceImg;

    public Category(String categoryName, int resourceImg) {
        this.categoryName = categoryName;
        this.resourceImg = resourceImg;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getResourceImg() {
        return resourceImg;
    }

    public void setResourceImg(int resourceImg) {
        this.resourceImg = resourceImg;
    }
}
