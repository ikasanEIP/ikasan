package org.ikasan.cli.sample.process;

public class SampleProcess
{
    boolean execute = true;
    public static void main(String[] args)
    {
        SampleProcess sampleProcess = new SampleProcess();
        sampleProcess.execute();
    }

    void execute()
    {
        while(execute)
        {
            sleep();
        }
    }

    void sleep()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}