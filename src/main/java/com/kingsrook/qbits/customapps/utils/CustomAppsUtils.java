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


import java.util.List;
import java.util.Optional;
import com.kingsrook.qbits.customapps.model.CustomApp;
import com.kingsrook.qbits.customapps.model.CustomAppContainer;
import com.kingsrook.qbits.customapps.model.CustomAppIcon;
import com.kingsrook.qbits.customapps.model.CustomAppSection;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterOrderBy;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType;
import com.kingsrook.qqq.backend.core.model.metadata.fields.FieldAdornment;
import com.kingsrook.qqq.backend.core.model.metadata.frontend.QFrontendFieldMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.frontend.QFrontendTableMetaData;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import static com.kingsrook.qqq.backend.core.model.metadata.fields.AdornmentType.ChipValues.iconAndColorValues;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppsUtils
{
   /***************************************************************************
    **
    ***************************************************************************/
   public static void addDynamicIconChipAdornments(QFrontendTableMetaData table, String fieldName) throws QException
   {
      QFrontendFieldMetaData   field            = table.getFields().get(fieldName);
      Optional<FieldAdornment> chipAdornmentOpt = field.getAdornments().stream().filter(a -> a.getType().equals(AdornmentType.CHIP)).findFirst();
      if(chipAdornmentOpt.isPresent())
      {
         ////////////////////////////////////////////////////////////
         // look up all icons in the system and add chips for them //
         ////////////////////////////////////////////////////////////
         List<CustomAppIcon> customAppIcons = QueryAction.execute(CustomAppIcon.TABLE_NAME, CustomAppIcon.class, new QQueryFilter().withOrderBy(new QFilterOrderBy("name")));
         for(CustomAppIcon icon : customAppIcons)
         {
            chipAdornmentOpt.get().withValues(iconAndColorValues(icon.getId(), icon.getIconId(), AdornmentType.ChipValues.COLOR_SECONDARY));
         }
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   public static String getCamelCaseName(String name, String tableName)
   {
      name = nameToCamelCase(name);
      switch(tableName)
      {
         case CustomApp.TABLE_NAME -> name = "qca" + StringUtils.ucFirst(name);
         case CustomAppSection.TABLE_NAME -> name = "qcas" + StringUtils.ucFirst(name);
         case CustomAppContainer.TABLE_NAME -> name = "qcac" + StringUtils.ucFirst(name);
         default ->
         {
            ////////////////////////////////////////////////////////////
            // dont add a prefix in this case and lowercase the start //
            ////////////////////////////////////////////////////////////
            name = StringUtils.lcFirst(name);
         }
      }

      return (name);
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
