declare @serverId numeric(18),
@moduleId numeric(18),
@flowId numeric(18)

select @serverId = Id from Server where
	Name = 'ESB 01'


insert into IkasanModule (
  Name
  ,Description
  ,ServerId
) VALUES (
  'gloss-referencemarketDataTgt'  -- Name - IN varchar(256)
  ,'Gloss Reference Market Data Module'
  ,@serverId   -- ServerId - IN numeric(18, 0)
)

select @moduleId = Id from IkasanModule where
	Name = 'gloss-referencemarketDataTgt'
 
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Counterparty Transformer Flow'  -- Name - IN varchar(256)
  ,'Counterparty Transformer Flow.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Counterparty Transformer Flow' 

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'GoldenSource Counterparty Consumer'  -- Name - IN varchar(256)
  ,'GoldenSource Counterparty Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JmsText to String'  -- Name - IN varchar(256)
  ,'JmsText to String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Split counterparty by accounts'  -- Name - IN varchar(256)
  ,'Split counterparty by accounts.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CMF to Gloss Counterparty Transformer Flow Element'  -- Name - IN varchar(256)
  ,'CMF to Gloss Counterparty Transformer Flow Element.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Gloss Counterparty Validator Flow Element'  -- Name - IN varchar(256)
  ,'Gloss Counterparty Validator Flow Element.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Counterparty Transformer Producer'  -- Name - IN varchar(256)
  ,'Counterparty Transformer Producer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'ValidationResult To String'  -- Name - IN varchar(256)
  ,'ValidationResult To String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Counterparty Transformer Producer'  -- Name - IN varchar(256)
  ,'Counterparty Transformer Producer.'  -- Description - IN varchar(1024)
  ,@flowId
)


insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Counterparty Publisher Flow'  -- Name - IN varchar(256)
  ,'Counterparty Publisher Flow.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Counterparty Publisher Flow'

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Counterparty Publisher Consumer'  -- Name - IN varchar(256)
  ,'Counterparty Publisher Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JmsText to String'  -- Name - IN varchar(256)
  ,'JmsText to String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Counterparty Producer'  -- Name - IN varchar(256)
  ,'Counterparty Producer.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Asset Transformer Flow'  -- Name - IN varchar(256)
  ,'Asset Transformer Flow.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Asset Transformer Flow' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'GoldenSource Asset Consumer'  -- Name - IN varchar(256)
  ,'GoldenSource Asset Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JmsText to String'  -- Name - IN varchar(256)
  ,'JmsText to String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Split asset by issue listings'  -- Name - IN varchar(256)
  ,'Split asset by issue listings.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CMF to Gloss Asset Transformer Flow Element'  -- Name - IN varchar(256)
  ,'CMF to Gloss Asset Transformer Flow Element.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Gloss Asset Validator Flow Element'  -- Name - IN varchar(256)
  ,'Gloss Asset Validator Flow Element.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'ValidationResult To String'  -- Name - IN varchar(256)
  ,'ValidationResult To String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Asset Transformer Producer'  -- Name - IN varchar(256)
  ,'Asset Transformer Producer.'  -- Description - IN varchar(1024)
  ,@flowId
)


insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Asset Publisher Flow'  -- Name - IN varchar(256)
  ,'Asset Publisher Flow.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Asset Publisher Flow' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Asset Publisher Consumer'  -- Name - IN varchar(256)
  ,'Asset Publisher Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JmsText to String'  -- Name - IN varchar(256)
  ,'JmsText to String.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Asset Producer'  -- Name - IN varchar(256)
  ,'Asset Producer.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
commit
