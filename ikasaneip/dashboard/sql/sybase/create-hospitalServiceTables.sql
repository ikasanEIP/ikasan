CREATE TABLE ExclusionEventAction
(
   Id             				NUMERIC(18, 0) 		IDENTITY NOT NULL,
   ErrorUri	      				VARCHAR(256) 		UNIQUE NOT NULL,
   ActionedBy		      		VARCHAR(256) 		NOT NULL,
   Action		      			VARCHAR(32) 		NOT NULL,
   Event		      			IMAGE 				NOT NULL,
   Timestamp		      		NUMERIC(18, 0) 		default getdate() NOT NULL,
   PRIMARY KEY (Id)
) 

LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE NONCLUSTERED INDEX ExclEventAction_ErrorUri ON ExclusionEventAction(ErrorUri)