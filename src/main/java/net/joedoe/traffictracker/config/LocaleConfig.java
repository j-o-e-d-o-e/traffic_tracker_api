package net.joedoe.traffictracker.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.TimeZone;

@PropertySource("classpath:locale.properties")
@Component
public class LocaleConfig implements InitializingBean {
    @Value("${timezone}")
    private String timezone;
    @Value("${language}")
    private String language;
    @Value("${country}")
    private String country;

    @Override
    public void afterPropertiesSet() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        Locale.setDefault(new Locale(language, country));
    }
}
