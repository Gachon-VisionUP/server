package GaVisionUp.server.web.dto.post;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


public class PostResponse {

    @Getter
    @Setter
    @Builder
    public static class Preview{
        Long postId;
        String title;
        String body;
        LocalDate date;
    }

    /*@Getter
    @Setter
    @Builder
    public static class PreviewList{
        List<Preview> previewList;
        boolean hasNext;
        Long lastValue;
    }*/

    @Getter
    @Setter
    @Builder
    public static class PreviewList{
        List<Preview> previewList;
    }

    @Getter
    @Setter
    @Builder
    public static class Detail{
        String title;
        String body;
        LocalDate date;
    }

    @Getter
    @Setter
    @Builder
    public static class AddPost{
        Long postId;
    }
}