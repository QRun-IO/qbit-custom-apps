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

package com.kingsrook.qbits.customapps.definition;


import java.util.Map;
import com.kingsrook.qqq.backend.core.instances.QHelpContentPlugin;
import com.kingsrook.qqq.backend.core.instances.QInstanceValidator;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.QSupplementalInstanceMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.help.QHelpContent;


/*******************************************************************************
 * Class that stores the available custom apps and sections
 * Implemented as QSupplementalInstanceMetaData, meaning, it should exist
 * as effectively a singleton within a QInstance.
 *******************************************************************************/
public class CustomAppsRegistry implements QSupplementalInstanceMetaData, QHelpContentPlugin
{
   private static final QLogger LOG = QLogger.getLogger(CustomAppsRegistry.class);

   public static String NAME = CustomAppsRegistry.class.getName();



   /*******************************************************************************
    *
    *******************************************************************************/
   public CustomAppsRegistry()
   {
   }



   /*******************************************************************************
    * get the instance of this object that is a member *of* the input QInstance
    *
    * @param qInstance the object containing the registry you are looking for
    * @return the custom apps registry in that qInstance, or null if there isn't one.
    *******************************************************************************/
   public static CustomAppsRegistry of(QInstance qInstance)
   {
      return QSupplementalInstanceMetaData.of(qInstance, NAME);
   }



   /*******************************************************************************
    * get the instance of this object that is a member *of* the input QInstance -
    * OR - if there isn't one, construct a new one and attach it to the instance.
    *
    * @param qInstance the object containing the registry you are looking for
    * @return the custom apps registry in that qInstance - OR - if there wasn't one,
    * a new one, which has been put in the instance.
    *******************************************************************************/
   public static CustomAppsRegistry ofOrWithNew(QInstance qInstance)
   {
      return QSupplementalInstanceMetaData.ofOrWithNew(qInstance, NAME, CustomAppsRegistry::new);
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void enrich(QInstance qInstance)
   {
      // todo?
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void validate(QInstance qInstance, QInstanceValidator qInstanceValidator)
   {
      // todo?
   }



   /***************************************************************************
    * as required by {@link QSupplementalInstanceMetaData}
    * @return the unique name for this object within its QInstance
    ***************************************************************************/
   @Override
   public String getName()
   {
      return (NAME);
   }



   /***************************************************************************
    * plug in to the QQQ Help Content system, to allow help content to set
    * descriptions and labels for customapps data
    *
    * Note:  helpContent can come in here as null, if it has been, e.g., deleted!
    ***************************************************************************/
   @Override
   public void acceptHelpContent(QInstance qInstance, QHelpContent helpContent, Map<String, String> nameValuePairs)
   {
      // not yet needed
   }

}
