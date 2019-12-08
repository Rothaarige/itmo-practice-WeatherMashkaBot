package DBService;

import org.telegram.telegrambots.meta.api.objects.Message;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @Column(name = "UserID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userID;

    @Column(name = "UserFirstName")
    private String firstName;

    @Column(name = "UserLastName")
    private String lastName;

    @Column(name = "UserName")
    private String userName;

    @Column(name = "ChatId")
    private long chatId;
    @Column(name = "City")
    private String city;
    @Column(name = "Latitude")
   private float latitude;
    @Column(name = "Longitude")
    private float longitude;

    public User(){
    }

    public User(Message message) {
//        this.setId(-1);
        this.firstName = message.getFrom().getFirstName();
        this.lastName = message.getFrom().getLastName();
        this.userName = message.getFrom().getUserName();

        this.chatId = message.getChatId();

        if (message.hasLocation()){
            this.latitude = message.getLocation().getLatitude();
            this.longitude = message.getLocation().getLongitude();
        } else {
            this.city = message.getText();
        }
    }

    public void setId(long id) {
        this.userID = id;
    }
    public long getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public long getChatId() {
        return chatId;
    }

    public String getCity() {
        return city;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", chatId=" + chatId +
                ", city='" + city + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
