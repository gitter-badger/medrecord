package com.medvision360.medrecord.rmutil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openehr.build.RMObjectBuilder;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Pathable;

public class RMUtil extends RMObjectBuilder
{
    public static final Pattern INDEX_PATH_PATTERN = Pattern.compile("^(.*?)\\[([0-9]+)\\]/$");

    private static final String ROOT_ARCHETYPE = "^\\[([a-zA-Z][a-zA-Z0-9\\._-]+)\\]/(.*)$";
    public static final Pattern ROOT_ARCHETYPE_PATTERN = Pattern.compile(ROOT_ARCHETYPE);

    private static final String PATH_PART_FIRST = "(.+?)";
    private static final String PATH_PART_ARCHETYPE_NODE_ID = "(?:\\[([a-zA-Z][a-zA-Z0-9\\._-]+)\\])?";
    private static final String PATH_PART_INDEX_ID = "(?:\\[([0-9]+)\\])??";
    public static final Pattern PATH_PART_PATTERN = Pattern.compile(
            "^" + PATH_PART_FIRST + PATH_PART_ARCHETYPE_NODE_ID + PATH_PART_INDEX_ID + "$");

    public RMUtil(Map<SystemValue, Object> systemValues)
    {
        super(systemValues);
    }

    public RMUtil()
    {
        super();
    }

    @SuppressWarnings("ConstantConditions")
    public boolean fuzzyPathEquals(String path, String other)
    {
        if (path == null)
        {
            return other == null;
        }
        if (path.equals(other))
        {
            return true;
        }
        
        Matcher matcher;
        
        // turn [root-archetype-id]/foo/bar into /foo/bar
        String[] paths = path.split("/");
        String rootArchetypeId = null;
        matcher = ROOT_ARCHETYPE_PATTERN.matcher(paths[0]);
        if (matcher.matches())
        {
            rootArchetypeId = matcher.group(1);
            String[] paths2 = new String[paths.length-1];
            System.arraycopy(paths, 1, paths2, 0, paths2.length);
            paths = paths2;
        }
        String[] otherPaths = other.split("/");
        String otherRootArchetypeId = null;
        matcher = ROOT_ARCHETYPE_PATTERN.matcher(otherPaths[0]);
        if (matcher.matches())
        {
            otherRootArchetypeId = matcher.group(1);
            String[] paths2 = new String[otherPaths.length-1];
            System.arraycopy(otherPaths, 1, paths2, 0, paths2.length);
            otherPaths = paths2;
        }
        
        // [root-archetype-id]/foo does not match [other-root-archetype-id/bar
        // [root-archetype-id]/foo does match /foo
        if (rootArchetypeId != null && otherRootArchetypeId != null && !rootArchetypeId.equals(otherRootArchetypeId))
        {
            return false;
        }
        
        // foo/bar/blah does not match /foo/bar/blah/blahBlah
        if (paths.length != otherPaths.length)
        {
            return false;
        }

        for (int i = 0; i < paths.length; i++)
        {
            String pathPart = paths[i];
            String otherPathPart = otherPaths[i];
            
            Matcher pathPartMatcher;
            
            pathPartMatcher = PATH_PART_PATTERN.matcher(pathPart);
            String attributeName = pathPart;
            String archetypeNodeIdString = null;
            int index = -1;
            if (pathPartMatcher.matches())
            {
                attributeName = pathPartMatcher.group(1);
                archetypeNodeIdString = pathPartMatcher.group(2);
                String indexString = pathPartMatcher.group(3);
                if (indexString != null)
                {
                    index = Integer.parseInt(indexString);
                }
            }

            pathPartMatcher = PATH_PART_PATTERN.matcher(otherPathPart);
            String otherAttributeName = otherPathPart;
            String otherArchetypeNodeIdString = null;
            int otherIndex = -1;
            if (pathPartMatcher.matches())
            {
                otherAttributeName = pathPartMatcher.group(1);
                otherArchetypeNodeIdString = pathPartMatcher.group(2);
                String indexString = pathPartMatcher.group(3);
                if (indexString != null)
                {
                    otherIndex = Integer.parseInt(indexString);
                }
            }
            
            // foo/bar does not match /foo/blah
            if (!attributeName.equals(otherAttributeName))
            {
                return false;
            }
            
            // /foo/bar[at0001] does not match /foo/bar/[at0002]
            if (archetypeNodeIdString == null)
            {
                if (otherArchetypeNodeIdString != null)
                {
                    return false;
                }
            }
            else
            {
                if (!archetypeNodeIdString.equals(otherArchetypeNodeIdString))
                {
                    return false;
                }
            }
            
            // foo/bar[1]/blah matches foo/bar/blah
            if ((index != -1 || otherIndex != -1) && index != otherIndex)
            {
                return false;
            }
        }
        
        return true;
    }

    public void setParent(Object child, Object parent)
            throws IllegalAccessException, InvocationTargetException
    {
        if (child instanceof Pathable && parent instanceof Pathable)
        {
            try
            {
                Method setParent = child.getClass().getMethod("setParent", Pathable.class);
                setParent.invoke(child, (Pathable) parent);
            }
            catch (NoSuchMethodException e)
            {
            }
        }
    }

    protected Object get(Object target, String name) throws InvocationTargetException, IllegalAccessException
    {
        Method getter = getter(name, target.getClass());
        if (getter == null)
        {
            return null;
        }

        Object value = getter.invoke(target);
        return value;
    }

    protected String indexPath(String fullPath, Object index)
    {
        return fullPath + "[" + index + "]/";
    }
}
