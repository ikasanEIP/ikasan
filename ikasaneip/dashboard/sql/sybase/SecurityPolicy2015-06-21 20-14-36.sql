use MS_Ikasan01
GO

insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (1,null,'ALL','Policy to do everything','2015-05-01 09:58:15','2015-05-01 09:58:15')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (2,null,'Read Only','Read only policy','2015-05-01 09:58:15','2015-05-01 09:58:15')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (10,null,'WebServiceAdmin','Web service admin policy','2015-06-16 14:59:20','2015-06-16 14:59:20')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (12,null,'ViewBusinessStream','Policy to view business streams','2015-06-18 13:49:21','2015-06-18 13:49:21')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (13,null,'ViewWiretap','Policy to view wiretaps','2015-06-18 13:49:48','2015-06-18 13:49:48')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (14,null,'ViewExclusion','Policy to view exclusions','2015-06-18 13:50:07','2015-06-18 13:50:07')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (15,null,'ViewErrors','Policy to view errors','2015-06-18 13:50:31','2015-06-18 13:50:31')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (17,null,'CreateBusinessStream','Policy to create a business stream','2015-06-18 14:14:39','2015-06-18 14:14:39')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (18,null,'DeleteBusinessStream','Policy to delete a business stream','2015-06-18 14:15:12','2015-06-18 14:15:12')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (19,null,'ModifyBusinessStream','Policy to modify a business stream','2015-06-18 14:15:34','2015-06-18 14:15:34')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (20,null,'ActionExclusion','Policy to action an excluded event.','2015-06-18 14:16:30','2015-06-18 14:16:30')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (21,3,'TestBusinessStream','Business Stream test','2015-06-19 08:26:26','2015-06-19 08:26:26')
insert into [dbo].[SecurityPolicy]([Id],[PolicyLinkId],[Name],[Description],[CreatedDateTime],[UpdatedDateTime]) values (22,4,'FIBusinessStream','Access to FI Business Stream','2015-06-19 10:07:27','2015-06-19 10:07:27')

GO
