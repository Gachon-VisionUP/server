package GaVisionUp.server.service.exp.personalexp;

import GaVisionUp.server.entity.Users;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.PersonalExp;

import java.util.List;

public interface PersonalExpService {
    PersonalExp savePersonalExp(Users users, ExpType expType, int exp);
    List<PersonalExp> getPersonalExpByUserId(Long userId);
    PersonalExp getPersonalExpById(Long id);
    void addExperience(Long userId, int exp);
}
