import com.sun.org.apache.xerces.internal.impl.PropertyManager;
import jdk.nashorn.internal.runtime.Property;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    private final static String BOT_TOKEN = "BOT_TOKEN";
    private final static String API_OWM = "API_OWM";
    private final static String botName = "Mashka_Bot";
    private static String botToken;
    private static String apiOwm;

    public static void main(String[] args) {
        class PropertyManager {
            private String readPropertyConfig(String name) {
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

        PropertyManager propertyManager = new PropertyManager();
        apiOwm = propertyManager.readPropertyConfig(API_OWM);
        botToken = propertyManager.readPropertyConfig(BOT_TOKEN);

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new Bot(botToken, apiOwm, botName));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
