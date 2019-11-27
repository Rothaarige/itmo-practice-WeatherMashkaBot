import com.google.gson.annotations.SerializedName;

public class ForecastJSON {
    @SerializedName("cod")
    private final String cod;

    @SerializedName("message")
    private final int message;

    @SerializedName("cnt")
    private final int cnt;

    @SerializedName("list")
    private final java.util.List<List> list;

    @SerializedName("city")
    private final City city;

    public ForecastJSON(String cod, int message, int cnt, java.util.List<List> list, City city) {
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

    public String getCod() {
        return cod;
    }

    public int getMessage() {
        return message;
    }

    public int getCnt() {
        return cnt;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public City getCity() {
        return city;
    }

    public static class List {
        @SerializedName("dt")
        private final int dt;

        @SerializedName("main")
        private final Main main;

        @SerializedName("weather")
        private final java.util.List<Weather> weather;

        @SerializedName("clouds")
        private final Clouds clouds;

        @SerializedName("wind")
        private final Wind wind;

        @SerializedName("sys")
        private final Sys sys;

        @SerializedName("dt_txt")
        private final String dtTxt;

        public List(int dt, Main main, java.util.List<Weather> weather, Clouds clouds, Wind wind,
                    Sys sys, String dtTxt) {
            this.dt = dt;
            this.main = main;
            this.weather = weather;
            this.clouds = clouds;
            this.wind = wind;
            this.sys = sys;
            this.dtTxt = dtTxt;
        }

        public int getDt() {
            return dt;
        }

        public Main getMain() {
            return main;
        }

        public java.util.List<Weather> getWeather() {
            return weather;
        }

        public Clouds getClouds() {
            return clouds;
        }

        public Wind getWind() {
            return wind;
        }

        public Sys getSys() {
            return sys;
        }

        public String getDtTxt() {
            return dtTxt;
        }

        public static class Main {
            @SerializedName("temp")
            private final double temp;

            @SerializedName("temp_min")
            private final double tempMin;

            @SerializedName("temp_max")
            private final double tempMax;

            @SerializedName("pressure")
            private final int pressure;

            @SerializedName("sea_level")
            private final int seaLevel;

            @SerializedName("grnd_level")
            private final int grndLevel;

            @SerializedName("humidity")
            private final int humidity;

            @SerializedName("temp_kf")
            private final double tempKf;

            public Main(double temp, double tempMin, double tempMax, int pressure, int seaLevel,
                        int grndLevel, int humidity, int tempKf) {
                this.temp = temp;
                this.tempMin = tempMin;
                this.tempMax = tempMax;
                this.pressure = pressure;
                this.seaLevel = seaLevel;
                this.grndLevel = grndLevel;
                this.humidity = humidity;
                this.tempKf = tempKf;
            }

            public double getTemp() {
                return temp;
            }

            public double getTempMin() {
                return tempMin;
            }

            public double getTempMax() {
                return tempMax;
            }

            public int getPressure() {
                return pressure;
            }

            public int getSeaLevel() {
                return seaLevel;
            }

            public int getGrndLevel() {
                return grndLevel;
            }

            public int getHumidity() {
                return humidity;
            }

            public double getTempKf() {
                return tempKf;
            }
        }

        public static class Weather {
            @SerializedName("id")
            private final int id;

            @SerializedName("main")
            private final String main;

            @SerializedName("description")
            private final String description;

            @SerializedName("icon")
            private final String icon;

            public Weather(int id, String main, String description, String icon) {
                this.id = id;
                this.main = main;
                this.description = description;
                this.icon = icon;
            }

            public int getId() {
                return id;
            }

            public String getMain() {
                return main;
            }

            public String getDescription() {
                return description;
            }

            public String getIcon() {
                return icon;
            }
        }

        public static class Clouds {
            @SerializedName("all")
            private final int all;

            public Clouds(int all) {
                this.all = all;
            }

            public int getAll() {
                return all;
            }
        }

        public static class Wind {
            @SerializedName("speed")
            private final double speed;

            @SerializedName("deg")
            private final int deg;

            public Wind(double speed, int deg) {
                this.speed = speed;
                this.deg = deg;
            }

            public double getSpeed() {
                return speed;
            }

            public int getDeg() {
                return deg;
            }
        }

        public static class Sys {
            @SerializedName("pod")
            private final String pod;

            public Sys(String pod) {
                this.pod = pod;
            }

            public String getPod() {
                return pod;
            }
        }
    }

    public static class City {
        @SerializedName("id")
        private final int id;

        @SerializedName("name")
        private final String name;

        @SerializedName("coord")
        private final Coord coord;

        @SerializedName("country")
        private final String country;

        @SerializedName("timezone")
        private final int timezone;

        @SerializedName("sunrise")
        private final int sunrise;

        @SerializedName("sunset")
        private final int sunset;

        public City(int id, String name, Coord coord, String country, int timezone, int sunrise,
                    int sunset) {
            this.id = id;
            this.name = name;
            this.coord = coord;
            this.country = country;
            this.timezone = timezone;
            this.sunrise = sunrise;
            this.sunset = sunset;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Coord getCoord() {
            return coord;
        }

        public String getCountry() {
            return country;
        }

        public int getTimezone() {
            return timezone;
        }

        public int getSunrise() {
            return sunrise;
        }

        public int getSunset() {
            return sunset;
        }

        public static class Coord {
            @SerializedName("lat")
            private final double lat;

            @SerializedName("lon")
            private final double lon;

            public Coord(double lat, double lon) {
                this.lat = lat;
                this.lon = lon;
            }

            public double getLat() {
                return lat;
            }

            public double getLon() {
                return lon;
            }
        }
    }
}
