package GaVisionUp.server.global.exception.code.status;

import GaVisionUp.server.global.exception.code.ApiCodeDto;
import GaVisionUp.server.global.exception.code.ApiErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorStatus implements ApiErrorCodeInterface {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON402", "Validation Error입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 정보를 찾을 수 없습니다."),
    _METHOD_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "COMMON405", "Argument Type이 올바르지 않습니다."),
    _INTERNAL_PAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "페이지 에러, 0 이상의 페이지를 입력해주세요"),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "예외처리 테스트입니다."),

    // User Error Status
    _USER_NOT_EXIST(HttpStatus.BAD_REQUEST, "USER4001", "존재하지 않는 사원입니다."),
    _INVALID_USER(HttpStatus.BAD_REQUEST, "USER4002", "일치하지 않는 유저입니다."),
    _INVALID_DEPARTMENT(HttpStatus.BAD_REQUEST, "USER4003", "잘못된 소속입니다."),
    _INVALID_ROLE(HttpStatus.BAD_REQUEST, "USER4004", "잘못된 권한입니다."),
    _ID_WRONG(HttpStatus.BAD_REQUEST, "USER4005", "잘못된 아이디입니다."),
    _PW_WRONG(HttpStatus.BAD_REQUEST, "USER4006", "비밀번호가 틀렸습니다."),
    _NOT_LOGIN(HttpStatus.UNAUTHORIZED, "USER4007", "로그인 되지 않은 사용자입니다."),
    _NOT_FOUND_INFORMATION(HttpStatus.NOT_FOUND, "USER4008", "사용자 정보를 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
    private final String code;
    private final String message;

    @Override
    public ApiCodeDto getErrorCode() {
        return ApiCodeDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(isSuccess)
                .code(code)
                .message(message)
                .build();
    }
}