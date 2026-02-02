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

package com.kingsrook.qbits.customapps.metadata;


import com.kingsrook.qbits.customapps.model.CustomApp;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinOn;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinType;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;


/*******************************************************************************
 ** Meta Data Producer for CustomAppJoinCustomApp
 *******************************************************************************/
public class CustomAppJoinCustomAppMetaDataProducer extends MetaDataProducer<QJoinMetaData>
{
   public static final String NAME = "CustomAppJoinCustomApp";



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QJoinMetaData produce(QInstance qInstance) throws QException
   {
      return new QJoinMetaData()
         .withName(NAME)
         .withLeftTable(CustomApp.TABLE_NAME)
         .withRightTable(CustomApp.TABLE_NAME)
         .withType(JoinType.ONE_TO_MANY)
         .withJoinOn(new JoinOn("customAppSectionId", "customAppSectionId"));
   }

}
