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

package com.kingsrook.qbits.customapps;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.kingsrook.qbits.customapps.definition.CustomAppsRegistry;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerMultiOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaDataProducer;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppsQBitProducer implements QBitMetaDataProducer<CustomAppsQBitConfig>
{
   private static final QLogger LOG = QLogger.getLogger(CustomAppsQBitProducer.class);

   private CustomAppsQBitConfig customAppsQBitConfig;



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public QBitMetaData getQBitMetaData()
   {
      QBitMetaData qBitMetaData = new QBitMetaData()
         .withNamespace(getNamespace())
         .withConfig(getQBitConfig());

      /////////////////////////////////////////////////////////////////////////
      // look up details from properties file (which gets data from pom.xml) //
      /////////////////////////////////////////////////////////////////////////
      Properties properties = new Properties();
      try(InputStream in = getClass().getResourceAsStream("/qbit.properties"))
      {
         properties.load(in);
         qBitMetaData
            .withGroupId(properties.getProperty("qbit.groupId"))
            .withArtifactId(properties.getProperty("qbit.artifactId"))
            .withVersion(properties.getProperty("qbit.version"));
      }
      catch(IOException ioe)
      {
         LOG.error("Error reading qbit.properties", ioe);
      }

      return (qBitMetaData);
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void postProduceActions(MetaDataProducerMultiOutput metaDataProducerMultiOutput, QInstance qInstance) throws QException
   {
      CustomAppsRegistry customAppsRegistry = metaDataProducerMultiOutput.get(CustomAppsRegistry.class, CustomAppsRegistry.NAME);
      qInstance.add(customAppsRegistry);
   }



   /*******************************************************************************
    ** Getter for qBitConfig
    *******************************************************************************/
   @Override
   public CustomAppsQBitConfig getQBitConfig()
   {
      return (this.customAppsQBitConfig);
   }



   /*******************************************************************************
    ** Setter for qBitConfig
    *******************************************************************************/
   public void setQBitConfig(CustomAppsQBitConfig customAppsQBitConfig)
   {
      this.customAppsQBitConfig = customAppsQBitConfig;
   }



   /*******************************************************************************
    ** Fluent setter for qBitConfig
    *******************************************************************************/
   public CustomAppsQBitProducer withQBitConfig(CustomAppsQBitConfig customAppsQBitConfig)
   {
      this.customAppsQBitConfig = customAppsQBitConfig;
      return (this);
   }

}
