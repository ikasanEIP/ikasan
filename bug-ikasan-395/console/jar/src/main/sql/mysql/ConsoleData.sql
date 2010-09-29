-- ------------
-- Module Data
-- ------------

-- Modules - Wiretapped
INSERT INTO Module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpSource', 'Deals with picking up files for demonstration purposes', NULL);
INSERT INTO Module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpTarget', 'Deals with delivering files for demonstration purposes', NULL);

-- -----------------------------
-- PointToPointFlowProfile Data
-- -----------------------------

INSERT INTO PointToPointFlowProfile (Name) VALUES ('Demonstration File Delivery');

-- ----------------------
-- ----------------------
-- PointToPointFlow Data
-- ----------------------
-- ----------------------

-- ----------------------------
-- Demonstration File Delivery
-- ----------------------------

-- demoFileDelivery-ftpSource
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, NULL, Module.Id
FROM PointToPointFlowProfile, Module
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND Module.Name = 'demoFileDelivery-ftpSource';

-- demoFileDelivery-ftpSource --> demoFileDelivery-ftpTarget
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, M1.Id, M2.Id
FROM PointToPointFlowProfile, Module M1, Module M2
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND M1.Name = 'demoFileDelivery-ftpSource'
AND M2.Name = 'demoFileDelivery-ftpTarget';

-- demoFileDelivery-ftpTarget
INSERT INTO PointToPointFlow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT PointToPointFlowProfile.Id, Module.Id, NULL
FROM PointToPointFlowProfile, Module
WHERE PointToPointFlowProfile.Name = 'Demonstration File Delivery'
AND Module.Name = 'demoFileDelivery-ftpTarget';
