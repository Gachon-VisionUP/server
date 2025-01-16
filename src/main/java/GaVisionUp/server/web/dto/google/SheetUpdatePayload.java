package GaVisionUp.server.web.dto.google;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@NoArgsConstructor
@Setter
public class SheetUpdatePayload {
    private String sheet;
    private String row;
    private String column;
    private String value;

    @Override
    public String toString() {
        return "SheetUpdatePayload{" +
                "sheetName='" + sheet + '\'' +
                ", row=" + row +
                ", column=" + column +
                ", value='" + value + '\'' +
                '}';
    }
}