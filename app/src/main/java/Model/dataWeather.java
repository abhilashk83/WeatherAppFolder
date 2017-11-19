package Model;

/**
 * Created by abhilashk on 11/18/2017.
 */

public class dataWeather {

    public dataLocation place;
    public String iconData;
    public dataCurrentConditions currentCondition = new dataCurrentConditions();
    public dataTemperature temperature = new dataTemperature();
    public dataWind wind = new dataWind();
    public dataClouds clouds = new dataClouds();
}
