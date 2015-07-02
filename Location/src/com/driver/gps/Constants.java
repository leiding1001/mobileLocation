/*------------------------------------------------------------------------------
 **     Ident: Sogeti Smart Mobile Solutions
 **    Author: rene
 ** Copyright: (c) Apr 24, 2011 Sogeti Nederland B.V. All Rights Reserved.
 **------------------------------------------------------------------------------
 ** Sogeti Nederland B.V.            |  No part of this file may be reproduced  
 ** Distributed Software Engineering |  or transmitted in any form or by any        
 ** Lange Dreef 17                   |  means, electronic or mechanical, for the      
 ** 4131 NJ Vianen                   |  purpose, without the express written    
 ** The Netherlands                  |  permission of the copyright holder.
 *------------------------------------------------------------------------------
 *
 *   This file is part of OpenGPSTracker.
 *
 *   OpenGPSTracker is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenGPSTracker is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenGPSTracker.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.driver.gps;



/**
 * Various application wide constants
 * 
 * @version $Id$
 * @author rene (c) Mar 22, 2009, Sogeti B.V.
 */
public class Constants
{
   public static final String SERVICENAME = "com.driver.gps.intent.action.GPSLoggerService";
   
   /**
    * The state of the service is unknown
    */
   public static final int UNKNOWN = -1;
   
   /**
    * The service is actively logging, it has requested location update from the location provider.
    */
   public static final int LOGGING = 1;
   
   /**
    * The service is not active, but can be resumed to become active and store location changes as 
    * part of a new segment of the current track.
    */
   public static final int PAUSED = 2;
   
   /**
    * The service is not active and can not resume a current track but must start a new one when becoming active.
    */
   public static final int STOPPED = 3;
   
   /**
    * The precision of the GPS provider is based on the custom time interval and distance.
    */
   public static final int LOGGING_CUSTOM = 0;
   
   /**
    * The GPS location provider is asked to update every 10 seconds or every 5 meters.
    */
   public static final int LOGGING_FINE   = 1;
   
   /**
    * The GPS location provider is asked to update every 15 seconds or every 10 meters.
    */
   public static final int LOGGING_NORMAL = 2;
   
   /**
    * The GPS location provider is asked to update every 30 seconds or every 25 meters.
    */
   public static final int LOGGING_COARSE = 3;
   
   /**
    * The radio location provider is asked to update every 5 minutes or every 500 meters.
    */
   public static final int LOGGING_GLOBAL = 4;
}
