-- Create new table
CREATE TABLE MCSSourceConfigGroupSeq
(
   Id               NUMERIC(18, 0) IDENTITY NOT NULL,
   SequenceNumber   NUMERIC(20, 0) NULL,
   PRIMARY KEY (Id) -- clustered index on Id
)

LOCK DATAROWS

IF OBJECT_ID('MCSSourceConfigGroupSeq') IS NOT NULL 
   PRINT '<<< CREATED TABLE MCSSourceConfigGroupSeq >>>' 
ELSE
   PRINT '<<< FAILED CREATING TABLE MCSSourceConfigGroupSeq >>>' 
go

SET IDENTITY_INSERT MCSSourceConfigGroupSeq ON
insert into MCSSourceConfigGroupSeq (
   Id
  ,SequenceNumber
) VALUES (
   1   -- Id - IN numeric(18, 0)
  ,1   -- SequenceNumber - IN numeric(20, 0)
)
SET IDENTITY_INSERT MCSSourceConfigGroupSeq OFF

commit

-- Permissions
GRANT DELETE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT DELETE STATISTICS ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT INSERT ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT REFERENCES ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT SELECT ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT TRUNCATE TABLE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT UPDATE ON MCSSourceConfigGroupSeq TO CMI2Adm
go
GRANT UPDATE STATISTICS ON MCSSourceConfigGroupSeq TO CMI2Adm
go

ALTER TABLE MCSSourceConfigValue
ADD SourceConfigGroupId          NUMERIC(18, 0) NULL
