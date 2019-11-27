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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    private String botToken;
    private String apiOwm;
    private final static String URL_PATH = "http://api.openweathermap.org/data/2.5/";
    private final static String URL_WEATHER_TEMPLATE = URL_PATH + "weather?%s&units=metric&lang=ru&appid=%s";
    private final static String URL_FORECAST_TEMPLATE = URL_PATH + "forecast?%s&units=metric&lang=ru&appid=%s";
    private final static String SMILE = "\uD83D\uDE42";
    private final static String CHECK_MARK = "\u2705";
    private final static String CROSS_MARK = "\u274C";
    private final static String CELSIUS = "\u2103";

    public Bot() {

    }

    @Override
    public void onUpdateReceived(Update update) {
//        new Thread() {
//            @Override
//            public void run() {
//            }
//        }.start();

        Message message = update.getMessage();

        if (message != null) {
            String text;
            if (message.hasLocation()) {
                String coordinate = String.format("lat=%f&lon=%f", message.getLocation().getLatitude(),
                        message.getLocation().getLongitude());
                WeatherJSON weatherFromMessage = getWeather(coordinate);
                ForecastJSON forecastFromMessage = getForecast(coordinate);
                if (weatherFromMessage == null || forecastFromMessage == null) {
                    text = "Извини, что-то пошло не так с определением координат";
                } else {
                    text = String.format("Погодка:\nГородок: %s\nТекущая температурка: %.1f%s\n" +
                                    "Завтра ожидается от %.1f%s до %.1f%s",
                            weatherFromMessage.getName(), weatherFromMessage.getMain().getTemp(), CELSIUS,
                            //       15.2, 16.2);
                            forecastFromMessage.getList().get(0).getMain().getTempMin(), CELSIUS,
                            forecastFromMessage.getList().get(9).getMain().getTempMax(), CELSIUS);
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
                        text = "Теперь вы каждый день будете получать рассылку";
                        sendMsg(message, text);
                        break;
                    case "/unsubscribe":
                        text = "Вы отказались от рассылки";
                        sendMsg(message, text);
                        break;
                    default:
                        WeatherJSON weatherFromMessage = getWeather("q=" + message.getText());
                        ForecastJSON forecastFromMessage = getForecast("q=" + message.getText());
                        if (weatherFromMessage == null || forecastFromMessage == null) {
                            text = "Я не понимать :(";
                        } else {
                            text = String.format("Погодка:\nГородок: %s\nТекущая температурка: %.1f%s\n" +
                                            "Завтра ожидается от %.1f%s до %.1f%s",
                                    weatherFromMessage.getName(), weatherFromMessage.getMain().getTemp(), CELSIUS,
                                    forecastFromMessage.getList().get(0).getMain().getTempMin(), CELSIUS,
                                    forecastFromMessage.getList().get(9).getMain().getTempMax(), CELSIUS);
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
        return "Mashka_Bot";
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

        keyboardFirstRow.add(new KeyboardButton(String.format("%s хочу получать погоду", CHECK_MARK)));
        keyboardFirstRow.add(new KeyboardButton(String.format("%s надоели присылать", CROSS_MARK)));

        keyboardRowList.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    private static WeatherJSON getWeather(String findWeather) {
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

    private static ForecastJSON getForecast(String findWeather) {
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
}
