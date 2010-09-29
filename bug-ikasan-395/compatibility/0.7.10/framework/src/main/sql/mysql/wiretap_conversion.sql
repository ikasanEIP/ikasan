--
-- $Id$
-- $URL$
--
-- ====================================================================
--
-- Copyright (c) 2000-2008 by Mizuho International plc.
--  All Rights Reserved.
--
-- ====================================================================
--
-- Purpose: This file contains the required alterations for the IkasanWireTap
--          table when migrating from 0.7.10 to HEAD.
--
-- Author:  Ikasan Development Team
--

ALTER TABLE IkasanWiretap MODIFY EventId VARCHAR(255);
ALTER TABLE IkasanWiretap MODIFY PayloadId VARCHAR(255);
GO