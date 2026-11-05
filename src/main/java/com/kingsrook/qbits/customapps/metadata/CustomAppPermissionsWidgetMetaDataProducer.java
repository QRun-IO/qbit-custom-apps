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
import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.ChildRecordListRenderer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.dashboard.QWidgetMetaData;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinOn;
import com.kingsrook.qqq.backend.core.model.metadata.joins.JoinType;
import com.kingsrook.qqq.backend.core.model.metadata.joins.QJoinMetaData;


/*******************************************************************************
 ** The "users" section of a custom app: the roles that hold the app's access
 ** permission (rows of the permission-to-role table), as a child record list.
 *******************************************************************************/
public class CustomAppPermissionsWidgetMetaDataProducer extends MetaDataProducer<QWidgetMetaData>
{
   public static final String NAME      = "customAppPermissions";
   public static final String JOIN_NAME = "customAppRolePermission";



   /***************************************************************************
    ** The widget, and (as a side effect, once) its join.
    ***************************************************************************/
   @Override
   public QWidgetMetaData produce(QInstance qInstance) throws QException
   {
      if(qInstance.getJoin(JOIN_NAME) == null)
      {
         qInstance.addJoin(new QJoinMetaData()
            .withName(JOIN_NAME)
            .withLeftTable(CustomApp.TABLE_NAME)
            .withRightTable("rolePermissionInt")
            .withType(JoinType.ONE_TO_MANY)
            .withJoinOn(new JoinOn("permissionId", "permissionId")));
      }
      QWidgetMetaData widget = ChildRecordListRenderer.widgetMetaDataBuilder(qInstance.getJoin(JOIN_NAME))
         .withLabel("Roles with access")
         .getWidgetMetaData();
      widget.setName(NAME);
      return (widget);
   }
}
