package ru.skillbox.socialnet.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchemaConfig implements BeanPostProcessor {

  @Value("${db.schema}")
  private String schemaName;

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (!StringUtils.isEmpty(schemaName) && bean instanceof DataSource) {
      DataSource dataSource = (DataSource) bean;
      try (Connection conn = dataSource.getConnection();
          Statement statement = conn.createStatement()) {
        log.info("Going to create DB schema '{}' if not exists.", schemaName);
        statement.execute("create schema if not exists " + schemaName);
      } catch (SQLException e) {
        throw new RuntimeException("Failed to create schema '" + schemaName + "'", e);
      }
    }
    return bean;
  }
}