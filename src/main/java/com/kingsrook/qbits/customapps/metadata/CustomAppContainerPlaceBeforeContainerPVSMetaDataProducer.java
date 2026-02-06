/*
 * Copyright © 2022-2025. ColdTrack <contact@coldtrack.com>.  All Rights Reserved.
 */

package com.kingsrook.qbits.customapps.metadata;


import com.kingsrook.qbits.customapps.customizers.CustomAppContainerPlaceBeforeContainerPossibleValueSource;
import com.kingsrook.qqq.backend.core.exceptions.QException;
import com.kingsrook.qqq.backend.core.model.metadata.MetaDataProducer;
import com.kingsrook.qqq.backend.core.model.metadata.QInstance;
import com.kingsrook.qqq.backend.core.model.metadata.code.QCodeReference;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValueSource;
import com.kingsrook.qqq.backend.core.model.metadata.possiblevalues.QPossibleValueSourceType;


/*******************************************************************************
 ** Meta Data Producer for CustomAppContainerPlaceBeforeContainerPVS
 **
 **
 *******************************************************************************/
public class CustomAppContainerPlaceBeforeContainerPVSMetaDataProducer extends MetaDataProducer<QPossibleValueSource>
{
   public static final String NAME = "CustomAppContainerPlaceBeforeContainerPVS";



   /*******************************************************************************
    **
    *******************************************************************************/
   @Override
   public QPossibleValueSource produce(QInstance qInstance) throws QException
   {
      return (new QPossibleValueSource().withName(NAME).withType(QPossibleValueSourceType.CUSTOM).withCustomCodeReference(new QCodeReference(CustomAppContainerPlaceBeforeContainerPossibleValueSource.class)));
   }
}
