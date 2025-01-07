package GaVisionUp.server.web.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${server.url}") // 서버 URL (예: http://your-ec2-public-ip)
    private String serverUrl;

    @GetMapping("/list")
    public List<String> getCharacterImages() {
        // 서버에 저장된 캐릭터 이미지 경로 반환
        return Arrays.asList(
                serverUrl + "/images/man-01.png",
                serverUrl + "/images/man-02.png",
                serverUrl + "/images/man-03.png",
                serverUrl + "/images/man-05.png",
                serverUrl + "/images/woman-01.png",
                serverUrl + "/images/woman-03.png",
                serverUrl + "/images/woman-04.png",
                serverUrl + "/images/woman-05.png"
        );
    }
}
