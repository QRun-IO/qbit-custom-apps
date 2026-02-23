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


import java.util.Map;
import com.kingsrook.qbits.userrolepermissions.model.Permission;
import com.kingsrook.qbits.userrolepermissions.model.PermissionObjectType;
import com.kingsrook.qqq.backend.core.actions.tables.GetAction;
import com.kingsrook.qqq.backend.core.actions.tables.InsertAction;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.tables.get.GetInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.get.GetOutput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertInput;
import com.kingsrook.qqq.backend.core.model.actions.tables.insert.InsertOutput;


/*******************************************************************************
 **
 *******************************************************************************/
public class CustomAppPermissionUtils
{

   /***************************************************************************
    **
    ***************************************************************************/
   public static Permission buildOrGetPermission(String name, String tableName) throws QException
   {
      String objectName     = CustomAppsUtils.getCamelCaseName(name, tableName);
      String permissionName = objectName + ".hasAccess";

      /////////////////////////////////////
      // if permission exists, return it //
      /////////////////////////////////////
      GetOutput output = new GetAction().execute(new GetInput(Permission.TABLE_NAME).withUniqueKey(Map.of("name", permissionName)));
      if(output.getRecord() != null)
      {
         return (new Permission(output.getRecord()));
      }

      //////////////////////////////////////////////////////
      // otherwise create a new permission to be inserted //
      //////////////////////////////////////////////////////
      Permission permission = new Permission()
         .withName(permissionName)
         .withDescription("Permission to access " + name)
         .withObjectType(PermissionObjectType.APP.getId())
         .withObjectLabel(name);

      InsertInput  permissionInput  = new InsertInput(Permission.TABLE_NAME).withRecordEntity(permission);
      InsertOutput permissionOutput = new InsertAction().execute(permissionInput);
      return (new Permission(permissionOutput.getRecords().get(0)));
   }

}
