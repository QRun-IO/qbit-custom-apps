/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2024.  Kingsrook, LLC
 * 651 N Broad St Ste 205 # 6917 | Middletown DE 19709 | United States
 * contact@kingsrook.com
 * https://github.com/Kingsrook/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.kingsrook.qbits.customapps.customizers;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import com.kingsrook.qqq.backend.core.actions.values.QCustomPossibleValueProvider;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.values.SearchPossibleValueSourceInput;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValue;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppContainerPlaceBeforeContainerPossibleValueSource implements QCustomPossibleValueProvider<String>
{
   private static final QLogger LOG = QLogger.getLogger(CustomAppContainerPlaceBeforeContainerPossibleValueSource.class);

   public static final String NAME = "placeBeforeContainer";



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QPossibleValue<String> getPossibleValue(Serializable idValue)
   {
      try
      {
         for(QAppMetaData app : getNavApps())
         {
            if(app.getLabel().equals(idValue))
            {
               return appToPossibleValue(app);
            }
         }
      }
      catch(Exception e)
      {
         LOG.error("Unable to find app with label: {}", idValue);
      }

      return null;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public List<QPossibleValue<String>> search(SearchPossibleValueSourceInput input) throws QException
   {
      List<QPossibleValue<String>> rs = new ArrayList<>();
      for(QAppMetaData app : getNavApps())
      {
         rs.add(appToPossibleValue(app));
      }
      return rs;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   private static List<QAppMetaData> getNavApps() throws QException
   {
      //////////////////////////////////////////////////////////////////////////
      // determine which apps are in the nav bar based on not having a parent //
      //////////////////////////////////////////////////////////////////////////
      List<QAppMetaData>        navApps = new ArrayList<>();
      Map<String, QAppMetaData> apps    = QContext.getQInstance().getApps();
      for(String appName : apps.keySet())
      {
         QAppMetaData app = apps.get(appName);
         if(app.getParentAppName() == null)
         {
            navApps.add(app);
         }
      }

      //////////////////////////////////////////
      // sort the navApps by their sort order //
      //////////////////////////////////////////
      navApps.sort(Comparator.comparing(QAppMetaData::getSortOrder));
      return (navApps);
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   private static QPossibleValue<String> appToPossibleValue(QAppMetaData app)
   {
      return new QPossibleValue<>(app.getLabel(), app.getLabel());
   }

}
