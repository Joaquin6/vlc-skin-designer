/*****************************************************************************
 * Bezier.java
 *****************************************************************************
 * Copyright (C) 2007 Daniel Dreibrodt
 *
 * This file is part of vlcskineditor
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package vlcskineditor;

import java.awt.geom.Point2D;
import java.util.*;

/**
 * Helper class for drawing beziers in sliders and anchors.
 * Conversion of VLC /trunk/modules/gui/skins2/utils/bezier.cpp <br>
 * original authors: Cyril Deguet     <asmax@via.ecp.fr> and
 *                   Olivier Teuli&egrave;re <ipkiss@via.ecp.fr>
 * @author Daniel Dreibrodt
 */
public class Bezier {

  /** The maximal number of points in a bezier curve */
  public final int MAX_BEZIER_POINT = 1023;

  /** Constant value indicating that both coordinates have to be checked in order to distinguish two points */
  public static final int kCoordsBoth = 0;
  /** Constant value indicating that only the x coordinate has to be checked in order to distinguish two points */
  public static final int kCoordsX = 1;
  /** Constant value indicating that only the y coordinate has to be checked in order to distinguish two points */
  public static final int kCoordsY = 2;
  /** number of control points */
  int m_nbCtrlPt;
  /** list containing the x coordinates of the control points */
  List<Float> m_ptx = new LinkedList<Float>();
  /** list containing the y coordinates of the control points */
  List<Float> m_pty = new LinkedList<Float>();
  /** Vector containing precalculated factoriels */
  List<Float> m_ft = new LinkedList<Float>();
  
  /** Number of points (=pixels) used by the curve */
  int m_nbPoints;
  /** Vectors with the coordinates of the different points of the curve */
  List<Integer> m_leftVect = new LinkedList<Integer>();
  List<Integer> m_topVect = new LinkedList<Integer>();
  /** Vector with the percentages associated with the points of the curve */
  List<Float> m_percVect = new LinkedList<Float>();
  
  /**
   * Creates a new instance of Bezier
   * @param x_ The x coordinates of the bezier curve points
   * @param y_ The y coordinates of the bezier curve points
   * @param flag Indicates whether x, y or both coordinates of a point have to be considered when distinguishing them
   */
  public Bezier(int[] x_,int[] y_, int flag) {
    // Copy the control points coordinates
    for (int i=0;i<x_.length;i++) {
      m_ptx.add((float)(x_[i]));
      m_pty.add((float)(y_[i]));
    }
    
    // We expect m_ptx and m_pty to have the same size, of course
    m_nbCtrlPt = m_ptx.size();
    
    // Precalculate the factoriels
    m_ft.add( 1f );
    for( int i = 1; i < m_nbCtrlPt; i++ )
    {
      m_ft.add((float)(i * m_ft.get(i - 1)));
    }
    
    // Calculate the first point    
    Point2D.Float old = computePoint( 0 );
    m_leftVect.add( (int)old.getX() );
    m_topVect.add( (int)old.getY() );
    m_percVect.add( 0f );
    
    // Calculate the other points
    float percentage;    
    for( float j = 1; j <= MAX_BEZIER_POINT; j++ )
    {
        percentage = j / MAX_BEZIER_POINT;        
        Point2D.Float c = computePoint( percentage );
        if( ( flag == kCoordsBoth && ( c.getX() != old.getX() || c.getY() != old.getY() ) ) ||
            ( flag == kCoordsX && c.getX() != old.getX() ) ||
            ( flag == kCoordsY && c.getY() != old.getY() ) )
        {
            m_percVect.add( percentage );
            m_leftVect.add( (int)c.getX() );
            m_topVect.add( (int)c.getY() );
            old.setLocation(c.getX(),c.getY());
        }
    }
    m_nbPoints = m_leftVect.size();
    
    // If we have only one control point, we duplicate it
    // This allows to simplify the algorithms used in the class
    if( m_nbPoints == 1 )
    {
        m_leftVect.add( m_leftVect.get(0) );
        m_topVect.add( m_topVect.get(0) );
        m_percVect.add( 1f );
        m_nbPoints = 2;
    }
    // Ensure that the percentage of the last point is always 1
    m_percVect.set(m_nbPoints - 1, 1f);
  }
  
  /**
   * Get the number of control points used to define the curve
   * @return The number of control points
   */
  public final int getNbCtrlPoints() { 
    return m_nbCtrlPt;
  }
  
  /**
   * Return the percentage of the curve point nearest to a given point
   * @param x The x coordinate of the point to be checked
   * @param y The y coordinate of the point to be checked
   * @return The percentage value [0-1] on the curve corresponding most closely to the given point
   */
  public final float getNearestPercent( int x, int y ) {
    int nearest = findNearestPoint( x, y );
    return m_percVect.get(nearest);
  }
  
  /**
   * Return the distance of a point to the curve, corrected
   * by the (optional) given scale factors
   * @param x The x coordinate of the point
   * @param y The y coordinate of the point
   * @param xScale The x scale factor
   * @param yScale The y scale factor
   * @return The distance from the point to the curve multiplied by the scale factors
   */
  public final float getMinDist( int x, int y, float xScale , float yScale ) {
    int nearest = findNearestPoint( x, y );
    double xDist = xScale * (m_leftVect.get(nearest) - x);
    double yDist = yScale * (m_topVect.get(nearest) - y);    
    return (float)Math.sqrt( xDist * xDist + yDist * yDist );
  }
  
  /**
   * Get the coordinates of the point at t percent of
   * the curve (t must be between 0 and 1)
   * @param t The percentage value
   * @return The point at percentage t of the curve
   */
  public final Point2D.Float getPoint( float t )  {
    // Find the precalculated point whose percentage is nearest from t
    int refPoint = 0; 
    float minDiff = Math.abs( m_percVect.get(0) - t );

    // The percentages are stored in increasing order, so we can stop the loop
    // as soon as 'diff' starts increasing
    float diff;
    while( refPoint < m_nbPoints &&
           (diff = Math.abs( m_percVect.get(refPoint) - t )) <= minDiff )
    {
        refPoint++;
        minDiff = diff;
    }

    // The searched point is then (refPoint - 1)
    // We know that refPoint > 0 because we looped at least once
    return new Point2D.Float (m_leftVect.get(refPoint - 1), m_topVect.get(refPoint - 1));
  }
  
  /**
   * Get the width (maximum abscissa) of the curve
   * @return The curve's width
   */
  public final int getWidth()  {
    int width = 0;
    for( int i = 0; i < m_nbPoints; i++ )
    {
        if( m_leftVect.get(i) >= width )
        {
            width = m_leftVect.get(i) + 1;
        }
    }
    return width;
  }
  
  /**
   * Get the height (maximum ordinate) of the curve
   * @return The curve's height
   */
  public final int getHeight() {
    int height = 0;
    for( int i = 0; i < m_nbPoints; i++ )
    {
        if( m_topVect.get(i) >= height )
        {
            height = m_topVect.get(i) + 1;
        }
    }
    return height;
  }
  
  /**
   * Finds the nearest reference point of the curve to a given point
   * @param x The x coordinate of the point
   * @param y The y coordinate of the point
   * @return the index of the curve point that is the nearest to the given point
   */
  private int findNearestPoint( int x, int y ) {
    // The distance to the first point is taken as the reference
    int refPoint = 0;
    int minDist = (m_leftVect.get(0) - x) * (m_leftVect.get(0) - x) +
                  (m_topVect.get(0) - y) * (m_topVect.get(0) - y);

    int dist;
    for( int i = 1; i < m_nbPoints; i++ )
    {
        dist = (m_leftVect.get(i) - x) * (m_leftVect.get(i) - x) +
               (m_topVect.get(i) - y) * (m_topVect.get(i) - y);
        if( dist < minDist )
        {
            minDist = dist;
            refPoint = i;
        }
    }

    return refPoint;
  }
  
  /** 
   * Compute the coordinates of a point corresponding to a given
   * percentage
   * @param t The percentage
   * @return The corresponding point
   */
  private Point2D.Float computePoint( float t ) {
    // See http://astronomy.swin.edu.au/~pbourke/curves/bezier/ for a simple
    // explanation of the algorithm
    float xPos = 0;
    float yPos = 0;
    float coeff;
    for( int i = 0; i < m_nbCtrlPt; i++ )
    {
        coeff = computeCoeff( i, m_nbCtrlPt - 1, t );
        xPos += m_ptx.get(i) * coeff;
        yPos += m_pty.get(i) * coeff;
    }

    return new Point2D.Float(xPos,yPos);    
  }
  
  /** 
   * Helper function to compute a coefficient of the curve
   */
  private float computeCoeff( int i, int n, float t ) {
    return (power( t, i ) * power( 1 - t, (n - i) ) *
        (m_ft.get(n) / m_ft.get(i) / m_ft.get(n - i)));
  }
  
  /** 
   * x^n
   * @param x The basis
   * @param n The exponent
   * @see java.lang.Math.pow()
   */
  private float power( float x, int n ) {
    return (float)Math.pow( x, n );
  } 
 
}
