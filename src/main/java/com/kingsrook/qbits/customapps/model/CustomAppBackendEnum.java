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

package com.kingsrook.qbits.customapps.model;


import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.PossibleValueEnum;
import com.kingsrook.qqq.backend.core.model.metadata.producers.annotations.QMetaDataProducingPossibleValueEnum;


/*******************************************************************************
 **
 *******************************************************************************/
@QMetaDataProducingPossibleValueEnum
public enum CustomAppBackendEnum implements PossibleValueEnum<Integer>
{
   LOOKER(1, "Looker"),
   URL(2, "URL");

   private final Integer id;
   private final String  label;



   /*******************************************************************************
    **
    *******************************************************************************/
   CustomAppBackendEnum(Integer id, String label)
   {
      this.id = id;
      this.label = label;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   public static CustomAppBackendEnum getById(Integer id)
   {
      if(id == null)
      {
         return (null);
      }

      for(CustomAppBackendEnum value : CustomAppBackendEnum.values())
      {
         if(value.getId().equals(id))
         {
            return (value);
         }
      }

      return null;
   }



   /*******************************************************************************
    ** Getter for id
    **
    *******************************************************************************/
   public Integer getId()
   {
      return id;
   }



   /*******************************************************************************
    ** Getter for label
    **
    *******************************************************************************/
   public String getLabel()
   {
      return label;
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public Integer getPossibleValueId()
   {
      return getId();
   }



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public String getPossibleValueLabel()
   {
      return getLabel();
   }
}
