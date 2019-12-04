import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyManager {
    public PropertyManager() {
    }

    String readPropertyConfig(String name) {
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать файл с настройками");
        }
        return properties.getProperty(name);
    }
}
