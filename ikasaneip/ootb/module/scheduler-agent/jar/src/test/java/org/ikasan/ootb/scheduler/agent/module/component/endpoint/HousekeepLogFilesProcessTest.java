package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.apache.commons.io.FileUtils;
import org.ikasan.ootb.scheduler.agent.module.component.endpoint.configuration.HousekeepLogFilesProcessConfiguration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.stream.Collectors;

public class HousekeepLogFilesProcessTest {

    HousekeepLogFilesProcessConfiguration configuration;
    HousekeepLogFilesProcess housekeepLogFilesProcess;

    @Before
    public void setup() throws IOException {

        // clean any files from previous test classes
        clean();

        configuration = new HousekeepLogFilesProcessConfiguration();
        configuration.setLogFolder("src/test/resources/data/housekeep");
        configuration.setFolderToMove("src/test/resources/data/housekeep/archive");
        configuration.setTimeToLive(100);

        housekeepLogFilesProcess = new HousekeepLogFilesProcess();
        housekeepLogFilesProcess.setConfiguration(configuration);

        Files.createDirectory(Paths.get(configuration.getLogFolder()));
        Files.createDirectory(Paths.get(configuration.getFolderToMove()));

        LocalDate oldDate = LocalDate.of(2020, 12, 31);
        Instant instant = oldDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Path path1 = Paths.get(configuration.getLogFolder(), "logFile-1.txt");
        Path path2 = Paths.get(configuration.getLogFolder(), "logFile-2.txt");
        Path path3 = Paths.get(configuration.getLogFolder(), "logFile-3.txt");

        Files.createFile(path1);
        Files.createFile(path2);
        Files.createFile(path3);

        Files.setLastModifiedTime(path1, FileTime.from(instant));
        Files.setLastModifiedTime(path2, FileTime.from(instant));
    }

    @After
    public void clean() throws IOException {
        FileUtils.deleteDirectory(new File("src/test/resources/data/housekeep"));
    }

    @Test
    public void test_housekeep_delete() {

        Assert.assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        Assert.assertEquals(1, files.size());
        Assert.assertEquals("logFile-3.txt", files.stream().findFirst().get().getName() );
    }

    @Test
    public void test_housekeep_move() {

        Assert.assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldMove(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        Assert.assertEquals(1, files.size());

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        Assert.assertEquals(2, filesInArchive.size());

        Assert.assertEquals("logFile-3.txt", files.stream().findFirst().get().getName() );
    }

    @Test
    public void test_housekeep_archive() {

        Assert.assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldArchive(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        Assert.assertEquals(2, files.size());

        for (File file : files.stream().collect(Collectors.toList())) {
            Assert.assertTrue(file.getName().equals("logFile-3.txt") || (file.getName().contains("logFiles-") && file.getName().contains(".tar.gz")) );
        }

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        Assert.assertEquals(0, filesInArchive.size());
    }

    @Test
    public void test_housekeep_archive_and_move() {

        Assert.assertEquals(3, FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false).size());

        configuration.setShouldArchive(true);
        configuration.setShouldMove(true);
        housekeepLogFilesProcess.setConfiguration(configuration);

        housekeepLogFilesProcess.invoke(null);

        Collection<File> files = FileUtils.listFiles(new File("src/test/resources/data/housekeep"), null, false);

        Assert.assertEquals(1, files.size());
        Assert.assertEquals("logFile-3.txt", files.stream().collect(Collectors.toList()).get(0).getName() );

        Collection<File> filesInArchive = FileUtils.listFiles(new File("src/test/resources/data/housekeep/archive"), null, false);

        Assert.assertEquals(1, filesInArchive.size());
        Assert.assertTrue(filesInArchive.stream().collect(Collectors.toList()).get(0).getName().contains("logFiles-") );
        Assert.assertTrue(filesInArchive.stream().collect(Collectors.toList()).get(0).getName().contains(".tar.gz") );
    }

    @Test
    public void create_flow_def() {
        String base="{\"BBG_Anvil_NYClose_Original_0700_2\":\"FILE\",\"AC_SCRIPT_Export_NM_Price_1600\":\"SCHEDULER_JOB\",\"BBG_Anvil_NYClose_Original_0700_1\":\"FILE\",\"AC_NM_SOI_1530_RCVD_EV\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_NumerixSwaptionVolatility_1530\":\"SCHEDULER_JOB\",\"ANV_REQUEST_MHIRepoBonds_RCVD_EV\":\"FILE\",\"AC_CHAIN_PriceExport_Numerix_1600_ScheduledJob_17:00:00\":\"QUARTZ\",\"AC_SCRIPT_PriceConsolidate_AnvilEquityEUClose_2100\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_NumerixInterestRateSwap_1530\":\"SCHEDULER_JOB\",\"BB_NumerixFXSpot_1600\":\"FILE\",\"AC_CHAIN_PriceConsolidate_Numerix_1600_ScheduledJob_17:00:00\":\"QUARTZ\",\"BBG_Anvil_TWMDClose_GetData_1730\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondEUClose_1600\":\"SCHEDULER_JOB\",\"BB_NumerixDeposit_1600\":\"FILE\",\"AC_CHAIN_AnvilBondEUClose_1600_ScheduledJob_16:00:00\":\"QUARTZ\",\"AC_SCRIPT_PriceRequest_BB_NumerixFXVolatility_1530\":\"SCHEDULER_JOB\",\"AC_SCRIPT_Export_CDW_AnvilBondNYClose_0700\":\"SCHEDULER_JOB\",\"AC_SCRIPT_Export_CDW_AnvilEquityAsiaClose_0830\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityEUClose_2100\":\"SCHEDULER_JOB\",\"BB_NumerixIRS_1600\":\"FILE\",\"AC_SCRIPT_RefreshADO_ADMN_BloombergRequestAnvilBondEUClose\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceConsolidate_NumerixCapVolatility_1600\":\"SCHEDULER_JOB\",\"BB_NumerixFXVolatility_1600\":\"FILE\",\"BBG_Anvil_BondEUClose_GetData_1600\":\"FILE\",\"BB_NumerixCapVolatility_1600_2\":\"FILE\",\"BBG_Anvil_NYClose_Original_0700\":\"FILE\",\"BB_NumerixCapVolatility_1600_1\":\"FILE\",\"BB_NumerixSwaptionVolatility_1600_1\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_NumerixDeposit_1530\":\"SCHEDULER_JOB\",\"BBG_Anvil_BondEUClose1600_1\":\"FILE\",\"BBG_Anvil_NYClose_GetData_0700\":\"FILE\",\"BB_NumerixSwaptionVolatility_1600_2\":\"FILE\",\"BBG_Anvil_BondEUClose1600_2\":\"FILE\",\"AC_CHAIN_AnvilSOI_1530_ScheduledJob_15:05:00\":\"QUARTZ\",\"AC_SCRIPT_Interface_Bloomberg_DLResponses\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceConsolidate_AnvilBondNYClose_0700\":\"SCHEDULER_JOB\",\"AC_SCRIPT_Interface_CDW_AssetsAnvilSOI\":\"SCHEDULER_JOB\",\"BBG_Anvil_NYClose_GetData_Original_0700\":\"FILE\",\"AC_CHAIN_AnvilEquityEUClose_2100_ScheduledJob_21:00:00\":\"QUARTZ\",\"AC_CHAIN_BloombergRequest_Numerix_1530_ScheduledJob_15:45:00\":\"QUARTZ\",\"AC_SCRIPT_PriceRequest_BB_NumerixFRA_1530\":\"SCHEDULER_JOB\",\"AC_SCRIPT_RefreshADO_SOII_Anvil\":\"SCHEDULER_JOB\",\"AC_SCRIPT_Export_CDW_AnvilBondEUClose_1600\":\"SCHEDULER_JOB\",\"BB_NumerixIndex_1600\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_NumerixBasisSwap_1530\":\"SCHEDULER_JOB\",\"BB_NumerixFXVolatility_1600_1\":\"FILE\",\"BB_NumerixFXVolatility_1600_2\":\"FILE\",\"BBG_Anvil_BondEUClose_Original_1600_1\":\"FILE\",\"BBG_Anvil_BondEUClose_Original_1600_2\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_AnvilBondUKClose_1730\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_NumerixCapVolatility_1530\":\"SCHEDULER_JOB\",\"AC_BBG_Anvil_AsiaClose_0830\":\"FILE\",\"BBG_Anvil_BondEUClose_Original_1600\":\"FILE\",\"AC_SCRIPT_RefreshADO_SOII_AnvilBondEUClose\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondNYClose_0700\":\"SCHEDULER_JOB\",\"AC_SCRIPT_Export_CDW_AnvilEquityEUClose_2100\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondEUClose_1600_ScheduledJob_15:25:00\":\"QUARTZ\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondUKClose_1730\":\"SCHEDULER_JOB\",\"BB_Index_Late_0930\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_NumerixFXSpot_1600\":\"SCHEDULER_JOB\",\"BBG_Index_9am\":\"FILE\",\"BBG_Index_4pm\":\"FILE\",\"BB_NumerixDeposit_1600_1\":\"FILE\",\"BB_NumerixDeposit_1600_2\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityAsiaClose_0830\":\"SCHEDULER_JOB\",\"BBG_Anvil_NYClose_GetData_Original_0700_2\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_AnvilEquityAsiaClose_0830\":\"SCHEDULER_JOB\",\"BBG_Anvil_NYClose_GetData_Original_0700_1\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_NumerixBasisSwapIRS_1600\":\"SCHEDULER_JOB\",\"BBG_Anvil_TWMDClose_1730\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondNYClose_0700_ScheduledJob_06:00:00\":\"QUARTZ\",\"BBG_Anvil_TWMDClose_1730_2\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_NumerixIndex_1530\":\"SCHEDULER_JOB\",\"BBG_Anvil_TWMDClose_1730_1\":\"FILE\",\"AC_SCRIPT_Export_CDW_AnvilBondUKClose_1730\":\"SCHEDULER_JOB\",\"BB_NumerixBasisSwap_1600\":\"FILE\",\"BBG_Anvil_TWMDClose_GetData_1730_1\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_NumerixSwaptionVolatility_1600\":\"SCHEDULER_JOB\",\"BBG_Anvil_TWMDClose_GetData_1730_2\":\"FILE\",\"AC_CHAIN_AnvilBondNYClose_0700_ScheduledJob_07:00:00\":\"QUARTZ\",\"AC_SCRIPT_RefreshADO_ADMN_BloombergRequestAnvil\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceConsolidate_NumerixFXVolatility_1600\":\"SCHEDULER_JOB\",\"ANV_REQUEST_MHIRepoEquitiesEOD_RCVD_EV\":\"FILE\",\"BBG_Anvil_NYClose_GetData_0700_1\":\"FILE\",\"BBG_Anvil_NYClose_GetData_0700_2\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondUKClose_1730_ScheduledJob_16:30:00\":\"QUARTZ\",\"BBG_Anvil_BondEUClose1600\":\"FILE\",\"BB_NumerixIRS_1600_1\":\"FILE\",\"BBG_Anvil_NYClose_0700\":\"FILE\",\"AC_SCRIPT_Interface_SOII\":\"SCHEDULER_JOB\",\"AC_SCRIPT_PriceRequest_BB_NumerixFXSpot_1530\":\"SCHEDULER_JOB\",\"AC_CHAIN_PriceLoad_Numerix_1600_ScheduledJob_17:00:00\":\"QUARTZ\",\"AC_CHAIN_NumerixSOI_1530_ScheduledJob_15:30:00\":\"QUARTZ\",\"AC_CHAIN_AnvilBondUKClose_1730_ScheduledJob_17:30:00\":\"QUARTZ\",\"BB_NumerixIRS_1600_2\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityEUClose_2100_ScheduledJob_16:30:00\":\"QUARTZ\",\"ANV_REQUEST_MHIRepoPrevCloseBonds_RCVD_EV\":\"FILE\",\"AC_CHAIN_AnvilEquityAsiaClose_0830_ScheduledJob_08:30:00\":\"QUARTZ\",\"ANV_REQUEST_MHIRepoEquities_RCVD_EV\":\"FILE\",\"BBG_Anvil_NYClose_0700_2\":\"FILE\",\"BBG_Anvil_NYClose_0700_1\":\"FILE\",\"AC_BBG_Anvil_EUClose_2100_1\":\"FILE\",\"AC_BBG_Anvil_EUClose_2100_2\":\"FILE\",\"BB_NumerixIndex_1600_2\":\"FILE\",\"ANV_REQUEST_MHIRepoBondsGEMM_RCVD_EV\":\"FILE\",\"BB_NumerixIndex_1600_1\":\"FILE\",\"BB_NumerixFRA_1600\":\"FILE\",\"Scheduled Process Event Outbound Flow\":\"OUTBOUND\",\"BB_NumerixBasisSwap_1600_1\":\"FILE\",\"BB_NumerixBasisSwap_1600_2\":\"FILE\",\"BB_NumerixSwaptionVolatility_1600\":\"FILE\",\"BB_NumerixFXSpot_1600_2\":\"FILE\",\"BB_NumerixFXSpot_1600_1\":\"FILE\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600_2\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_NumerixMoneyMarket_1600\":\"SCHEDULER_JOB\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600_1\":\"FILE\",\"AC_BBG_Anvil_EUClose_2100\":\"FILE\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityAsiaClose_0830_ScheduledJob_06:00:00\":\"QUARTZ\",\"BB_NumerixFRA_1600_2\":\"FILE\",\"BB_NumerixFRA_1600_1\":\"FILE\",\"BBG_Anvil_BondEUClose_GetData_1600_1\":\"FILE\",\"AC_BBG_Anvil_AsiaClose_0830_2\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_NumerixFRA_1600\":\"SCHEDULER_JOB\",\"BBG_Anvil_BondEUClose_GetData_1600_2\":\"FILE\",\"AC_BBG_Anvil_AsiaClose_0830_1\":\"FILE\",\"AC_SCRIPT_PriceConsolidate_AnvilBondEUClose_1600\":\"SCHEDULER_JOB\",\"BB_NumerixCapVolatility_1600\":\"FILE\",\"AC_CHAIN_AnvilSOI_1545_ScheduledJob_15:45:00\":\"QUARTZ\",\"BBG_Index_2pm\":\"FILE\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600\":\"FILE\"";

        StringBuilder sb = new StringBuilder(base);

        int num = 1000;

        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_FILE_"+i+"\":\"FILE\"");
        }
        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_SCHEDULER_JOB_"+i+"\":\"SCHEDULER_JOB\"");
        }
        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_QUARTZ_"+i+"_ScheduledJob_06:00:00\":\"QUARTZ\"");
        }

        sb.append("}");

        System.out.println(sb.toString());
    }

    @Test
    public void create_flow_def_2() {
        String base="{\"BBG_Anvil_NYClose_Original_0700_2\":\"AUTOMATIC\",\"AC_SCRIPT_Export_NM_Price_1600\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_Original_0700_1\":\"AUTOMATIC\",\"AC_NM_SOI_1530_RCVD_EV\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixSwaptionVolatility_1530\":\"AUTOMATIC\",\"ANV_REQUEST_MHIRepoBonds_RCVD_EV\":\"AUTOMATIC\",\"AC_CHAIN_PriceExport_Numerix_1600_ScheduledJob_17:00:00\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_AnvilEquityEUClose_2100\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixInterestRateSwap_1530\":\"AUTOMATIC\",\"BB_NumerixFXSpot_1600\":\"AUTOMATIC\",\"AC_CHAIN_PriceConsolidate_Numerix_1600_ScheduledJob_17:00:00\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_GetData_1730\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondEUClose_1600\":\"AUTOMATIC\",\"BB_NumerixDeposit_1600\":\"AUTOMATIC\",\"AC_CHAIN_AnvilBondEUClose_1600_ScheduledJob_16:00:00\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixFXVolatility_1530\":\"AUTOMATIC\",\"AC_SCRIPT_Export_CDW_AnvilBondNYClose_0700\":\"AUTOMATIC\",\"AC_SCRIPT_Export_CDW_AnvilEquityAsiaClose_0830\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityEUClose_2100\":\"AUTOMATIC\",\"BB_NumerixIRS_1600\":\"AUTOMATIC\",\"AC_SCRIPT_RefreshADO_ADMN_BloombergRequestAnvilBondEUClose\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixCapVolatility_1600\":\"AUTOMATIC\",\"BB_NumerixFXVolatility_1600\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetData_1600\":\"AUTOMATIC\",\"BB_NumerixCapVolatility_1600_2\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_Original_0700\":\"AUTOMATIC\",\"BB_NumerixCapVolatility_1600_1\":\"AUTOMATIC\",\"BB_NumerixSwaptionVolatility_1600_1\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixDeposit_1530\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose1600_1\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_0700\":\"AUTOMATIC\",\"BB_NumerixSwaptionVolatility_1600_2\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose1600_2\":\"AUTOMATIC\",\"AC_CHAIN_AnvilSOI_1530_ScheduledJob_15:05:00\":\"AUTOMATIC\",\"AC_SCRIPT_Interface_Bloomberg_DLResponses\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_AnvilBondNYClose_0700\":\"AUTOMATIC\",\"AC_SCRIPT_Interface_CDW_AssetsAnvilSOI\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_Original_0700\":\"AUTOMATIC\",\"AC_CHAIN_AnvilEquityEUClose_2100_ScheduledJob_21:00:00\":\"AUTOMATIC\",\"AC_CHAIN_BloombergRequest_Numerix_1530_ScheduledJob_15:45:00\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixFRA_1530\":\"AUTOMATIC\",\"AC_SCRIPT_RefreshADO_SOII_Anvil\":\"AUTOMATIC\",\"AC_SCRIPT_Export_CDW_AnvilBondEUClose_1600\":\"AUTOMATIC\",\"BB_NumerixIndex_1600\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixBasisSwap_1530\":\"AUTOMATIC\",\"BB_NumerixFXVolatility_1600_1\":\"AUTOMATIC\",\"BB_NumerixFXVolatility_1600_2\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_Original_1600_1\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_Original_1600_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_AnvilBondUKClose_1730\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixCapVolatility_1530\":\"AUTOMATIC\",\"AC_BBG_Anvil_AsiaClose_0830\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_Original_1600\":\"AUTOMATIC\",\"AC_SCRIPT_RefreshADO_SOII_AnvilBondEUClose\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondNYClose_0700\":\"AUTOMATIC\",\"AC_SCRIPT_Export_CDW_AnvilEquityEUClose_2100\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondEUClose_1600_ScheduledJob_15:25:00\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondUKClose_1730\":\"AUTOMATIC\",\"BB_Index_Late_0930\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixFXSpot_1600\":\"AUTOMATIC\",\"BBG_Index_9am\":\"AUTOMATIC\",\"BBG_Index_4pm\":\"AUTOMATIC\",\"BB_NumerixDeposit_1600_1\":\"AUTOMATIC\",\"BB_NumerixDeposit_1600_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityAsiaClose_0830\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_Original_0700_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_AnvilEquityAsiaClose_0830\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_Original_0700_1\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixBasisSwapIRS_1600\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_1730\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondNYClose_0700_ScheduledJob_06:00:00\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_1730_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixIndex_1530\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_1730_1\":\"AUTOMATIC\",\"AC_SCRIPT_Export_CDW_AnvilBondUKClose_1730\":\"AUTOMATIC\",\"BB_NumerixBasisSwap_1600\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_GetData_1730_1\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixSwaptionVolatility_1600\":\"AUTOMATIC\",\"BBG_Anvil_TWMDClose_GetData_1730_2\":\"AUTOMATIC\",\"AC_CHAIN_AnvilBondNYClose_0700_ScheduledJob_07:00:00\":\"AUTOMATIC\",\"AC_SCRIPT_RefreshADO_ADMN_BloombergRequestAnvil\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixFXVolatility_1600\":\"AUTOMATIC\",\"ANV_REQUEST_MHIRepoEquitiesEOD_RCVD_EV\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_0700_1\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_GetData_0700_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilBondUKClose_1730_ScheduledJob_16:30:00\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose1600\":\"AUTOMATIC\",\"BB_NumerixIRS_1600_1\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_0700\":\"AUTOMATIC\",\"AC_SCRIPT_Interface_SOII\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_NumerixFXSpot_1530\":\"AUTOMATIC\",\"AC_CHAIN_PriceLoad_Numerix_1600_ScheduledJob_17:00:00\":\"AUTOMATIC\",\"AC_CHAIN_NumerixSOI_1530_ScheduledJob_15:30:00\":\"AUTOMATIC\",\"AC_CHAIN_AnvilBondUKClose_1730_ScheduledJob_17:30:00\":\"AUTOMATIC\",\"BB_NumerixIRS_1600_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityEUClose_2100_ScheduledJob_16:30:00\":\"AUTOMATIC\",\"ANV_REQUEST_MHIRepoPrevCloseBonds_RCVD_EV\":\"AUTOMATIC\",\"AC_CHAIN_AnvilEquityAsiaClose_0830_ScheduledJob_08:30:00\":\"AUTOMATIC\",\"ANV_REQUEST_MHIRepoEquities_RCVD_EV\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_0700_2\":\"AUTOMATIC\",\"BBG_Anvil_NYClose_0700_1\":\"AUTOMATIC\",\"AC_BBG_Anvil_EUClose_2100_1\":\"AUTOMATIC\",\"AC_BBG_Anvil_EUClose_2100_2\":\"AUTOMATIC\",\"BB_NumerixIndex_1600_2\":\"AUTOMATIC\",\"ANV_REQUEST_MHIRepoBondsGEMM_RCVD_EV\":\"AUTOMATIC\",\"BB_NumerixIndex_1600_1\":\"AUTOMATIC\",\"BB_NumerixFRA_1600\":\"AUTOMATIC\",\"Scheduled Process Event Outbound Flow\":\"AUTOMATIC\",\"BB_NumerixBasisSwap_1600_1\":\"AUTOMATIC\",\"BB_NumerixBasisSwap_1600_2\":\"AUTOMATIC\",\"BB_NumerixSwaptionVolatility_1600\":\"AUTOMATIC\",\"BB_NumerixFXSpot_1600_2\":\"AUTOMATIC\",\"BB_NumerixFXSpot_1600_1\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixMoneyMarket_1600\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600_1\":\"AUTOMATIC\",\"AC_BBG_Anvil_EUClose_2100\":\"AUTOMATIC\",\"AC_SCRIPT_PriceRequest_BB_AnvilEquityAsiaClose_0830_ScheduledJob_06:00:00\":\"AUTOMATIC\",\"BB_NumerixFRA_1600_2\":\"AUTOMATIC\",\"BB_NumerixFRA_1600_1\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetData_1600_1\":\"AUTOMATIC\",\"AC_BBG_Anvil_AsiaClose_0830_2\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_NumerixFRA_1600\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetData_1600_2\":\"AUTOMATIC\",\"AC_BBG_Anvil_AsiaClose_0830_1\":\"AUTOMATIC\",\"AC_SCRIPT_PriceConsolidate_AnvilBondEUClose_1600\":\"AUTOMATIC\",\"BB_NumerixCapVolatility_1600\":\"AUTOMATIC\",\"AC_CHAIN_AnvilSOI_1545_ScheduledJob_15:45:00\":\"AUTOMATIC\",\"BBG_Index_2pm\":\"AUTOMATIC\",\"BBG_Anvil_BondEUClose_GetDataOriginal_1600\":\"AUTOMATIC\"";

        StringBuilder sb = new StringBuilder(base);

        int num = 1000;

        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_FILE_"+i+"\":\"AUTOMATIC\"");
        }
        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_SCHEDULER_JOB_"+i+"\":\"AUTOMATIC\"");
        }
        for (int i=0;i<num;i++) {
            sb.append(",\"SERKAN_TEST_QUARTZ_"+i+"_ScheduledJob_06:00:00\":\"AUTOMATIC\"");
        }

        sb.append("}");

        System.out.println(sb.toString());
    }

}
