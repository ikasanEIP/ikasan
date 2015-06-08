declare @serverId numeric(18),
@moduleId numeric(18),
@flowId numeric(18)

insert into Server (
  Name
  ,Description
  ,Url
  ,Port
) VALUES (
   'ESB 01'  -- Name - IN varchar(256)
  ,'Initial ESB Server'  -- Description - IN varchar(1024)
  ,'svc-esb01d'  -- Url - IN varchar(256)
  ,8380   -- Port - IN numeric(18, 0)
)

select @serverId = Id from Server where
	Name = 'ESB 01'

insert into IkasanModule (
  Name
  ,Description
  ,ServerId
) VALUES (
  'cdw-asset'  -- Name - IN varchar(256)
  ,'CDW Asset publication and (re)consolidation.'
  ,@serverId   -- ServerId - IN numeric(18, 0)
)

select @moduleId = Id from IkasanModule where
	Name = 'cdw-asset'
  
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Asset Publisher'  -- Name - IN varchar(256)
  ,'Flow to publish assets to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Asset Publisher' 

insert into Component (
  Name
  ,Description
  ,FlowId
  ,IsConfigurable
  ,ConfigurationId
) VALUES (
  'JMS CMF Asset Consumer'  -- Name - IN varchar(256)
  ,'JMS CMF Asset Consumer which consumes cmf assets from a public topic.'  -- Description - IN varchar(1024)
  ,@flowId
  ,1
  ,'jmsCmfAssetConsumerConfiguration'
)

insert into Component (
  Name
  ,Description
  ,FlowId
  ,IsConfigurable
  ,ConfigurationId
) VALUES (
  'Extract JMS Map Payload '
  ,'Extracts a map from the payload.'
  ,@flowId
  ,0
  ,NULL
)

insert into Component (
  Name
  ,Description
  ,FlowId
  ,IsConfigurable
  ,ConfigurationId
) VALUES (
  'Convert XML to CDW Asset Event Object'
  ,'Binds XML to a java object using JAXB'
  ,@flowId
  ,0
  ,NULL
)

insert into Component (
  Name
  ,Description
  ,FlowId
  ,IsConfigurable
  ,ConfigurationId
) VALUES (
  'CDW Asset Event to MongoDB'
  ,'Writes the CDW asset to the Mongo database'
  ,@flowId
  ,1
  ,'cdw-mongo-asset'
)

insert into IkasanModule (
  Name
  ,Description
  ,ServerId
) VALUES (
  'cdw-eod'  -- Name - IN varchar(256)
  ,'CDW EndOfDay for sourcing file events from Murex Datamart and Xenomorph and publishing into CDW.'
  ,@serverId   -- ServerId - IN numeric(18, 0)
)

select @moduleId = Id from IkasanModule where
	Name = 'cdw-eod'
 
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'EOD Files'  -- Name - IN varchar(256)
  ,'CDW EndOfDay for sourcing file events from Murex Datamart and Xenomorph and publishing into CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'EOD Files' 

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Scheduled Consumer'  -- Name - IN varchar(256)
  ,'Scheduled Consumer to consume the end of day messages.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Multiple Files to Single Files Splitter'  -- Name - IN varchar(256)
  ,'Aggregates multiple files into a single file.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Process File MR Router'  -- Name - IN varchar(256)
  ,'Process File MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Archive File'  -- Name - IN varchar(256)
  ,'Archive File.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Batching Service Management'  -- Name - IN varchar(256)
  ,'Batching Service Management.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Caching Service Management'  -- Name - IN varchar(256)
  ,'Caching Service Management.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Data MR Router'  -- Name - IN varchar(256)
  ,'CDW Data MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Filter Partially Populated FpML Caches'  -- Name - IN varchar(256)
  ,'Filter Partially Populated FpML Caches.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'File Type Router'  -- Name - IN varchar(256)
  ,'File Type Router.'  -- Description - IN varchar(1024)
  ,@flowId
)
 
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Delete Latest FpML Trades'  -- Name - IN varchar(256)
  ,'Delete Latest FpML Trades.'  -- Description - IN varchar(1024)
  ,@flowId
) 
 
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Curve MR Router'  -- Name - IN varchar(256)
  ,'Curve MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)  	

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Curve MR Router'  -- Name - IN varchar(256)
  ,'Curve MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade MR Router'  -- Name - IN varchar(256)
  ,'Trade MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Cashflow MR Router'  -- Name - IN varchar(256)
  ,'Cashflow MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)	

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'FpML to CDW Coordinator'  -- Name - IN varchar(256)
  ,'FpML to CDW Coordinator.'  -- Description - IN varchar(1024)
  ,@flowId
)	

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Bootstrapped/Xenomorph Curve Batch Update'  -- Name - IN varchar(256)
  ,'Bootstrapped/Xenomorph Curve Batch Update.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Curve Type Router'  -- Name - IN varchar(256)
  ,'Curve Type Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'GLPosting BES Event to CDW'  -- Name - IN varchar(256)
  ,'GLPosting BES Event to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)
 
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'GLPosting Batch Update to CDW'  -- Name - IN varchar(256)
  ,'GLPosting Batch Update to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'GLPosting File to Lines Splitter'  -- Name - IN varchar(256)
  ,'GLPosting File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade/TradeLeg Batch Update'  -- Name - IN varchar(256)
  ,'Trade/TradeLeg Batch Update.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade Type Router'  -- Name - IN varchar(256)
  ,'Trade Type Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Cashflow BES Event to CDW'  -- Name - IN varchar(256)
  ,'Cashflow BES Event to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CashFlow Batch Update to CDW'  -- Name - IN varchar(256)
  ,'CashFlow Batch Update to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CashFlow File to Lines Splitter'  -- Name - IN varchar(256)
  ,'CashFlow File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Update FpML Batch Processing End Date'  -- Name - IN varchar(256)
  ,'Update FpML Batch Processing End Date.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Raise Fpml Eod Trade BES Event to CDW'  -- Name - IN varchar(256)
  ,'Raise Fpml Eod Trade BES Event to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)
 	
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Get Cached Trades for FpML'  -- Name - IN varchar(256)
  ,'Get Cached Trades for FpML.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Filter Partially Populated Curve Cache'  -- Name - IN varchar(256)
  ,'Filter Partially Populated Curve Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Clear BootstrappedCurve Cache'  -- Name - IN varchar(256)
  ,'Clear BootstrappedCurve Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Clear XenomorphCurve Cache'  -- Name - IN varchar(256)
  ,'Clear XenomorphCurve Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert to DatamartGLPosting'  -- Name - IN varchar(256)
  ,'Convert to DatamartGLPosting.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Filter Partially Populated Trade Cache'  -- Name - IN varchar(256)
  ,'Filter Partially Populated Trade Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Clear Trade Cache'  -- Name - IN varchar(256)
  ,'Clear Trade Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Clear TradeLeg Cache'  -- Name - IN varchar(256)
  ,'Clear TradeLeg Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert to DatamartCashFlow'  -- Name - IN varchar(256)
  ,'Convert to DatamartCashFlow.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Filter Trades of Correct Murex Typology'  -- Name - IN varchar(256)
  ,'Filter Trades of Correct Murex Typology.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Curve to CDW Coordinator'  -- Name - IN varchar(256)
  ,'Curve to CDW Coordinator.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'BootstrappedCurve File to Lines Splitter'  -- Name - IN varchar(256)
  ,'BootstrappedCurve File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'XenomorphCurve File to Lines Splitter'  -- Name - IN varchar(256)
  ,'XenomorphCurve File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Update DatamartGLPosting'  -- Name - IN varchar(256)
  ,'Update DatamartGLPosting.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade/TradeLeg to CDW Coordinator'  -- Name - IN varchar(256)
  ,'Trade/TradeLeg to CDW Coordinator.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade File to Lines Splitter'  -- Name - IN varchar(256)
  ,'Trade File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'TradeLeg File to Lines Splitter'  -- Name - IN varchar(256)
  ,'TradeLeg File to Lines Splitter.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Update DatamartCashFlow'  -- Name - IN varchar(256)
  ,'Update DatamartCashFlow.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Add Legs to DatamartTrade for FpML Trade'  -- Name - IN varchar(256)
  ,'Add Legs to DatamartTrade for FpML Trade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Generated Curve Batch Update to CDW'  -- Name - IN varchar(256)
  ,'Generated Curve Batch Update to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Curve BES Event to CDW'  -- Name - IN varchar(256)
  ,'Curve BES Event to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Get Cache Curve Entries'  -- Name - IN varchar(256)
  ,'Get Cache Curve Entries.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert BootstrappedCurve Fileline to ConsolidatedCurve'  -- Name - IN varchar(256)
  ,'Convert BootstrappedCurve Fileline to ConsolidatedCurve.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert XenomorphCurve Fileline to ConsolidatedCurve'  -- Name - IN varchar(256)
  ,'Convert XenomorphCurve Fileline to ConsolidatedCurve.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish GLPosting to CDW'  -- Name - IN varchar(256)
  ,'Publish GLPosting to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Generated Trade Batch Update to CDW'  -- Name - IN varchar(256)
  ,'Generated Trade Batch Update to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Trade BES Event to CDW'  -- Name - IN varchar(256)
  ,'Trade BES Event to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Get Cached Trades'  -- Name - IN varchar(256)
  ,'Get Cached Trades.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert Line to DatamartTrade'  -- Name - IN varchar(256)
  ,'Convert Line to DatamartTrade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert Line to DatamartTradeLeg'  -- Name - IN varchar(256)
  ,'Convert Line to DatamartTradeLeg.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish Cashflow to CDW'  -- Name - IN varchar(256)
  ,'Publish Cashflow to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Lookup Counterparty reference for FpML Trade'  -- Name - IN varchar(256)
  ,'Lookup Counterparty reference for FpML Trade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Merge Curves into CdwCurve'  -- Name - IN varchar(256)
  ,'Merge Curves into CdwCurve.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish Bootstrapped ConsolidatedCurve to Cache'  -- Name - IN varchar(256)
  ,'Publish Bootstrapped ConsolidatedCurve to Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish Xenomorph ConsolidatedCurve to Cache'  -- Name - IN varchar(256)
  ,'Publish Bootstrapped ConsolidatedCurve to Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Add Legs to DatamartTrade'  -- Name - IN varchar(256)
  ,'Add Legs to DatamartTrade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish DatamartTrade to Cache'  -- Name - IN varchar(256)
  ,'Publish DatamartTrade to Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish TradeLeg to Cache'  -- Name - IN varchar(256)
  ,'Publish TradeLeg to Cache.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Lookup Security reference for FpML Trade'  -- Name - IN varchar(256)
  ,'Lookup Security reference for FpML Trade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Filter Partial Curves'  -- Name - IN varchar(256)
  ,'Filter Partial Curves.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Lookup Counterparty reference'  -- Name - IN varchar(256)
  ,'Lookup Counterparty reference.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert Consolidated Trade to FpML Trade'  -- Name - IN varchar(256)
  ,'Convert Consolidated Trade to FpML Trade.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish Curve to CDW'  -- Name - IN varchar(256)
  ,'Publish Curve to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Lookup Security reference'  -- Name - IN varchar(256)
  ,'Lookup Security reference.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish FpML Trade to Multiple Collections'  -- Name - IN varchar(256)
  ,'Publish FpML Trade to Multiple Collections.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish DatamartTrade to CDW'  -- Name - IN varchar(256)
  ,'Publish DatamartTrade to CDW.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish FpML trade to Latest collection'  -- Name - IN varchar(256)
  ,'Publish FpML trade to Latest collection.'  -- Description - IN varchar(1024)
  ,@flowId
)
 
insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Publish FpML trade to Version collection'  -- Name - IN varchar(256)
  ,'Publish FpML trade to Version collection.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into IkasanModule (
  Name
  ,Description
  ,ServerId
) VALUES (
  'cdw-cashbalance'  -- Name - IN varchar(256)
  ,'CDW Cash Balance publication. '
  ,@serverId   -- ServerId - IN numeric(18, 0)
)

select @moduleId = Id from IkasanModule where
	Name = 'cdw-cashbalance'
  
insert into Flow (
  Name
  ,Description
  ,ModuleId
) VALUES (
  'Cash Balance'  -- Name - IN varchar(256)
  ,'Cash Balance to CDW.'  -- Description - IN varchar(1024)
  ,@moduleId   -- ModuleId - IN numeric(18, 0)
)

select @flowId = Id from Flow where
	Name = 'Cash Balance' 

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Convert the cash balance'  -- Name - IN varchar(256)
  ,'Convert the cash balance.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Cash Balance MR Router'  -- Name - IN varchar(256)
  ,'Cash Balance MR Router.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Cash Balance BES Publisher'  -- Name - IN varchar(256)
  ,'CDW Cash Balance BES Publisher.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'Split the cash balance list'  -- Name - IN varchar(256)
  ,'Split the cash balance list.'  -- Description - IN varchar(1024)
  ,@flowId
)

insert into Component (
  Name
  ,Description
  ,FlowId
) VALUES (
  'CDW Cash Balance Publisher'  -- Name - IN varchar(256)
  ,'CDW Cash Balance Publisher.'  -- Description - IN varchar(1024)
  ,@flowId
)

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

