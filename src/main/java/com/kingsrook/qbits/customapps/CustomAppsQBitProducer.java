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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.kingsrook.qbits.customapps.definition.CustomAppsRegistry;
import com.kingsrook.qbits.customapps.model.CustomApp;
import com.kingsrook.qbits.customapps.model.CustomAppBackendConfig;
import com.kingsrook.qbits.customapps.model.CustomAppBackendEnum;
import com.kingsrook.qbits.customapps.model.CustomAppContainer;
import com.kingsrook.qbits.customapps.model.CustomAppIcon;
import com.kingsrook.qbits.customapps.model.CustomAppSection;
import com.kingsrook.qbits.customapps.utils.CustomAppsUtils;
import com.kingsrook.qbits.customapps.widgets.LookerWidgetRenderer;
import com.kingsrook.qqq.backend.core.actions.tables.QueryAction;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.instances.QMetaDataVariableInterpreter;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterOrderBy;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.WidgetType;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducerMultiOutput;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaDataInterface;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppSection;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.DenyBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.PermissionLevel;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.qbits.QBitMetaDataProducer;
import com.kingsrook.qqq.backend.core.utils.ListingHash;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qqq.backend.core.utils.collections.MapBuilder;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


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



   /***************************************************************************
    *
    ***************************************************************************/
   public CustomAppData getCustomAppList(QInstance qInstance) throws QException
   {
      ///////////////////////////////////////////////////
      // look up data needed to set up the custom apps //
      ///////////////////////////////////////////////////
      List<CustomAppContainer>             customAppContainerList     = QueryAction.execute(CustomAppContainer.TABLE_NAME, CustomAppContainer.class, new QQueryFilter().withOrderBy(new QFilterOrderBy("sequenceNo")));
      List<CustomAppSection>               customAppSectionList       = QueryAction.execute(CustomAppSection.TABLE_NAME, CustomAppSection.class, new QQueryFilter().withOrderBy(new QFilterOrderBy("sequenceNo")));
      List<CustomApp>                      customAppList              = QueryAction.execute(CustomApp.TABLE_NAME, CustomApp.class, new QQueryFilter().withOrderBy(new QFilterOrderBy("sequenceNo")));
      List<CustomAppIcon>                  customAppIcons             = QueryAction.execute(CustomAppIcon.TABLE_NAME, CustomAppIcon.class, new QQueryFilter());
      List<CustomAppBackendConfig>         customAppBackendConfigList = QueryAction.execute(CustomAppBackendConfig.TABLE_NAME, CustomAppBackendConfig.class, new QQueryFilter());
      Map<Integer, CustomAppBackendConfig> customAppBackendConfigMap  = customAppBackendConfigList.stream().collect(Collectors.toMap(CustomAppBackendConfig::getId, Function.identity()));

      /////////////////////
      // icon id to icon //
      /////////////////////
      Map<Integer, CustomAppIcon> customAppIconMap = customAppIcons.stream().collect(Collectors.toMap(CustomAppIcon::getId, Function.identity()));

      //////////////////////////////
      // container id to sections //
      //////////////////////////////
      ListingHash<Integer, CustomAppSection> sectionsMap = new ListingHash<>();
      customAppSectionList.forEach(section -> sectionsMap.add(section.getCustomAppContainerId(), section));

      ////////////////////////
      // section id to apps //
      ////////////////////////
      ListingHash<Integer, CustomApp> appsMap = new ListingHash<>();
      customAppList.forEach(app -> appsMap.add(app.getCustomAppSectionId(), app));

      ///////////////////////////////////
      // iterate over containers first //
      ///////////////////////////////////
      List<QAppMetaData>                    containers = new ArrayList<>();
      Map<String, QAppMetaData>             appMap     = new HashMap<>();
      Map<String, QWidgetMetaDataInterface> widgetMap  = new HashMap<>();
      for(CustomAppContainer container : customAppContainerList)
      {
         //////////////////////////
         // if no sections, skip //
         //////////////////////////
         if(sectionsMap.get(container.getId()) == null)
         {
            continue;
         }

         ////////////////////////////////////////
         // iterate over sections for this app //
         ////////////////////////////////////////
         List<QAppSection> sections = new ArrayList<>();
         for(CustomAppSection section : sectionsMap.get(container.getId()))
         {
            //////////////////////
            // if no apps, skip //
            //////////////////////
            if(appsMap.get(section.getId()) == null)
            {
               continue;
            }

            ///////////////////////
            // iterate over apps //
            ///////////////////////
            int          appCount    = 0;
            List<String> appNameList = new ArrayList<>();
            if(appsMap.containsKey(section.getId()))
            {
               for(CustomApp app : appsMap.get(section.getId()))
               {
                  //////////////////////////////////////////////////
                  // turn the label into a valid name for the app //
                  //////////////////////////////////////////////////
                  String appName = CustomAppsUtils.getAppName(app.getName(), CustomApp.TABLE_NAME);

                  /////////////////////////////////////////////////////
                  // make sure user has permission to this app //
                  ////////////////////////////////////////////////////
                  if(!QContext.getQSession().hasPermission(appName + ".hasAccess"))
                  {
                     continue;
                  }

                  /////////////////////////////////////////
                  // create a custom widget for this app //
                  /////////////////////////////////////////
                  String                   widgetName = appName + "Widget";
                  QWidgetMetaDataInterface widget     = buildWidget(qInstance, app, customAppBackendConfigMap, widgetName);
                  CustomAppIcon            appIcon    = customAppIconMap.get(app.getCustomAppIconId());

                  QAppMetaData appMetaData = new QAppMetaData()
                     .withPermissionRules(qInstance.getDefaultPermissionRules().withLevel(PermissionLevel.HAS_ACCESS_PERMISSION).withPermissionBaseName(appName).withDenyBehavior(DenyBehavior.HIDDEN))
                     .withIcon(new QIcon(appIcon.getIconId()))
                     .withName(appName)
                     .withLabel(app.getName())
                     .withWidgets(List.of(widget.getName()));
                  appNameList.add(appMetaData.getName());
                  appCount++;

                  ////////////////////////
                  // add to output data //
                  ////////////////////////
                  widgetMap.put(widgetName, widget);
                  appMap.put(appMetaData.getName(), appMetaData);
               }
            }

            ///////////////////////////////////////////////////////
            // if no apps for this section, continue to next one //
            ///////////////////////////////////////////////////////
            if(appCount == 0)
            {
               continue;
            }

            /////////////////////////////////////////////////
            // build section with its apps and add to list //
            /////////////////////////////////////////////////
            String sectionName = nameToCamelCase(section.getName());
            sectionName = "qcas" + StringUtils.ucFirst(sectionName);

            QAppSection appSection = new QAppSection()
               .withApps(appNameList)
               .withName(sectionName)
               .withLabel(section.getName());
            sections.add(appSection);
         }

         /////////////////////////
         // build the container //
         /////////////////////////
         String containerName = CustomAppsUtils.getAppName(container.getName(), CustomAppContainer.TABLE_NAME);

         //////////////////////////
         // if no sections, skip //
         //////////////////////////
         if(sections.isEmpty())
         {
            continue;
         }

         /////////////////////////////////////////////////////
         // make sure user has permission to this container //
         /////////////////////////////////////////////////////
         if(!QContext.getQSession().hasPermission(containerName + ".hasAccess"))
         {
            continue;
         }

         CustomAppIcon containerIcon = customAppIconMap.get(container.getCustomAppIconId());
         QAppMetaData containerApp = new QAppMetaData()
            .withPermissionRules(qInstance.getDefaultPermissionRules().withLevel(PermissionLevel.HAS_ACCESS_PERMISSION).withPermissionBaseName(containerName).withDenyBehavior(DenyBehavior.HIDDEN))
            .withName(containerName)
            .withLabel(container.getName())
            .withIcon(new QIcon().withName(containerIcon.getIconId()))
            .withSections(sections);
         containers.add(containerApp);
      }

      return (new CustomAppData(containers, appMap, widgetMap));
   }



   /*******************************************************************************
    ** builds a widget to be used for a customapps app
    *******************************************************************************/
   private QWidgetMetaDataInterface buildWidget(QInstance qInstance, CustomApp customApp, Map<Integer, CustomAppBackendConfig> customAppBackendConfigMap, String widgetName)
   {
      CustomAppBackendConfig customAppBackendConfig = customAppBackendConfigMap.get(customApp.getCustomAppBackendConfigId());
      switch(CustomAppBackendEnum.getById(customAppBackendConfig.getCustomAppBackendId()))
      {
         case LOOKER:
            return (buildLookerWidget(qInstance, customApp, widgetName));
         default:
            throw new IllegalArgumentException("Unknown custom app backend: " + customAppBackendConfig.getCustomAppBackendId());
      }

   }



   /*******************************************************************************
    ** builds a widget to be used for a customapps app
    *******************************************************************************/
   private QWidgetMetaDataInterface buildLookerWidget(QInstance qInstance, CustomApp customApp, String widgetName)
   {
      String componentSourceUrl = new QMetaDataVariableInterpreter().interpret("${env.Q_CUSTOM_APPS_SOURCE_URL}");
      if(StringUtils.hasContent(componentSourceUrl))
      {
         LOG.debug("Using component source url from environment", logPair("url", componentSourceUrl));
      }
      else
      {
         componentSourceUrl = "/dynamic-qfmd-components/q-custom-apps.bundle.js";
      }

      //////////////////////////////
      // add the dashboard to url //
      /////////////////////////
      Integer dashboardId = customApp.getLookerDashboardId();
      componentSourceUrl += "?dashboardId=" + dashboardId;

      Map<String, Serializable> defaultValues = MapBuilder.of(
         "componentName", "CustomApp",
         "componentSourceUrl", componentSourceUrl,
         "dashboardId", dashboardId,
         "sx", new HashMap<>(Map.of("height", "calc(90vh)", "minHeight", "600px"))
      );

      QWidgetMetaData lookerWidget = new QWidgetMetaData()
         .withName(widgetName)
         .withType(WidgetType.CUSTOM_COMPONENT.getType())
         .withCodeReference(new QCodeReference(LookerWidgetRenderer.class))
         .withGridColumns(12)
         .withIsCard(false)
         .withLabel(null)
         .withDefaultValues(defaultValues);
      if(qInstance.getWidget(widgetName) == null)
      {
         qInstance.addWidget(lookerWidget);
      }

      return (lookerWidget);
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



   public record CustomAppData(List<QAppMetaData> containerList, Map<String, QAppMetaData> appMap, Map<String, QWidgetMetaDataInterface> widgetMap) {}
}
