package GaVisionUp.server.service.exp.personalexp;

import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.ExpType;
import GaVisionUp.server.entity.exp.ExpBar;
import GaVisionUp.server.entity.exp.PersonalExp;
import GaVisionUp.server.repository.exp.expbar.ExpBarRepository;
import GaVisionUp.server.repository.exp.personalexp.PersonalExpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalExpServiceImpl implements PersonalExpService {

    private final PersonalExpRepository personalExpRepository;
    private final ExpBarRepository expBarRepository;

    /**
     * 개인 경험치 저장
     * ExpBar의 totalExp도 업데이트
     */
    @Transactional
    public PersonalExp savePersonalExp(User user, ExpType expType, int exp) {
        // ExpBar 조회
        ExpBar expBar = expBarRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 ExpBar가 존재하지 않습니다."));

        // PersonalExp 생성
        PersonalExp personalExp = new PersonalExp(user, expType, exp, expBar);

        // ExpBar의 totalExp 업데이트
        expBar.setTotalExp(expBar.getTotalExp() + exp);
        expBarRepository.save(expBar);

        // PersonalExp 저장
        return personalExpRepository.save(personalExp);
    }

    /**
     * 특정 사용자의 모든 개인 경험치 조회
     */
    public List<PersonalExp> getPersonalExpByUserId(Long userId) {
        return personalExpRepository.findByUserId(userId);
    }

    /**
     * 특정 개인 경험치 조회
     */
    public PersonalExp getPersonalExpById(Long id) {
        return personalExpRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 개인 경험치가 존재하지 않습니다."));
    }

    /**
     * 경험치 추가 (ExpBar의 totalExp만 업데이트)
     */
    @Transactional
    public void addExperience(Long userId, int exp) {
        // ExpBar 업데이트
        ExpBar expBar = expBarRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 ExpBar가 존재하지 않습니다."));
        expBar.setTotalExp(expBar.getTotalExp() + exp);
        expBarRepository.save(expBar);
    }
}
