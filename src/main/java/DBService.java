import org.hibernate.cfg.Configuration;

public class DBService {

    private Configuration getMySqlConfiguration() {
        Configuration configuration = new Configuration();
        PropertyManager propertyManager = new PropertyManager();

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/telegrambot");
        configuration.setProperty("hibernate.connection.username", propertyManager.readPropertyConfig("SQL_DB_USERNAME"));
        configuration.setProperty("hibernate.connection.password", propertyManager.readPropertyConfig("SQL_DB_PASS"));
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

        return configuration;
    }

}
