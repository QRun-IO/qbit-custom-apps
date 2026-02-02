/*
 * Copyright © 2022-2023. ColdTrack <contact@coldtrack.com>.  All Rights Reserved.
 */

package com.kingsrook.qbits.customapps.widgets;


import java.util.Map;
import com.kingsrook.qqq.backend.core.actions.dashboard.widgets.AbstractWidgetRenderer;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetInput;
import com.kingsrook.qqq.backend.core.model.actions.widgets.RenderWidgetOutput;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.QWidgetData;
import com.kingsrook.qqq.backend.core.model.dashboard.widgets.WidgetType;


/*******************************************************************************
 **
 *******************************************************************************/
public class LookerWidgetRenderer extends AbstractWidgetRenderer
{
   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public RenderWidgetOutput render(RenderWidgetInput input) throws QException
   {
      try
      {
         Map<String, String> queryParams = input.getQueryParams();
         Integer             dashboardId = Integer.parseInt(queryParams.get("dashboardId"));
         return (new RenderWidgetOutput(new LookerWidgetData().withDashboardId(dashboardId)));
      }
      catch(Exception e)
      {
         throw (new QException("Error rendering LookerWidget", e));
      }
   }



   /***************************************************************************
    **
    ***************************************************************************/
   public static class LookerWidgetData extends QWidgetData
   {
      private Integer dashboardId;



      /***************************************************************************
       **
       ***************************************************************************/
      @Override
      public String getType()
      {
         return WidgetType.CUSTOM_COMPONENT.getType();
      }



      /*******************************************************************************
       ** Getter for dashboardId
       *******************************************************************************/
      public Integer getDashboardId()
      {
         return (this.dashboardId);
      }



      /*******************************************************************************
       ** Setter for dashboardId
       *******************************************************************************/
      public void setDashboardId(Integer dashboardId)
      {
         this.dashboardId = dashboardId;
      }



      /*******************************************************************************
       ** Fluent setter for dashboardId
       *******************************************************************************/
      public LookerWidgetData withDashboardId(Integer dashboardId)
      {
         this.dashboardId = dashboardId;
         return (this);
      }
   }

}
