package com.paar.ch9.datasource;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.paar.ch9.IconMarker;
import com.paar.ch9.Marker;
import com.paar.ch9.R;


public class LocalDataSource extends DataSource {
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private static Bitmap icon = null;
    
    public LocalDataSource(Resources res) {
        if (res == null) throw new NullPointerException();
        
        createIcon(res);
    }
    
    protected void createIcon(Resources res) {
        if (res == null) throw new NullPointerException();
        
        icon = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
    }
    
    public List<Marker> getMarkers() {
//        Marker atl = new IconMarker("ATL", 59.956540, 30.321217, 0, Color.DKGRAY, icon);
//        cachedMarkers.add(atl);

//        Marker home = new Marker("Mt Laurel", 59.956995, 30.341660, 0, Color.YELLOW);
//        cachedMarkers.add(home);

        cachedMarkers.add( new Marker("left"                        ,59.956540,30.321217,0, Color.YELLOW, "Исаакий.aac"));
        cachedMarkers.add( new Marker("down"                        ,59.945605,30.342417,0, Color.YELLOW, "Здание ФСБ.aac"));
        cachedMarkers.add( new Marker("right"                       ,59.956497,30.364089,0, Color.YELLOW, "Мечеть.aac"));
        cachedMarkers.add( new Marker("top"                         ,59.966525,30.342503,0, Color.YELLOW, "Летний сад.aac"));
        cachedMarkers.add( new Marker("Биржа ростральные колонны"   ,59.944167,30.306803,0, Color.YELLOW, "Биржа ростральные колонны.aac"	));
        cachedMarkers.add( new Marker("Летний сад"                  ,59.945824,30.334997,0, Color.YELLOW, "Летний сад.aac"	));
        cachedMarkers.add( new Marker("Летний дворец Петра Первого" ,59.947342,30.336155,0, Color.YELLOW, "Дворец Петра Первого.aac"	));
        cachedMarkers.add( new Marker("Большой эрмитаж"             ,59.942310,30.316080,0, Color.YELLOW, "О Петербурге.aac"	));
        cachedMarkers.add( new Marker("Васильевский остров"         ,59.942639,30.277369,0, Color.YELLOW, "Васильевский остров.aac"	));
        cachedMarkers.add( new Marker("Здание ФСБ"                  ,59.948652,30.349252,0, Color.YELLOW, "Здание ФСБ.aac"	));
        cachedMarkers.add( new Marker("Зимний дворец"               ,59.940105,30.314669,0, Color.YELLOW, "Зимний дворец.aac"	));
        cachedMarkers.add( new Marker("Исаакиевский собор"          ,59.934198,30.305786,0, Color.YELLOW, "Исаакий.aac"	));
        cachedMarkers.add( new Marker("Аврора"                      ,59.955437,30.337891,0, Color.YELLOW, "Крейсер Аврора.aac"	));
        cachedMarkers.add( new Marker("Кунсткамера"                 ,59.941577,30.304666,0, Color.YELLOW, "Кунсткамера.aac"	));
        cachedMarkers.add( new Marker("Летний сад"                  ,59.945825,30.335071,0, Color.YELLOW, "Летний сад.aac"	));
        cachedMarkers.add( new Marker("Литейный мост"               ,59.951829,30.349310,0, Color.YELLOW, "Литейный мост.aac"	));
        cachedMarkers.add( new Marker("Малый Эрмитаж"               ,59.941315,30.315638,0, Color.YELLOW, "Малый Эрмитаж.aac"	));
        cachedMarkers.add( new Marker("Мечеть"                      ,59.955098,30.323902,0, Color.YELLOW, "Мечеть.aac"	));
        cachedMarkers.add( new Marker("Набережная Кутузова"         ,59.948779,30.340559,0, Color.YELLOW, "Набережная Кутузова.aac"	));
        cachedMarkers.add( new Marker("Нахимовское училище"         ,59.955521,30.336317,0, Color.YELLOW, "Нахимовское училище.aac"	));
        cachedMarkers.add( new Marker("Петропавловская крепость"    ,59.951448,30.315941,0, Color.YELLOW, "Петропавловская крепость.aac"	));
        cachedMarkers.add( new Marker("Река Нева"                   ,59.952536,30.341417,0, Color.YELLOW, "Река Нева.aac"	));
        cachedMarkers.add( new Marker("Спас на Крови"               ,59.940250,30.328807,0, Color.YELLOW, "Спас на Крови.aac"	));
        cachedMarkers.add( new Marker("Троицкий мост"               ,59.948950,30.327464,0, Color.YELLOW, "Троицкий мост.aac"	));
        cachedMarkers.add( new Marker("Эрмитажный театр"            ,59.942686,30.317877,0, Color.YELLOW, "Эрмитажный театр.aac"  ));

        return cachedMarkers;
    }
}