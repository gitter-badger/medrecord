package com.medvision360.medrecord.basex;

import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTestBase;
import org.basex.core.Context;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;

public class DatabaseGeneratorTest extends LocatableStoreTestBase
{
    Context ctx;

    @Override
    public void setUp() throws Exception
    {
        ctx = new Context();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        ctx.close();
    }

    LocatableParser parser = new MockLocatableParser();
    LocatableSerializer serializer = new MockLocatableSerializer();
    String name = "DatabaseGeneratorTest";
    String path = "unittest";
    int numLocatables = 2;

    protected LocatableStore getStore() throws Exception
    {
        BaseXLocatableStore store = new BaseXLocatableStore(
                ctx,
                parser,
                serializer,
                LocatableSelectorBuilder.any(),
                name,
                path
        );
        return store;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void testInsertLotsOfObjects() throws Exception
    {
        // initial version of code had an inline optimize()
        //
        // numLocatables = 1000...
        // Leo's macbook pro: about 36 inserts per second
        //   for $x in collection() return $x
        //      --> 20ms in basex GUI
        //   <locatables>
        //      {for $x in collection("BaseXLocatableStoreTest/unittest/locatable_versions")
        //        /*[ //uid/value/text()
        //          ='11c17534-a2a8-47b3-932a-5e5fc38a271d' ]
        //        return $x
        //      }
        //   </locatables>
        //      ---> 20ms in basex GUI
        //   change condition to starts-with(//uid/value/text(),'1')
        //      ---> 25ms in basex GUI
        //   change condition to starts-with(//uid/value/text(),'1')
        //                       or //archetype_id/value='openehr-unittest-ADMIN_ENTRY.date.v2'
        //      ---> 50ms in basex GUI

        // numLocatables = 10000...doesn't finish in reasonable time

        // concurrent optimize(), optimize once per second
        //
        // numLocatables | inserts per second
        //          100  |  135
        //          200  |  170
        //          500  |  200
        //         1000  |  220
        //         2000  |  220
        //         5000  |  140
        //        10000  |   75

        // concurrent optimize(), optimize every 30 seconds
        //
        // numLocatables | inserts per second
        //          100  |  135
        //          200  |  175
        //          500  |  220
        //         1000  |  245
        //         2000  |  230
        //         5000  |  150
        //        10000  |   85

        // no optimize() at all
        //
        // numLocatables | inserts per second
        //          100  |  135
        //          200  |  175
        //          500  |  220
        //         1000  |  250
        //         2000  |  235
        //         5000  |  150
        //        10000  |   85

        // concurrent optimize(), optimize every 5 seconds
        //
        //            documents  |               seconds  |            per second
        //                 1000  |                  4.65  |                215.24
        //                 2000  |                  9.23  |                216.74
        //                 3000  |                 15.42  |                194.58
        //                 4000  |                 23.51  |                170.15
        //                 5000  |                 33.52  |                149.15
        //                 6000  |                 45.35  |                132.31
        //                 7000  |                 59.60  |                117.44
        //                 8000  |                 75.98  |                105.30
        //                 9000  |                 94.63  |                 95.11
        //                10000  |                115.54  |                 86.55
        //                11000  |                138.49  |                 79.43
        //                12000  |                163.99  |                 73.17
        //                13000  |                192.42  |                 67.56
        //                14000  |                223.07  |                 62.76
        //                15000  |                256.15  |                 58.56
        //                16000  |                291.69  |                 54.85
        //                17000  |                329.96  |                 51.52
        //                18000  |                371.29  |                 48.48
        //                19000  |                415.01  |                 45.78
        //                20000  |                461.35  |                 43.35
        //                21000  |                510.33  |                 41.15
        //                22000  |                562.93  |                 39.08
        //                23000  |                618.49  |                 37.19
        //                24000  |                676.38  |                 35.48
        //                25000  |                737.64  |                 33.89
        //                26000  |                799.79  |                 32.51
        //                27000  |                866.05  |                 31.18
        //                28000  |                934.12  |                 29.97
        //                29000  |               1005.89  |                 28.83
        //                30000  |               1080.86  |                 27.76
        //                31000  |               1159.67  |                 26.73
        //                32000  |               1240.21  |                 25.80
        //                33000  |               1324.78  |                 24.91
        //                34000  |               1412.30  |                 24.07
        //                35000  |               1502.66  |                 23.29
        //                36000  |               1596.38  |                 22.55
        //                37000  |               1694.05  |                 21.84
        //                38000  |               1794.64  |                 21.17
        //                39000  |               1898.04  |                 20.55
        //                40000  |               2005.13  |                 19.95
        //
        // an OPTIMIZE every 5 seconds takes about a second around this db size
        //
        // UPDINDEX = true......
        //   java.lang.ArrayIndexOutOfBoundsException: 6
        //       at org.basex.util.Compress.pull(Compress.java:156)
        // todo a concurrency issue with UPDINDEX? Maybe we're holding on to an IntList where we shouldn't??

        int i = 0;
        List<Locatable> data = new ArrayList<>();
        while (i < numLocatables)
        {
            i++;
            Locatable locatable = makeLocatable();
            data.add(locatable);
        }

        System.out.println("// Insert test");
        System.out.println("// -----------");
        System.out.println(String.format("// %20s  |  %20s  |  %20s", "documents", "seconds", "per second"));

        long start = System.nanoTime();
        i = 0;
        try
        {
            while (i < numLocatables)
            {
                Locatable locatable = data.get(i);
                i++;
                store.insert(locatable);
//                if (i % 50 == 0)
//                {
//                    System.out.print(".");
//                }
                if (i % 1000 == 0)
                {
//                    System.out.println();
                    long end = System.nanoTime();
                    double duration = (end - start) / 1000.0 / 1000.0 / 1000.0;
                    double per_second = i / duration;
                    System.out.println(String.format("// %20d  |  %20.2f  |  %20.2f", i, duration, per_second));
                    if (per_second < 20)
                    {
                        System.out.println("Less than 20 per second, halting test");
                        break;
                    }
                }
            }
            System.out.println();
        }
        finally
        {
            long end = System.nanoTime();
            double duration = (end - start) / 1000.0 / 1000.0 / 1000.0;
            double per_second = i / duration;
            System.out.println(String.format("// inserted %d locatables in %.2f seconds (%.2f per second)", i,
                    duration, per_second));
        }
    }
}
