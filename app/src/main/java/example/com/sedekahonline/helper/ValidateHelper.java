package example.com.sedekahonline.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateHelper {

    public static boolean isValidName(final String name) {

        Pattern pattern;
        Matcher matcher;

        final String NAME_PATTERN = "^[a-zA-Z\\s]*$";

        pattern = Pattern.compile(NAME_PATTERN);
        matcher = pattern.matcher(name);

        return matcher.matches();

    }

    public static  boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
