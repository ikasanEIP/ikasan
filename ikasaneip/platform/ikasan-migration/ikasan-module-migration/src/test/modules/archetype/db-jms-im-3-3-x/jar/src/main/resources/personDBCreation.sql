      MERGE INTO Person (Id, name, dobDayOfMonth, dobMonthOfYear, dobYear )
			KEY (id)
					VALUES (1,'testUser', 10,1,1980);
