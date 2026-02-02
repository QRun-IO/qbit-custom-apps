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


import java.util.List;
import java.util.Optional;
import com.kingsrook.qqq.api.model.metadata.ApiInstanceMetaData;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.producers.MetaDataCustomizerInterface;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitConfig;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.tables.QTableMetaData;
import com.kingsrook.qqq.backend.core.utils.ClassPathUtils;


/*******************************************************************************
 ** Configuration data for this qbit.
 **
 *******************************************************************************/
public class CustomAppsQBitConfig implements QBitConfig
{
   private MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer;

   private static boolean apiMiddlewareModuleAvailable;

   private boolean includeApiVersions = false;
   private String  componentSourceUrl;



   /*******************************************************************************
    ** Constructor
    **
    *******************************************************************************/
   public CustomAppsQBitConfig()
   {
      apiMiddlewareModuleAvailable = ClassPathUtils.isClassAvailable(ApiInstanceMetaData.class.getName());
   }



   /***************************************************************************
    *
    ***************************************************************************/
   public static boolean isApiModuleAvailableAndDoesQBitIncludeApiVersions()
   {
      Optional<QBitMetaData> optionalQBitConfig = QContext.getQInstance().getQBits().values().stream().filter(qb -> qb.getConfig() instanceof CustomAppsQBitConfig).findFirst();
      return (CustomAppsQBitConfig.getApiMiddlewareModuleAvailable() && optionalQBitConfig.isPresent() && ((CustomAppsQBitConfig) (optionalQBitConfig.get().getConfig())).getIncludeApiVersions());
   }



   /***************************************************************************
    **
    ***************************************************************************/
   @Override
   public void validate(QInstance qInstance, List<String> errors)
   {
      if(includeApiVersions && !apiMiddlewareModuleAvailable)
      {
         errors.add("Custom Apps QBit is configured to includeApiVersions, but the qqq-middleware-api module is available.");
      }
   }



   /*******************************************************************************
    ** Getter for tableMetaDataCustomizer
    *******************************************************************************/
   public MetaDataCustomizerInterface<QTableMetaData> getTableMetaDataCustomizer()
   {
      return (this.tableMetaDataCustomizer);
   }



   /*******************************************************************************
    ** Setter for tableMetaDataCustomizer
    *******************************************************************************/
   public void setTableMetaDataCustomizer(MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer)
   {
      this.tableMetaDataCustomizer = tableMetaDataCustomizer;
   }



   /*******************************************************************************
    ** Fluent setter for tableMetaDataCustomizer
    *******************************************************************************/
   public CustomAppsQBitConfig withTableMetaDataCustomizer(MetaDataCustomizerInterface<QTableMetaData> tableMetaDataCustomizer)
   {
      this.tableMetaDataCustomizer = tableMetaDataCustomizer;
      return (this);
   }



   /*******************************************************************************
    ** Getter for includeApiVersions
    *******************************************************************************/
   public boolean getIncludeApiVersions()
   {
      return (this.includeApiVersions);
   }



   /*******************************************************************************
    ** Setter for includeApiVersions
    *******************************************************************************/
   public void setIncludeApiVersions(boolean includeApiVersions)
   {
      this.includeApiVersions = includeApiVersions;
   }



   /*******************************************************************************
    ** Fluent setter for includeApiVersions
    *******************************************************************************/
   public CustomAppsQBitConfig withIncludeApiVersions(boolean includeApiVersions)
   {
      this.includeApiVersions = includeApiVersions;
      return (this);
   }



   /*******************************************************************************
    ** Getter for apiMiddlewareModuleAvailable
    **
    *******************************************************************************/
   public static boolean getApiMiddlewareModuleAvailable()
   {
      return apiMiddlewareModuleAvailable;
   }



   /*******************************************************************************
    ** Getter for componentSourceUrl
    *******************************************************************************/
   public String getComponentSourceUrl()
   {
      return (this.componentSourceUrl);
   }



   /*******************************************************************************
    ** Setter for componentSourceUrl
    *******************************************************************************/
   public void setComponentSourceUrl(String componentSourceUrl)
   {
      this.componentSourceUrl = componentSourceUrl;
   }



   /*******************************************************************************
    ** Fluent setter for componentSourceUrl
    *******************************************************************************/
   public CustomAppsQBitConfig withComponentSourceUrl(String componentSourceUrl)
   {
      this.componentSourceUrl = componentSourceUrl;
      return (this);
   }

}
