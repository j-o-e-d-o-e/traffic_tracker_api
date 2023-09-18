package net.joedoe.traffictracker.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Locale;
import java.util.TimeZone;

@PropertySource("classpath:application-config.properties")
@Configuration
public class LocaleConfig implements InitializingBean {
    @Value("${locale.timezone}")
    private String timezone;
    @Value("${locale.lang}")
    private String language;
    @Value("${locale.country}")
    private String country;

    @Override
    public void afterPropertiesSet() {
        TimeZone.setDefault(TimeZone.getTimeZone(timezone));
        Locale.setDefault(new Locale(language, country));
    }
}
