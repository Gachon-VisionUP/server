package GaVisionUp.server.service.google;

import GaVisionUp.server.entity.Post;
import GaVisionUp.server.repository.post.PostRepository;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GooglePostService {

    private final PostRepository postRepository;
    private final Sheets sheetsService;

    @Value("${google.sheets.spreadsheet-id}") // ✅ YML에서 스프레드시트 ID 주입
    private String spreadsheetId;

    private static final String RANGE_BOARD = "참고. 게시판!B7:D"; // ✅ 게시판 데이터 범위

    /**
     * ✅ 데이터베이스의 게시글 데이터를 Google Sheets로 작성
     */
    public void syncBoardToGoogleSheet() {
        try {
            // ✅ 데이터베이스에서 게시글 조회
            List<Post> posts = postRepository.findAll();
            List<List<Object>> dataToWrite = new ArrayList<>();

            int number = 1; // ✅ 번호는 항상 1부터 시작
            for (Post post : posts) {
                List<Object> row = new ArrayList<>();
                row.add(number++); // 번호
                row.add(post.getTitle()); // 제목
                row.add(post.getBody());  // 내용
                dataToWrite.add(row);
            }

            // ✅ Google Sheets에 데이터 작성
            ValueRange data = new ValueRange().setValues(dataToWrite);
            sheetsService.spreadsheets().values()
                    .update(spreadsheetId, RANGE_BOARD, data)
                    .setValueInputOption("RAW")
                    .execute();

        } catch (IOException e) {
            log.error("❌ [ERROR] Google Sheets 데이터를 동기화하는 중 오류 발생", e);
        } catch (Exception e) {
            log.error("❌ [ERROR] 게시판 동기화 중 오류 발생", e);
        }
    }
}
