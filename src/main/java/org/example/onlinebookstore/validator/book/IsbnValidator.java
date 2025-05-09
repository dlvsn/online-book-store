package org.example.onlinebookstore.validator.book;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final String ISBN_10 = "^(?:\\d{1,5}-\\d{1,7}-\\d{1,7}-[\\dX])$";
    private static final String ISBN_13 = "^(?:\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d)$";

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext constraintValidatorContext) {
        return Pattern.compile(ISBN_10).matcher(isbn).matches()
                || Pattern.compile(ISBN_13).matcher(isbn).matches();
    }
}
