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


import java.util.ArrayList;
import java.util.List;
import com.kingsrook.qqq.backend.core.actions.metadata.DefaultNoopMetaDataActionCustomizer;
import com.kingsrook.qqq.backend.core.context.QContext;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.logging.QLogger;
import com.kingsrook.qqq.backend.core.model.actions.metadata.MetaDataInput;
import com.kingsrook.qqq.backend.core.model.actions.metadata.MetaDataOutput;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaDataInterface;
import com.kingsrook.qqq.backend.core.model.metadata.frontend.AppTreeNode;
import com.kingsrook.qqq.backend.core.model.metadata.frontend.QFrontendAppMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.frontend.QFrontendWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.layout.QAppMetaData;
import static com.kingsrook.qqq.backend.core.logging.LogUtils.logPair;


/*******************************************************************************
 ** Offers the declared custom apps to the frontend, per session: each request's
 ** metadata gets exactly the containers, apps and widgets whose
 ** "&lt;name&gt;.hasAccess" permission the session holds, ahead of a container named
 ** by its "place before" setting.  Everything else is the application's own
 ** metadata customizer behaviour, left unchanged (this extends the standard no-op
 ** customizer, so an application that needs its own rules extends this class).
 *******************************************************************************/
public class CustomAppsMetaDataActionCustomizer extends DefaultNoopMetaDataActionCustomizer
{
   private static final QLogger LOG = QLogger.getLogger(CustomAppsMetaDataActionCustomizer.class);



   /***************************************************************************
    ** Add the permitted custom apps to the finished metadata.
    ***************************************************************************/
   @Override
   public void postProcess(MetaDataOutput metaDataOutput) throws QException
   {
      super.postProcess(metaDataOutput);

      CustomAppsDynamicMetaDataProducer.CustomAppData data;
      try
      {
         data = new CustomAppsDynamicMetaDataProducer().getCustomAppList(QContext.getQInstance());
      }
      catch(QException | RuntimeException e)
      {
         LOG.warn("Custom apps could not be read; none are offered", logPair("error", e.getClass().getSimpleName()));
         return;
      }

      MetaDataInput input = new MetaDataInput();
      for(QWidgetMetaDataInterface widget : data.widgetMap().values())
      {
         if(QContext.getQInstance().getWidget(widget.getName()) != null)
         {
            metaDataOutput.getWidgets().put(widget.getName(), new QFrontendWidgetMetaData(input, widget));
         }
      }
      for(QAppMetaData app : data.appMap().values())
      {
         metaDataOutput.getApps().put(app.getName(), new QFrontendAppMetaData(app, metaDataOutput));
      }

      List<AppTreeNode> tree = new ArrayList<>(metaDataOutput.getAppTree());
      for(QAppMetaData container : data.containerList())
      {
         AppTreeNode node = new AppTreeNode(container);
         for(var section : container.getSections())
         {
            for(String appName : section.getApps())
            {
               QAppMetaData child = data.appMap().get(appName);
               if(child != null)
               {
                  node.addChild(new AppTreeNode(child));
               }
            }
         }
         metaDataOutput.getApps().put(container.getName(), new QFrontendAppMetaData(container, metaDataOutput));
         int position = tree.size();
         String before = data.positionMap().get(container.getName());
         for(int i = 0; before != null && i < tree.size(); i++)
         {
            if(before.equals(tree.get(i).getName()))
            {
               position = i;
               break;
            }
         }
         tree.add(position, node);
      }
      metaDataOutput.setAppTree(tree);
   }
}
