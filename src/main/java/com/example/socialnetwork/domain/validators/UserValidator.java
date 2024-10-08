package com.example.socialnetwork.domain.validators;
import com.example.socialnetwork.domain.User;

public class UserValidator implements Validator<User> {

    private static UserValidator instance = null;
    private UserValidator(){};

    public static UserValidator getInstance() {
        if(instance == null)
            instance = new UserValidator();
        return instance;
    }

    @Override
    public void validate(User entity) throws ValidationException {
        String errors = "";

        if(!entity.getFirstName().matches("[a-zA-z]+"))
            errors += "First name must contain only letters!\n";
        if(!entity.getLastName().matches("[a-zA-z]+"))
            errors += "Last name must contain only letters!\n";
        if(!errors.isEmpty())
            throw new ValidationException(errors);
    }
}

