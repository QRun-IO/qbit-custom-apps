/*
 * QQQ - Low-code Application Framework for Engineers.
 * Copyright (C) 2021-2025.  Kingsrook, LLC
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

package com.kingsrook.qbits.customapps.utils;


import com.kingsrook.qbits.customapps.model.CustomApp;
import com.kingsrook.qbits.customapps.model.CustomAppContainer;
import com.kingsrook.qqq.backend.core.utils.StringUtils;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppsUtils
{
   /***************************************************************************
    **
    ***************************************************************************/
   public static String getAppName(String appName, String tableName)
   {
      appName = nameToCamelCase(appName);
      switch(tableName)
      {
         case CustomApp.TABLE_NAME -> appName = "qca" + StringUtils.ucFirst(appName);
         case CustomAppContainer.TABLE_NAME -> appName = "qcac" + StringUtils.ucFirst(appName);
         default ->
         {
            ////////////////////////////////////////////////////////////
            // dont add a prefix in this case and lowercase the start //
            ////////////////////////////////////////////////////////////
            appName = StringUtils.lcFirst(appName);
         }
      }

      return (appName);
   }



   /*******************************************************************************
    ** transform an app name into a camelCase name
    *******************************************************************************/
   private static String nameToCamelCase(String label)
   {
      String[] parts = label
         .replaceAll("[^a-zA-Z0-9]+", " ")
         .trim()
         .split("\\s+");

      StringBuilder result = new StringBuilder(parts[0].toLowerCase());

      for(int i = 1; i < parts.length; i++)
      {
         result.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1).toLowerCase());
      }

      return result.toString();
   }
}
