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
package com.kingsrook.qbits.customapps.metadata;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.kingsrook.qbits.customapps.CustomAppsQBitConfig;
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
import com.kingsrook.qqq.backend.core.model.session.QSession;
import com.kingsrook.qqq.backend.core.model.session.QSystemUserSession;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.instances.QMetaDataVariableInterpreter;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QFilterOrderBy;
import com.kingsrook.qqq.backend.core.model.actions.tables.query.QQueryFilter;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.WidgetType;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaDataInterface;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppSection;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QIcon;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.DenyBehavior;
import com.kingsrook.qqq.backend.core.model.metadata.permissions.PermissionLevel;
import com.kingsrook.qqq.backend.core.utils.ListingHash;
import com.kingsrook.qqq.backend.core.utils.StringUtils;
import com.kingsrook.qqq.backend.core.utils.collections.MapBuilder;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


/*******************************************************************************
 * Meta Data Producer for ViewScreenGenericCompositeWidget
 *
 * builds up a record containing data used to build the custom apps layout
 *******************************************************************************/
public class CustomAppsDynamicMetaDataProducer
{
   private static final QLogger LOG = QLogger.getLogger(CustomAppsDynamicMetaDataProducer.class);

   public static final String NAME = "CustomAppsDynamicMetaDataProducer";



   /***************************************************************************
    *
    ***************************************************************************/
   public CustomAppData getCustomAppList(QInstance qInstance) throws QException
   {
      ////////////////////////////////////////////////////////////////////////////////
      // capture the viewer now: the build itself runs with the system session set //
      ////////////////////////////////////////////////////////////////////////////////
      QSession viewer = QContext.getQSession();
      return (getCustomAppList(qInstance, name -> viewer != null && viewer.hasPermission(name + ".hasAccess")));
   }



   /***************************************************************************
    ** Build the apps for whoever the predicate admits (by app/container name).
    ** The definition rows are read as the system user (they are configuration,
    ** not data of the viewer); what the viewer may see is decided only by the
    ** predicate, which the metadata customizer ties to the session's permissions.
    ***************************************************************************/
   public CustomAppData getCustomAppList(QInstance qInstance, java.util.function.Predicate<String> permitted) throws QException
   {
      QSession original = QContext.getQSession();
      try
      {
         QContext.setQSession(new QSystemUserSession());
         return (buildCustomAppList(qInstance, permitted, original));
      }
      finally
      {
         QContext.setQSession(original);
      }
   }



   /***************************************************************************
    ** The build proper, with the system session in place for reads.
    ***************************************************************************/
   private CustomAppData buildCustomAppList(QInstance qInstance, java.util.function.Predicate<String> permitted, QSession viewer) throws QException
   {
      ///////////////////////////////////////////////////
      // look up data needed to set up the custom apps //
      ///////////////////////////////////////////////////
      List<CustomAppContainer>             customAppContainerList     = QueryAction.execute(CustomAppContainer.TABLE_NAME, CustomAppContainer.class, new QQueryFilter().withOrderBy(new QFilterOrderBy("sequenceNo")).withOrderBy(new QFilterOrderBy("name")));
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
      List<QAppMetaData>                    containers  = new ArrayList<>();
      Map<String, QAppMetaData>             appMap      = new HashMap<>();
      Map<String, QWidgetMetaDataInterface> widgetMap   = new HashMap<>();
      Map<String, String>                   positionMap = new HashMap<>();
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
                  String appName = CustomAppsUtils.getCamelCaseName(app.getName(), CustomApp.TABLE_NAME);

                  /////////////////////////////////////////////////////
                  // make sure user has permission to this app //
                  ////////////////////////////////////////////////////
                  if(!permitted.test(appName))
                  {
                     continue;
                  }

                  /////////////////////////////////////////
                  // create a custom widget for this app //
                  /////////////////////////////////////////
                  String                   widgetName = appName + "Widget";
                  QWidgetMetaDataInterface widget;
                  try
                  {
                     widget = buildWidget(qInstance, app, customAppBackendConfigMap, widgetName);
                  }
                  catch(IllegalArgumentException e)
                  {
                     ///////////////////////////////////////////////////////////////////////
                     // one invalid row must not take the other apps (or startup) with it; //
                     // it is simply not offered                                           //
                     ///////////////////////////////////////////////////////////////////////
                     LOG.warn("Custom app skipped: its definition is not valid", logPair("app", app.getName()), logPair("reason", e.getMessage()));
                     continue;
                  }
                  CustomAppIcon appIcon = customAppIconMap.get(app.getCustomAppIconId());

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
            String sectionName = CustomAppsUtils.getCamelCaseName(section.getName(), CustomAppSection.TABLE_NAME);

            QAppSection appSection = new QAppSection()
               .withApps(appNameList)
               .withName(sectionName)
               .withLabel(section.getName());
            sections.add(appSection);
         }

         /////////////////////////
         // build the container //
         /////////////////////////
         String containerName = CustomAppsUtils.getCamelCaseName(container.getName(), CustomAppContainer.TABLE_NAME);

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
         if(!permitted.test(containerName))
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
         positionMap.put(containerName, container.getPlaceBeforeContainer());
      }

      return (new CustomAppData(containers, positionMap, appMap, widgetMap));
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
         case LOCAL_WIDGET:
            return (localWidget(qInstance, customApp));
         default:
            throw new IllegalArgumentException("Unknown custom app backend: " + customAppBackendConfig.getCustomAppBackendId());
      }

   }



   /*******************************************************************************
    ** a custom app that shows a widget the application itself defines (a record
    ** list, a chart, ...): the screen is composed from existing, permissioned
    ** metadata, so no code or markup comes from the data, and the widget keeps its
    ** own permission rules.  Only widgets that exist on the instance can be named,
    ** and never this qbit's own generated ones.
    *******************************************************************************/
   private QWidgetMetaDataInterface localWidget(QInstance qInstance, CustomApp customApp)
   {
      String name = customApp.getWidgetName();
      if(!StringUtils.hasContent(name))
      {
         throw new IllegalArgumentException("a local widget app needs a widget name");
      }
      QWidgetMetaDataInterface widget = qInstance.getWidget(name);
      if(widget == null)
      {
         throw new IllegalArgumentException("widget " + name + " is not defined on this application");
      }
      if(name.startsWith("qca"))
      {
         throw new IllegalArgumentException("widget " + name + " is generated by custom apps and cannot be chosen");
      }
      return (widget);
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
         componentSourceUrl = "/dynamic-qfmd-components/q-embed-looker-dashboards.bundle.js";
      }

      if(!isAllowedComponentSource(componentSourceUrl))
      {
         throw new IllegalArgumentException("the component source must be a path on this application (or an https address the configuration allows)");
      }

      //////////////////////////////
      // add the dashboard to url //
      /////////////////////////
      Integer dashboardId = customApp.getLookerDashboardId();
      if(dashboardId == null)
      {
         throw new IllegalArgumentException("a Looker app needs a dashboard id");
      }
      componentSourceUrl += "?dashboardId=" + dashboardId;

      Map<String, Serializable> defaultValues = MapBuilder.of(
         "componentName", "EmbedLookerDashboards",
         "componentSourceUrl", componentSourceUrl,
         "dashboardId", dashboardId,
         // "authenticationUrl", this.customAppsQBitConfig.getAuthenticationUrl(),
         "sx", new HashMap<>(Map.of("height", "calc(90vh)", "minHeight", "600px"))
      );

      QWidgetMetaData lookerWidget = new QWidgetMetaData()
         .withName(widgetName)
         .withPermissionRules(qInstance.getDefaultPermissionRules().withLevel(PermissionLevel.HAS_ACCESS_PERMISSION).withPermissionBaseName(customApp.getName() == null ? widgetName : CustomAppsUtils.getCamelCaseName(customApp.getName(), CustomApp.TABLE_NAME)).withDenyBehavior(DenyBehavior.HIDDEN))
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
    ** The component bundle is code the browser runs, so its source is never taken
    ** from data: a path served by this application, or an https address whose host
    ** the configuration lists.  Anything else (script or data URLs, protocol
    ** relative addresses, other hosts) is refused.
    *******************************************************************************/
   public boolean isAllowedComponentSource(String url)
   {
      if(url == null || url.chars().anyMatch(c -> c < 0x21 || c > 0x7e))
      {
         return (false);
      }
      if(url.startsWith("/") && !url.startsWith("//") && !url.contains("\\") && !url.contains(".."))
      {
         return (true);
      }
      try
      {
         java.net.URI uri = new java.net.URI(url);
         CustomAppsQBitConfig config = configFor(QContext.getQInstance());
         return ("https".equals(uri.getScheme()) && uri.getUserInfo() == null && uri.getHost() != null
            && config != null && config.getAllowedComponentHosts().contains(uri.getHost().toLowerCase()));
      }
      catch(java.net.URISyntaxException e)
      {
         return (false);
      }
   }



   /*******************************************************************************
    ** The qbit's configuration on the instance, if installed.
    *******************************************************************************/
   static CustomAppsQBitConfig configFor(QInstance qInstance)
   {
      if(qInstance == null || qInstance.getQBits() == null)
      {
         return (null);
      }
      return (qInstance.getQBits().values().stream().map(qBit -> qBit.getConfig()).filter(CustomAppsQBitConfig.class::isInstance)
         .map(CustomAppsQBitConfig.class::cast).findFirst().orElse(null));
   }



   public record CustomAppData(List<QAppMetaData> containerList, Map<String, String> positionMap, Map<String, QAppMetaData> appMap, Map<String, QWidgetMetaDataInterface> widgetMap) {}
}
