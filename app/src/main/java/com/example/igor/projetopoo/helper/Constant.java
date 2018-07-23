package com.example.igor.projetopoo.helper;

// Classe abstrata para armazenamento das constantes utilizadas no aplicativo

public abstract class Constant {
    public static final String RECENT_QUERIES = "Recent Queries";
    public static final String LAST_QUERY = "Last Query";
    public static final String ALL_SUGGESTIONS = "All Suggestions";
    public static final String SHARED_PREFERENCES = "Shared Preferences";
    public static final String SELECTED_CATEGORY = "Selected Category";
    public static final String SELECTED_PRODUCT = "Selected Product";

    public abstract static class Entities {
        public static final String CATEGORIES = "categories";
        public static final String PRODUCTS = "products";
        public static final String FEEDBACKS = "feedbacks";

        public abstract static class Item {
            public static final String TYPE_CATEGORY = "category";
            public static final String TYPE_PRODUCT = "product";
            public static final String TYPE_RECENT = "recent";
        }
    }
}
