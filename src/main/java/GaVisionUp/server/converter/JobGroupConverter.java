package GaVisionUp.server.converter;

import GaVisionUp.server.entity.enums.Filter;
import GaVisionUp.server.entity.enums.JobGroup;
import org.springframework.core.convert.converter.Converter;

public class JobGroupConverter implements Converter<String, JobGroup> {

    @Override
    public JobGroup convert(String param) {
        return JobGroup.from(param);
    }
}
