DROP TABLE ExclusionEvent
DROP TABLE ErrorOccurrence

CREATE TABLE ExclusionEvent
(
Id NUMERIC IDENTITY NOT NULL,
ModuleName VARCHAR(255) NOT NULL,
FlowName VARCHAR(255) NOT NULL,
Identifier VARCHAR(255) NOT NULL,
Event IMAGE NULL,
ErrorUri VARCHAR(255) NOT NULL,
Timestamp NUMERIC DEFAULT 0 NOT NULL,
Expiry NUMERIC NULL
)
LOCK DATAROWS
WITH IDENTITY_GAP=1

CREATE UNIQUE INDEX
IkasanExclusionEvent01u ON
ExclusionEvent(Id,ModuleName,FlowName,Identifier)


CREATE TABLE ErrorOccurrence
(
	Uri VARCHAR(255) NOT NULL,
	ModuleName VARCHAR(255) NOT NULL,
	FlowName VARCHAR(255) NOT NULL,
	FlowElementName VARCHAR(255) NOT NULL,
	ErrorDetail TEXT NULL,
	EventLifeIdentifier VARCHAR(255) NULL,
	EventRelatedIdentifier VARCHAR(255) NULL,
	Action VARCHAR(255) NULL,
	Event IMAGE NULL,
	Timestamp NUMERIC DEFAULT 0 NOT NULL,
	Expiry NUMERIC NOT NULL
	)
	LOCK DATAROWS
	WITH IDENTITY_GAP=1

	CREATE UNIQUE INDEX
	IkasanErrorOccurrence01u ON
	ErrorOccurrence(ModuleName,FlowName,FlowElementName,Uri,Timestamp)