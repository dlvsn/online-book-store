package org.example.onlybooks.dto.book;

public record BookSearchParameters(String[] titles,
                                   String[] authors,
                                   String[] isbn,
                                   String[] price,
                                   String[] description,
                                   String[] categoryIds
                                   ) {
    public static class Builder {
        private String[] titles;
        private String[] authors;
        private String[] isbn;
        private String[] price;
        private String[] description;
        private String[] categoryIds;

        public Builder titles(String[] titles) {
            this.titles = titles;
            return this;
        }

        public Builder authors(String[] authors) {
            this.authors = authors;
            return this;
        }

        public Builder isbn(String[] isbn) {
            this.isbn = isbn;
            return this;
        }

        public Builder price(String[] price) {
            this.price = price;
            return this;
        }

        public Builder description(String[] description) {
            this.description = description;
            return this;
        }

        public Builder categoryIds(String[] categoryIds) {
            this.categoryIds = categoryIds;
            return this;
        }

        public BookSearchParameters build() {
            return new BookSearchParameters(titles, authors, isbn, price, description, categoryIds);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
