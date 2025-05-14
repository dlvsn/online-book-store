package org.example.onlybooks.repository.book.spec;

public enum CriteriaSearch {
    TITLE("title"),
    AUTHOR("author"),
    ISBN("isbn"),
    PRICE("price"),
    DESCRIPTION("description"),
    CATEGORY("categoryIds");
    private final String value;

    CriteriaSearch(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
