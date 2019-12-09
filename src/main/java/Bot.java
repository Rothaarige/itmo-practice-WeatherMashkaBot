import DBService.DBService;
import DBService.User;
import JSON.ForecastJSON;
import JSON.WeatherJSON;
import com.google.gson.Gson;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {
    private String botToken;
    private String apiOwm;
    private String botName;
    private final static String URL_PATH = "http://api.openweathermap.org/data/2.5/";
    private final static String URL_WEATHER_TEMPLATE = URL_PATH + "weather?%s&units=metric&lang=ru&appid=%s";
    private final static String URL_FORECAST_TEMPLATE = URL_PATH + "forecast?%s&units=metric&lang=ru&appid=%s";
    private final static String SMILE = "\uD83D\uDE42";
    private final static String CHECK_MARK = "\u2705";
    private final static String CROSS_MARK = "\u274C";
    private final static String CELSIUS = "\u2103";
    private final static double PRESSURE_TO_MM = 133.322d;
    private DBService dbService;
    private boolean isSubscribe = false;

    public Bot(String botToken, String apiOwm, String botName, DBService dbService) {
        this.botToken = botToken;
        this.apiOwm = apiOwm;
        this.botName = botName;
        this.dbService = dbService;

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(sendNotification, 0, 5, TimeUnit.MINUTES);
    }

    @Override
    public void onUpdateReceived(Update update) {

//        new Thread() {
//            @Override
//            public void run() {
//            }
//        }.start();
        User user;
        Message message = update.getMessage();

        if (message != null) {
            user = dbService.getUserByChatID(message.getChatId());
            if (user == null) {
                user = new User(message);
            }

            String text;
            if (message.hasLocation()) {
                String coordinate = String.format("lat=%f&lon=%f", message.getLocation().getLatitude(),
                        message.getLocation().getLongitude());
                WeatherJSON weatherFromMessage = getWeather(coordinate);
                ForecastJSON forecastFromMessage = getForecast(coordinate);
                if (weatherFromMessage == null || forecastFromMessage == null) {
                    text = "Извини, что-то пошло не так с определением координат";
                } else {
                    if (isSubscribe) {
                        text = "Теперь вы каждый день будете получать рассылку";
                        user.setLocation(message);
                        dbService.updateUser(user);
                        isSubscribe = false;
                    } else {
                        text = getWeatherAndForecast(weatherFromMessage, forecastFromMessage);
                    }
                }
                sendMsg(message, text);
            } else if (message.hasText()) {
                switch (message.getText()) {
                    case "/start":
                        text = String.format("Давай начнем %s\nЧтобы узнать текущую погоду и прогноз " +
                                "ты можешь отправить имя интересующего тебя города или свои координаты.\n" +
                                "А еще, ты можешь подписаться на постоянную рассылку погоды и отписаться, когда тебе надоест.\n" +
                                "Используй для этого специальные кнопки %s и %s", SMILE, CHECK_MARK, CROSS_MARK);
                        sendMsg(message, text);
                        break;
                    case "/subscribe":
                        if (dbService.getUserByChatID(message.getChatId()) == null) {
                            dbService.addUser(user);
                        } else {
                            dbService.updateUser(user);
                        }
                        text = "Вы хотите подписаться \nПожалуйста отправте город или координаты";
                        isSubscribe = true;
                        sendMsg(message, text);
                        break;
                    case "/unsubscribe":

                        text = "Вы отказались от рассылки";
                        dbService.deleteUser(user);
                        sendMsg(message, text);
                        break;
                    default:
                        WeatherJSON weatherFromMessage = getWeather("q=" + message.getText());
                        ForecastJSON forecastFromMessage = getForecast("q=" + message.getText());
                        if (weatherFromMessage == null || forecastFromMessage == null) {
                            text = "Я не понимать :(";
                        } else {
                            if (isSubscribe) {
                                user.setCity(message);
                                dbService.updateUser(user);
                                text = "Теперь вы каждый день будете получать рассылку";
                                isSubscribe = false;
                            } else {
                                text = getWeatherAndForecast(weatherFromMessage, forecastFromMessage);
                            }
                        }
                        sendMsg(message, text);
                }
            }
        }
    }

    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        //sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/subscribe"));
        keyboardFirstRow.add(new KeyboardButton("/unsubscribe"));

        keyboardRowList.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private WeatherJSON getWeather(String findWeather) {
        String urlAddress = String.format(URL_WEATHER_TEMPLATE, findWeather, apiOwm);
        WeatherJSON weatherJSON;
        URL url;
        try {
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Какая-то ошибка, при попытке прочитать URL");
        }

        Gson gson = new Gson();
        try (InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream())) {
            weatherJSON = gson.fromJson(inputStreamReader, WeatherJSON.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения данных с URL");
        }
        return weatherJSON;
    }

    private ForecastJSON getForecast(String findWeather) {
        String urlAddress = String.format(URL_FORECAST_TEMPLATE, findWeather, apiOwm);
        ForecastJSON forecastJSON;
        URL url;
        try {
            url = new URL(urlAddress);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Какая-то ошибка, при попытке прочитать URL");
        }
        Gson gson = new Gson();
        try (InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream())) {
            forecastJSON = gson.fromJson(inputStreamReader, ForecastJSON.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения данных с URL");
        }
        return forecastJSON;
    }

    private String getWeatherAndForecast(WeatherJSON weatherFromMessage, ForecastJSON forecastFromMessage) {
        String text;
        String name = weatherFromMessage.getName();
        double currrentTemp = weatherFromMessage.getMain().getTemp();
        String currentDescription = weatherFromMessage.getWeather().get(0).getDescription();
        int currentHumidity = weatherFromMessage.getMain().getHumidity();
        double currentPressure = (weatherFromMessage.getMain().getPressure() / PRESSURE_TO_MM);
        double speedWind = weatherFromMessage.getWind().getSpeed();

        String descriptionWind;
        if (speedWind >= 0 && speedWind <= 5) {
            descriptionWind = "слабый";
        } else if (speedWind >= 6 && speedWind <= 14) {
            descriptionWind = "умеренный";
        } else if (speedWind >= 15 && speedWind <= 24) {
            descriptionWind = "сильный";
        } else if (speedWind >= 25 && speedWind <= 32) {
            descriptionWind = "очень сильный";
        } else {
            descriptionWind = "ураганный";
        }
        long data = weatherFromMessage.getDt();
        String tomorrowData = new SimpleDateFormat("yyyy-MM-dd").format(new Date((data + 24 * 60 * 60) * 1000));

        StringBuilder forecastText = new StringBuilder();
        for (int i = 0; i < forecastFromMessage.getList().size(); i++) {
            String targetDate = forecastFromMessage.getList().get(i).getDtTxt();
            if ((tomorrowData + " 00:00:00").equals(targetDate)) {
                forecastText.append(String.format("Ночю: %.1f%s %s\n",
                        forecastFromMessage.getList().get(i).getMain().getTemp(), CELSIUS,
                        forecastFromMessage.getList().get(i).getWeather().get(0).getDescription()));
            }
            if ((tomorrowData + " 06:00:00").equals(targetDate)) {
                forecastText.append(String.format("Утром: %.1f%s %s\n",
                        forecastFromMessage.getList().get(i).getMain().getTemp(), CELSIUS,
                        forecastFromMessage.getList().get(i).getWeather().get(0).getDescription()));
            }
            if ((tomorrowData + " 12:00:00").equals(targetDate)) {
                forecastText.append(String.format("Днем: %.1f%s %s\n",
                        forecastFromMessage.getList().get(i).getMain().getTemp(), CELSIUS,
                        forecastFromMessage.getList().get(i).getWeather().get(0).getDescription()));
            }
            if ((tomorrowData + " 18:00:00").equals(targetDate)) {
                forecastText.append(String.format("Вечером: %.1f%s %s\n",
                        forecastFromMessage.getList().get(i).getMain().getTemp(), CELSIUS,
                        forecastFromMessage.getList().get(i).getWeather().get(0).getDescription()));
            }
        }

        text = String.format("Погода сейчас в %s\nТемпература: %.1f%s, %s\n" +
                        "Влажность: %d %%,\n" +
                        "Давление %.2f мм.рт.ст\n" +
                        "Ветер: %s: %.1f м/с\n\n" +
                        "Завтра ожидается: \n%s",
                name, currrentTemp, CELSIUS, currentDescription, currentHumidity, currentPressure,
                descriptionWind, speedWind, forecastText.toString());
        return text;
    }

    private Runnable sendNotification = new Runnable() {
        @Override
        public void run() {
            sendToAllSubscribeUsers();
        }
    };

    public void sendToAllSubscribeUsers() {
        List<User> userList = dbService.getSubscribeUsers();
        if (userList != null) {
            for (User user : userList) {
                sendForecast(user);
            }
        }
    }

    public void sendForecast(User user) {
        long chatID = user.getChatId();
        WeatherJSON weatherFromMessage;
        ForecastJSON forecastFromMessage;
        if (user.getLatitude() != 0 && user.getLongitude() != 0) {
            String coordinate = String.format("lat=%f&lon=%f", user.getLatitude(),
                    user.getLongitude());
            weatherFromMessage = getWeather(coordinate);
            forecastFromMessage = getForecast(coordinate);
        } else {
            weatherFromMessage = getWeather("q=" + user.getCity());
            forecastFromMessage = getForecast("q=" + user.getCity());
        }

        SendMessage send = new SendMessage();
        send.setChatId(chatID);
        send.setText(getWeatherAndForecast(weatherFromMessage, forecastFromMessage));
        try {
            execute(send);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
