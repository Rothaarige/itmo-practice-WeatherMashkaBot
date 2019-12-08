import DBService.DBService;
import Property.PropertyManager;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    private final static String BOT_TOKEN = "BOT_TOKEN";
    private final static String API_OWM = "API_OWM";
    private final static String botName = "Mashka_Bot";
    private static String botToken;
    private static String apiOwm;
    private static DBService dbService;

    public static void main(String[] args) {

        PropertyManager propertyManager = new PropertyManager();
        apiOwm = propertyManager.readPropertyConfig(API_OWM);
        botToken = propertyManager.readPropertyConfig(BOT_TOKEN);

        dbService = new DBService();

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(new Bot(botToken, apiOwm, botName, dbService));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
