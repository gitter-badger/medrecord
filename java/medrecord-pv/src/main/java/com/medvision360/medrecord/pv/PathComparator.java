package com.medvision360.medrecord.pv;

import java.util.Comparator;

/**
 * Logical sorting of archetype(ish) paths.
 * <p/>
 * Sort more specific node identifiers first (i.e.  /foo[at0001][1] < /foo).
 * <p/>
 * Sort longer paths (i.e. /foo/bar/blah < /foo/bar) first.
 * <p/>
 * Finally, sort by string comparison.
 */
public class PathComparator implements Comparator<String>
{
    @Override
    public int compare(String path1, String path2)
    {
        if (path1 == null)
        {
            if (path2 == null)
            {
                return 0;
            }
            else
            {
                return 100;
            }
        }
        if (path2 == null)
        {
            return -100;
        }

        if (path1.equals(path2))
        {
            return 0;
        }

        String[] chain1 = path1.split("/");
        String[] chain2 = path2.split("/");
        return compare(chain1, chain2, 0);
    }

    private int compare(String[] chain1, String chain2[], int offset)
    {
        for (int i = offset; i < chain1.length && i < chain2.length; i++)
        {
            String chunk1s = chain1[i];
            char[] chunk1 = chunk1s.toCharArray();
            int brackets1 = 0;
            String chunk2s = chain2[i];
            char[] chunk2 = chunk2s.toCharArray();
            int brackets2 = 0;

            for (int j = 0; j < chunk1.length; j++)
            {
                char c = chunk1[j];
                if (c == '[')
                {
                    brackets1++;
                }
            }
            for (int j = 0; j < chunk2.length; j++)
            {
                char c = chunk2[j];
                if (c == '[')
                {
                    brackets2++;
                }
            }
            if (brackets1 != brackets2)
            {
                //     ..../foo[a][b]/.... beats ..../foo[a]/....
                return brackets2 - brackets1;
            }
            int stringCompare = chunk1s.compareTo(chunk2s);
            if (stringCompare != 0)
            {
                //     ..../foo/aaa/.... beats ..../foo/baa/....
                return stringCompare;
            }
            //   .../foo/x1 equals .../foo/2, compare x1 to x2
            return compare(chain1, chain2, offset + 1); // recurse!
        }

        // /foo/bar > /foo/bar/blah
        // l=2        l=3
        //      3-2 = 1 means >
        //
        // /foo/bar/blah < /foo/bar
        // l=3             l=2
        //      2-3 = -1 means <
        //
        // /foo/bar = /foo/bar
        //          0
        return chain2.length - chain1.length;
    }
}
