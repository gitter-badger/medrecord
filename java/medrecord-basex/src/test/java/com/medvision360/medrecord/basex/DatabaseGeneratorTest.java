package com.medvision360.medrecord.basex;

import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.spi.LocatableParser;
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
    String name = "BaseXLocatableStoreTest";
    String path = "unittest";
    int numLocatables = 2;
    
    protected LocatableStore getStore() throws Exception
    {
        BaseXLocatableStore store = new BaseXLocatableStore(
                ctx,
                parser,
                serializer,
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
        
        // so for now we're optimizing once every 5 seconds, iff the index needs updating
        
        int i = 0;
        List<Locatable> data = new ArrayList<>();
        while(i < numLocatables)
        {
            i++;
            HierObjectID uid = new HierObjectID(makeUUID());
            Locatable locatable = makeLocatable(uid, parent);
            data.add(locatable);
        }
        
        long start = System.nanoTime();
        i = 0;
        try
        {
            while(i < numLocatables)
            {
                Locatable locatable = data.get(i);
                i++;
                store.insert(locatable);
                if (i % 5 == 0)
                {
                    System.out.print(".");
                }
                if (i % 100 == 0)
                {
                    System.out.println();
                }
            }
            System.out.println();
        }
        finally
        {
            long end = System.nanoTime();
            double duration = (end - start)/1000.0/1000.0/1000.0;
            double per_second = numLocatables / duration;
            System.out.println(String.format("inserted %d locatables in %.2f seconds (%.2f per second)", i, 
                    duration, per_second));
        }
    }
}
