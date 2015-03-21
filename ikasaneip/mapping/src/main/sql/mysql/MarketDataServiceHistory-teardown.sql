--
-- $Id: MarketDataServiceHistory-teardown.sql 31790 2013-07-22 13:32:26Z stewmi $
-- $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/sql/mysql/MarketDataServiceHistory-teardown.sql $
--
-- ====================================================================
--
-- Copyright (c) 2000-2010 by Mizuho International plc.
-- All Rights Reserved.
--
-- ====================================================================
--
-- Author: CMI2 Development Team
--
-- Drop aleady existing table if any
IF OBJECT_ID('PriceHistory') IS NOT NULL 
   BEGIN
      DROP TABLE PriceHistory 
      IF OBJECT_ID('PriceHistory') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE PriceHistory >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE PriceHistory >>>' 
   END 
go

-- Drop aleady existing table if any
IF OBJECT_ID('TradePositionHistory') IS NOT NULL 
   BEGIN
      DROP TABLE TradePositionHistory 
      IF OBJECT_ID('TradePositionHistory') IS NOT NULL 
         PRINT '<<< FAILED DROPPING TABLE TradePositionHistory >>>' 
      ELSE
         PRINT '<<< DROPPED TABLE TradePositionHistory >>>' 
   END
go
