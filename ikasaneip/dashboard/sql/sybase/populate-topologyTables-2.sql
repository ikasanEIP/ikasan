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
  'cdw-trade'  -- Name - IN varchar(256)
  ,'CDW Trade publication and (re)consolidation.'
  ,@serverId   -- ServerId - IN numeric(18, 0)
)

select @moduleId = Id from IkasanModule where
	Name = 'cdw-trade'
 
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'IRS Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing IRS trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'IRS Trade Publisher' 

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF IRS Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF IRS Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to IRS XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to IRS XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Equity Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing Equity trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Equity Trade Publisher'

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Convertible Bond Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing Convertible Bond trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Convertible Bond Trade Publisher' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'FX Spot Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing FX Spot trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'FX Spot Trade Publisher' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Debt Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing FX Spot trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Debt Trade Publisher' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Cash Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing Cash trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Cash Trade Publisher' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'CDS Trade Publisher'  -- Name - IN varchar(256)
  ,'Publishing CDS trades to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'CDS Trade Publisher' 


insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'JMS CMF Equity Trade Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Equity Trade Consumer.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Extract JMS Map Payload to Equity XML'  -- Name - IN varchar(256)
  ,'Extract JMS Map Payload to Equity XML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML Trade to CDW Trade Event'  -- Name - IN varchar(256)
  ,'FpML Trade to CDW Trade Event.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Trade Event to MongoDB'  -- Name - IN varchar(256)
  ,'CDW Trade Event to MongoDB.'  -- Description - IN varchar(1024)
  ,@flowId
)

commit
