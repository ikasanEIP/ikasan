-- Set behaviours
SET FLUSHMESSAGE ON
SET NOCOUNT ON

-- Declare required vars
DECLARE @Timestamp CHAR(19)
DECLARE @RowsInTotal INT
DECLARE @RowsToDelete INT
DECLARE @Error INT

-- Set vars
SELECT @Error = 0

-- Print useful info
SELECT @Timestamp = CONVERT(CHAR(10), GETDATE(), 111) + ' ' + CONVERT(CHAR(8), GETDATE(), 108)
PRINT '%1! **** MessageFilter Housekeeping started ****', @Timestamp

-- Anything to delete?
SELECT @RowsInTotal = COUNT(*) FROM MessageFilter
SELECT @RowsToDelete = COUNT(*) FROM MessageFilter WHERE Expiry < (select cast(Datediff(s, '1970-01-01', GETUTCDATE()) AS bigint)*1000)
SELECT @Timestamp = CONVERT(CHAR(10), GETDATE(), 111) + ' ' + CONVERT(CHAR(8), GETDATE(), 108)
IF (@RowsToDelete > 0)
    PRINT '%1! About to start housekeeping MessageFilter table.', @Timestamp
ELSE
    PRINT '%1! Nothing to do.', @Timestamp

-- Time to housekeep!
WHILE (@RowsToDelete > 0)
BEGIN
    SELECT @Timestamp = CONVERT(CHAR(10), GETDATE(), 111) + ' ' + CONVERT(CHAR(8), GETDATE(), 108)
    PRINT '%1! Remaining records: [%2!/%3!].', @Timestamp, @RowsToDelete, @RowsInTotal
    
    --BEGIN TRAN
    SET ROWCOUNT 1000
    DELETE MessageFilter where Expiry < (select cast(Datediff(s, '1970-01-01', GETUTCDATE()) AS bigint)*1000)

    SELECT @Error = @@error
    IF (@Error != 0)
    BEGIN
        --WHILE (@@trancount > 0)
        --    ROLLBACK
        SELECT @Timestamp = CONVERT(CHAR(10), GETDATE(), 111) + ' ' + CONVERT(CHAR(8), GETDATE(), 108)
        PRINT '%1! Error while deleting - Error code: [%2!].', @Timestamp, @Error
        SELECT @RowsToDelete = -1
    END
    ELSE
    BEGIN
        --COMMIT TRAN
        SELECT @RowsToDelete = COUNT(*) FROM MessageFilter WHERE Expiry < (select cast(Datediff(s, '1970-01-01', GETUTCDATE()) AS bigint)*1000)
    END
END

SELECT @Timestamp = CONVERT(CHAR(10), GETDATE(), 111) + ' ' + CONVERT(CHAR(8), GETDATE(), 108)
PRINT '%1! **** MessageFilter Housekeeping completed ****', @Timestamp
