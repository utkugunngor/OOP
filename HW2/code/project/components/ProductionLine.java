package project.components;

import project.parts.Part;
import project.utility.Drawable;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class ProductionLine implements Drawable
{

    public int        maxCapacity ;
    public List<Part> parts       ;

    public ProductionLine ( int maxCapacity )
    {
        this.maxCapacity = maxCapacity       ;
        this.parts       = new ArrayList<>() ;
    }

    @Override public void draw ( Graphics2D g2d )
    {
        AffineTransform tOriginal = g2d.getTransform() ;
        g2d.setColor( Color.LIGHT_GRAY ) ;
        for ( int i = 200 ; i < maxCapacity * 200 ; i += 200 )  { g2d.drawLine( i , 0 , i , 200 ) ; }
        synchronized ( parts )
        {
            for ( Part p : parts )  { p.draw( g2d ) ;  g2d.translate( 200 , 0 ) ; }
        }
        g2d.setTransform( tOriginal ) ;
    }
}