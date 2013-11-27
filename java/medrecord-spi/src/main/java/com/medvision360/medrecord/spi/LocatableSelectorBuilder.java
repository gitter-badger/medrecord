/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

/**
 * Allows constructing a rule-based {@link LocatableSelector}.
 * <p/>
 * Examples:
 * <pre>
 *     LocatableSelector specialistECG = LocatableSelectorBuilder
 *         .start()
 *         .requireRMVersion("1.4")
 *         .matchArchetypeId("^openEHR-EHR-OBSERVATION.ecg.v[12](?:draft)?$")
 *         .build();
 *     LocatableSelector basicEHR = LocatableSelectorBuilder
 *         .start()
 *         .requireRMVersion("1.4")
 *         .requireRMName("EHR")
 *         .matchRMEntity("^(?:COMPOSITION|EHRSTATUS|ACTION|ADMIN_ENTRY|EVALUATION|INSTRUCTION|OBSERVATION)$")
 *         .build();
 *     LocatableSelector basicDemographics = LocatableSelectorBuilder
 *         .start()
 *         .requireRMVersion("1.4")
 *         .requireRMName("DEMOGRAPHIC")
 *         .matchRMEntity("^(?:PARTY_IDENTITY|PARTY_RELATIONSHIP|PERSON|ORGANISATION|ROLE)$")
 *         .build();
 *     LocatableSelector standardsOnly = LocatableSelectorBuilder
 *         .start()
 *         .matchRMVersion("^1\\.[45]$")
 *         .matchRMOriginator("openEHR")
 *         .build();
 *     LocatableSelector fallback = LocatableSelectorBuilder.any();
 * </pre>
 * <p/>
 * Such selectors can then be used to configure implementations of services like {@link LocatableStore} and {@link
 * LocatableParser} to accept only certain data.
 */
@SuppressWarnings("UnusedDeclaration")
public class LocatableSelectorBuilder
{
    private List<Rule> m_requirements = new ArrayList<>();

    public static LocatableSelector any()
    {
        return new AnySelector();
    }

    public static LocatableSelectorBuilder start()
    {
        LocatableSelectorBuilder builder = new LocatableSelectorBuilder();
        return builder;
    }

    public LocatableSelector build()
    {
        LocatableSelector result = new Selector(new ArrayList<>(m_requirements));
        return result;
    }

    public LocatableSelectorBuilder include(LocatableSelector rule)
    {
        m_requirements.add(new LocatableSelectorRule(rule));
        return this;
    }

    public LocatableSelectorBuilder exclude(LocatableSelector rule)
    {
        m_requirements.add(new InverseRule(new LocatableSelectorRule(rule)));
        return this;
    }

    public LocatableSelectorBuilder include(Rule rule)
    {
        m_requirements.add(rule);
        return this;
    }

    public LocatableSelectorBuilder exclude(Rule rule)
    {
        m_requirements.add(new InverseRule(rule));
        return this;
    }

    public static class Selector implements LocatableSelector
    {
        private List<Rule> m_requirements = new ArrayList<>();

        private Selector(List<Rule> requirements)
        {
            m_requirements = requirements;
        }

        @Override
        public boolean supports(Locatable test)
        {
            for (Rule rule : m_requirements)
            {
                if (!rule.match(test))
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean supports(Archetyped test)
        {
            for (Rule rule : m_requirements)
            {
                if (!rule.match(test))
                {
                    return false;
                }
            }

            return true;
        }
    }

    public static class AnySelector implements LocatableSelector
    {
        @Override
        public boolean supports(Locatable test)
        {
            return true;
        }

        @Override
        public boolean supports(Archetyped test)
        {
            return true;
        }
    }

    public static interface Rule
    {
        boolean match(Locatable test);

        boolean match(Archetyped test);
    }
    
    public static class LocatableSelectorRule implements Rule
    {
        private LocatableSelector m_delegate;

        public LocatableSelectorRule(LocatableSelector delegate)
        {
            m_delegate = delegate;
        }

        @Override
        public boolean match(Locatable test)
        {
            return m_delegate.supports(test);
        }

        @Override
        public boolean match(Archetyped test)
        {
            return m_delegate.supports(test);
        }
    }

    public static class InverseRule implements Rule
    {
        private Rule m_delegate;

        public InverseRule(Rule delegate)
        {
            m_delegate = delegate;
        }

        @Override
        public boolean match(Locatable test)
        {
            return !m_delegate.match(test);
        }

        @Override
        public boolean match(Archetyped test)
        {
            return !m_delegate.match(test);
        }
    }

    public abstract static class EqualsRule implements Rule
    {
        private Object m_expected;

        protected EqualsRule(Object expected)
        {
            m_expected = expected;
        }

        @Override
        public boolean match(Locatable test)
        {
            Object toTest = getTest(test);
            if (toTest == null)
            {
                return test == null;
            }
            return toTest.equals(m_expected);
        }

        @Override
        public boolean match(Archetyped test)
        {
            Object toTest = getTest(test);
            if (toTest == null)
            {
                return test == null;
            }
            return toTest.equals(m_expected);
        }

        protected Object getTest(Locatable test)
        {
            return getTest(test == null ? null : test.getArchetypeDetails());
        }

        protected abstract Object getTest(Archetyped test);
    }

    public abstract static class PatternRule implements Rule
    {
        private Pattern m_pattern;

        private PatternRule(Pattern pattern)
        {
            m_pattern = pattern;
        }

        @Override
        public boolean match(Locatable test)
        {
            return m_pattern.matcher(getTest(test)).matches();
        }

        @Override
        public boolean match(Archetyped test)
        {
            return m_pattern.matcher(getTest(test)).matches();
        }

        protected String getTest(Locatable test)
        {
            return getTest(test == null ? null : test.getArchetypeDetails());
        }

        protected abstract String getTest(Archetyped test);
    }

    //
    // RM Version
    //

    public LocatableSelectorBuilder requireRMVersion(String test)
    {
        m_requirements.add(new RMVersionEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder excludeRMVersion(String test)
    {
        m_requirements.add(new RMVersionEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchRMVersion(Pattern test)
    {
        m_requirements.add(new RMVersionPatternRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchRMVersion(String test)
    {
        return matchRMVersion(Pattern.compile(test));
    }

    public LocatableSelectorBuilder doNotMatchRMVersion(Pattern test)
    {
        m_requirements.add(new InverseRule(new RMVersionPatternRule(test)));
        return this;
    }

    public LocatableSelectorBuilder doNotMatchRMVersion(String test)
    {
        return doNotMatchRMVersion(Pattern.compile(test));
    }

    public static class RMVersionPatternRule extends PatternRule
    {
        public RMVersionPatternRule(Pattern pattern)
        {
            super(pattern);
        }

        @Override
        protected String getTest(Archetyped test)
        {
            return test == null ? null : test.getRmVersion();
        }
    }

    public static class RMVersionEqualsRule extends EqualsRule
    {
        public RMVersionEqualsRule(Object expected)
        {
            super(expected);
        }

        @Override
        protected Object getTest(Archetyped test)
        {
            return test == null ? null : test.getRmVersion();
        }
    }

    //
    // Archetype ID
    //

    public LocatableSelectorBuilder matchArchetypeId(Pattern test)
    {
        m_requirements.add(new ArchetypeIDPatternRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchArchetypeId(String test)
    {
        return matchArchetypeId(Pattern.compile(test));
    }

    public LocatableSelectorBuilder doNotMatchArchetypeId(Pattern test)
    {
        m_requirements.add(new InverseRule(new ArchetypeIDPatternRule(test)));
        return this;
    }

    public LocatableSelectorBuilder doNotMatchArchetypeId(String test)
    {
        return doNotMatchArchetypeId(Pattern.compile(test));
    }

    public LocatableSelectorBuilder requireArchetypeId(String test)
    {
        m_requirements.add(new ArchetypeIDEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder excludeArchetypeId(String test)
    {
        m_requirements.add(new InverseRule(new ArchetypeIDEqualsRule(test)));
        return this;
    }

    public static class ArchetypeIDPatternRule extends PatternRule
    {
        public ArchetypeIDPatternRule(Pattern pattern)
        {
            super(pattern);
        }

        @Override
        protected String getTest(Archetyped test)
        {
            return test == null ? null :
                    test.getArchetypeId() == null ? null :
                            test.getArchetypeId().getValue();
        }
    }

    public static class ArchetypeIDEqualsRule extends EqualsRule
    {
        public ArchetypeIDEqualsRule(Object expected)
        {
            super(expected);
        }

        @Override
        protected Object getTest(Archetyped test)
        {
            return test == null ? null :
                    test.getArchetypeId() == null ? null :
                            test.getArchetypeId().getValue();
        }
    }

    //
    // RM Originator
    //

    public LocatableSelectorBuilder matchRMOriginator(Pattern test)
    {
        m_requirements.add(new RMOriginatorPatternRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchRMOriginator(String test)
    {
        return matchRMOriginator(Pattern.compile(test));
    }

    public LocatableSelectorBuilder doNotMatchRMOriginator(Pattern test)
    {
        m_requirements.add(new InverseRule(new RMOriginatorPatternRule(test)));
        return this;
    }

    public LocatableSelectorBuilder doNotMatchRMOriginator(String test)
    {
        return doNotMatchRMOriginator(Pattern.compile(test));
    }

    public LocatableSelectorBuilder requireRMOriginator(String test)
    {
        m_requirements.add(new RMOriginatorEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder excludeRMOriginator(String test)
    {
        m_requirements.add(new InverseRule(new RMOriginatorEqualsRule(test)));
        return this;
    }

    public static class RMOriginatorPatternRule extends PatternRule
    {
        public RMOriginatorPatternRule(Pattern pattern)
        {
            super(pattern);
        }

        @Override
        protected String getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmOriginator();
        }
    }

    public static class RMOriginatorEqualsRule extends EqualsRule
    {
        public RMOriginatorEqualsRule(Object expected)
        {
            super(expected);
        }

        @Override
        protected Object getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmOriginator();
        }
    }

    //
    // RM Name
    //

    public LocatableSelectorBuilder matchRMName(Pattern test)
    {
        m_requirements.add(new RMNamePatternRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchRMName(String test)
    {
        return matchRMName(Pattern.compile(test));
    }

    public LocatableSelectorBuilder doNotMatchRMName(Pattern test)
    {
        m_requirements.add(new InverseRule(new RMNamePatternRule(test)));
        return this;
    }

    public LocatableSelectorBuilder doNotMatchRMName(String test)
    {
        return doNotMatchRMName(Pattern.compile(test));
    }

    public LocatableSelectorBuilder requireRMName(String test)
    {
        m_requirements.add(new RMNameEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder excludeRMName(String test)
    {
        m_requirements.add(new InverseRule(new RMNameEqualsRule(test)));
        return this;
    }

    public static class RMNamePatternRule extends PatternRule
    {
        public RMNamePatternRule(Pattern pattern)
        {
            super(pattern);
        }

        @Override
        protected String getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmName();
        }
    }

    public static class RMNameEqualsRule extends EqualsRule
    {
        public RMNameEqualsRule(Object expected)
        {
            super(expected);
        }

        @Override
        protected Object getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmName();
        }
    }

    //
    // RM Entity
    //

    public LocatableSelectorBuilder matchRMEntity(Pattern test)
    {
        m_requirements.add(new RMEntityPatternRule(test));
        return this;
    }

    public LocatableSelectorBuilder matchRMEntity(String test)
    {
        return matchRMEntity(Pattern.compile(test));
    }

    public LocatableSelectorBuilder doNotMatchRMEntity(Pattern test)
    {
        m_requirements.add(new InverseRule(new RMEntityPatternRule(test)));
        return this;
    }

    public LocatableSelectorBuilder doNotMatchRMEntity(String test)
    {
        return doNotMatchRMEntity(Pattern.compile(test));
    }

    public LocatableSelectorBuilder requireRMEntity(String test)
    {
        m_requirements.add(new RMEntityEqualsRule(test));
        return this;
    }

    public LocatableSelectorBuilder excludeRMEntity(String test)
    {
        m_requirements.add(new InverseRule(new RMEntityEqualsRule(test)));
        return this;
    }

    public static class RMEntityPatternRule extends PatternRule
    {
        public RMEntityPatternRule(Pattern pattern)
        {
            super(pattern);
        }

        @Override
        protected String getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmEntity();
        }
    }

    public static class RMEntityEqualsRule extends EqualsRule
    {
        public RMEntityEqualsRule(Object expected)
        {
            super(expected);
        }

        @Override
        protected Object getTest(Archetyped test)
        {
            return test == null ? null :
                test.getArchetypeId() == null ? null :
                        test.getArchetypeId().rmEntity();
        }
    }
}
