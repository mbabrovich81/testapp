package com.itrexgroup.turvo.testapp.config;

import com.itrexgroup.turvo.testapp.service.job.SchedulerJobFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by maxim.babrovich on 08.04.2019.
 */

@Configuration
public class SchedulerConfig {

    private ApplicationContext applicationContext;

    private QuartzProperties quartzProperties;

    @Bean(name = "quartzHikariConfig")
    @ConfigurationProperties(prefix = "quartz.datasource.hikari")
    public HikariConfig quartzHikariConfig() {
        return new HikariConfig();
    }

    @Bean(name = "quartzDataSource")
    public DataSource quartzDataSource() {
        return new HikariDataSource(quartzHikariConfig());
    }


    @Bean(name = "schedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerJobFactory jobFactory = new SchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        Properties properties = new Properties();
        properties.putAll(quartzProperties.getProperties());

        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(quartzDataSource());
        factory.setQuartzProperties(properties);
        factory.setJobFactory(jobFactory);
        return factory;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setQuartzProperties(QuartzProperties quartzProperties) {
        this.quartzProperties = quartzProperties;
    }
}
