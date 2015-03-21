
--
-- $Id: ConfigurationService-teardown.sql 40152 2014-10-17 15:57:49Z stewmi $
-- $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/sql/sybase/ConfigurationService-teardown.sql $
--
-- ====================================================================
--
-- Copyright (c) 2000-2012 by Mizuho International plc.
-- All Rights Reserved.
--
-- ====================================================================
--
-- Author: CMI2 Development Team
--

IF OBJECT_ID('MCSKeyLocationQuery') IS NOT NULL 
   BEGIN
      DROP TABLE MCSKeyLocationQuery
      IF OBJECT_ID('MCSKeyLocationQuery') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSKeyLocationQuery >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSKeyLocationQuery >>>' 
   END
go

IF OBJECT_ID('MCSSourceConfigValue') IS NOT NULL 
   BEGIN
      DROP TABLE MCSSourceConfigValue 
      IF OBJECT_ID('MCSSourceConfigValue') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSSourceConfigValue >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSSourceConfigValue >>>' 
   END
go


IF OBJECT_ID('MCSTargetConfigValue') IS NOT NULL 
   BEGIN
      DROP TABLE MCSTargetConfigValue
      IF OBJECT_ID('MCSTargetConfigValue') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSTargetConfigValue >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSTargetConfigValue >>>' 
   END
go

IF OBJECT_ID('MCSMappingConfiguration') IS NOT NULL 
   BEGIN
      DROP TABLE MCSMappingConfiguration 
      IF OBJECT_ID('MCSMappingConfiguration') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSMappingConfiguration >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSMappingConfiguration >>>' 
   END
go

IF OBJECT_ID('MCSConfigServiceClient') IS NOT NULL 
   BEGIN
      DROP TABLE MCSConfigServiceClient 
      IF OBJECT_ID('MCSConfigServiceClient') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSConfigServiceClient >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSConfigServiceClient >>>' 
   END
go

IF OBJECT_ID('MCSConfigurationContext') IS NOT NULL 
   BEGIN
      DROP TABLE MCSConfigurationContext
      IF OBJECT_ID('MCSConfigurationContext') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSConfigurationContext >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSConfigurationContext >>>' 
   END
go

IF OBJECT_ID('MCSConfigurationType') IS NOT NULL 
   BEGIN
      DROP TABLE MCSConfigurationType
      IF OBJECT_ID('MCSConfigurationType') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSConfigurationType >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSConfigurationType >>>' 
   END
go

IF OBJECT_ID('MCSSourceConfigGroupSeq') IS NOT NULL 
   BEGIN
      DROP TABLE MCSSourceConfigGroupSeq
      IF OBJECT_ID('MCSSourceConfigGroupSeq') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE MCSSourceConfigGroupSeq >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE MCSSourceConfigGroupSeq >>>' 
   END
go
