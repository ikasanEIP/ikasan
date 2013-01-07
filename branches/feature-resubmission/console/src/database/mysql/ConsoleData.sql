-- ------------
-- Module Data
-- ------------

-- Modules - Wiretapped
INSERT INTO module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpSource', 'Deals with picking up files for demonstration purposes', NULL);
INSERT INTO module (Name, Description, DesignDiagramURL) VALUES ('demoFileDelivery-ftpTarget', 'Deals with delivering files for demonstration purposes', NULL);

-- -----------------------------
-- PointToPointFlowProfile Data
-- -----------------------------

INSERT INTO pointtopointflowprofile (Name) VALUES ('Demonstration File Delivery');

-- ----------------------
-- ----------------------
-- PointToPointFlow Data
-- ----------------------
-- ----------------------

-- ----------------------------
-- Demonstration File Delivery
-- ----------------------------

-- demoFileDelivery-ftpSource
INSERT INTO pointtopointflow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT pointtopointflowprofile.Id, NULL, module.Id
FROM pointtopointflowprofile, module
WHERE pointtopointflowprofile.Name = 'Demonstration File Delivery'
AND module.Name = 'demoFileDelivery-ftpSource';

-- demoFileDelivery-ftpSource --> demoFileDelivery-ftpTarget
INSERT INTO pointtopointflow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT pointtopointflowprofile.Id, M1.Id, M2.Id
FROM pointtopointflowprofile, module M1, module M2
WHERE pointtopointflowprofile.Name = 'Demonstration File Delivery'
AND M1.Name = 'demoFileDelivery-ftpSource'
AND M2.Name = 'demoFileDelivery-ftpTarget';

-- demoFileDelivery-ftpTarget
INSERT INTO pointtopointflow (PointToPointFlowProfileId, FromModuleId, ToModuleId) 
SELECT pointtopointflowprofile.Id, module.Id, NULL
FROM pointtopointflowprofile, module
WHERE pointtopointflowprofile.Name = 'Demonstration File Delivery'
AND Module.Name = 'demoFileDelivery-ftpTarget';
