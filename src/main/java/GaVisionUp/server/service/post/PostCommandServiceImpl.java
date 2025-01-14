package GaVisionUp.server.service.post;

import GaVisionUp.server.entity.Post;
import GaVisionUp.server.entity.User;
import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.entity.enums.Role;
import GaVisionUp.server.global.exception.RestApiException;
import GaVisionUp.server.global.exception.code.status.GlobalErrorStatus;
import GaVisionUp.server.repository.post.PostRepository;
import GaVisionUp.server.repository.user.UserRepository;
import GaVisionUp.server.service.notification.ExpoNotificationService;
import GaVisionUp.server.service.notification.NotificationService;
import GaVisionUp.server.web.dto.post.PostRequest;
import GaVisionUp.server.web.dto.post.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ExpoNotificationService expoNotificationService;


    @Override
    public PostResponse.AddPost addPost(Long userId, PostRequest.AddPost request) {

        if (!checkAdmin(userId)) {
            throw new RestApiException(GlobalErrorStatus._ONLY_ADMIN);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .date(LocalDate.now())
                .build();

        postRepository.save(post);

        List<User> users = userRepository.findAllByRoleEquals(Role.USER);

        if (users.isEmpty()) {
            throw new RestApiException(GlobalErrorStatus._USER_NOT_EXIST);
        }

        for (User user : users) {
            // ✅ 내부 알림 저장
            String title = "📢 게시글 등록!";
            String message = String.format("%s님, %s 게시글이 등록되었습니다!", user.getName(), post.getTitle());
            notificationService.createNotification(user, title, message);

            // ✅ Expo 푸쉬 알림 전송
            expoNotificationService.sendPushNotification(user.getExpoPushToken(), title, message);

            log.info("✅ 게시글 등록 및 알림 전송 완료 - 유저: {}, 제목: {}", user.getName(), post.getTitle());
        }

        return PostResponse.AddPost.builder()
                .postId(post.getId())
                .build();
    }

    /* 추후에 expo 토큰 추가되면 사용
    public PostResponse.AddPost addPost(PostRequest.AddPost request) {
        Post post = Post.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .date(LocalDate.now())
                .build();

        postRepository.save(post);

        List<User> users = userRepository.findAllByRoleEquals(Role.USER);

        if (users.isEmpty()) {
            throw new RestApiException(GlobalErrorStatus._USER_NOT_EXIST);
        }

        for (User user : users) {
            // ✅ 내부 알림 저장
            String title = "📢 게시글 등록!";
            String message = String.format("%s님, %s 게시글이 등록되었습니다!", user.getName(), post.getTitle());
            notificationService.createNotification(user, title, message);

            log.info("✅ 게시글 등록 및 알림 전송 완료 - 유저: {}, 제목: {}", user.getName(), post.getTitle());
        }

        return PostResponse.AddPost.builder()
                .postId(post.getId())
                .build();
    }
     */

    private boolean checkAdmin(Long userId) {
        User admin = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(GlobalErrorStatus._USER_NOT_EXIST));

        return admin.getRole() == Role.ADMIN;
    }
}