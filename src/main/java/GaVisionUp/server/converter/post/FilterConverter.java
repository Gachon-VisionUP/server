package GaVisionUp.server.converter.post;

import GaVisionUp.server.entity.enums.Filter;
import org.springframework.core.convert.converter.Converter;

public class FilterConverter implements Converter<String, Filter> {

    @Override
    public Filter convert(String param) {
        return Filter.from(param);
    }
}
