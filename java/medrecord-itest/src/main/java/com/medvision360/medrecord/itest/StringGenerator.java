package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Simple utility to generate words and phrases that are gobblygook but that look and feel like they could be almost
 * english.
 */
public class StringGenerator
{
    private final static char[] CHARACTERS;
    private final static char SP = ' ';
    private final static int MIN_WORD_LENGTH = 2;
    private final static int MAX_WORD_LENGTH = 10;

    static
    {
        Map<Character, Double> FREQUENCY_MAP = new HashMap<>();
        // http://en.wikipedia.org/wiki/Letter_frequency#Relative_frequencies_of_letters_in_the_English_language
        FREQUENCY_MAP.put('a', 8.167);
        FREQUENCY_MAP.put('b', 1.492);
        FREQUENCY_MAP.put('c', 2.782);
        FREQUENCY_MAP.put('d', 4.253);
        FREQUENCY_MAP.put('e', 12.702);
        FREQUENCY_MAP.put('f', 2.228);
        FREQUENCY_MAP.put('g', 2.015);
        FREQUENCY_MAP.put('h', 6.094);
        FREQUENCY_MAP.put('i', 6.966);
        FREQUENCY_MAP.put('j', 0.153);
        FREQUENCY_MAP.put('k', 0.772);
        FREQUENCY_MAP.put('l', 4.025);
        FREQUENCY_MAP.put('m', 2.406);
        FREQUENCY_MAP.put('n', 6.749);
        FREQUENCY_MAP.put('o', 7.507);
        FREQUENCY_MAP.put('p', 1.929);
        FREQUENCY_MAP.put('q', 0.095);
        FREQUENCY_MAP.put('r', 5.987);
        FREQUENCY_MAP.put('s', 6.327);
        FREQUENCY_MAP.put('t', 9.056);
        FREQUENCY_MAP.put('u', 2.758);
        FREQUENCY_MAP.put('v', 0.978);
        FREQUENCY_MAP.put('w', 2.360);
        FREQUENCY_MAP.put('x', 0.150);
        FREQUENCY_MAP.put('y', 1.974);
        FREQUENCY_MAP.put('z', 0.074);

        List<Character> cs = new ArrayList<>();
        for (Map.Entry<Character, Double> entry : FREQUENCY_MAP.entrySet())
        {
            Character c = entry.getKey();
            Double f = entry.getValue();
            int count = (int) (f * 10);
            for (int i = 0; i < count; i++)
            {
                cs.add(c);
            }
        }
        CHARACTERS = new char[cs.size()];
        for (int i = 0; i < CHARACTERS.length; i++)
        {
            CHARACTERS[i] = cs.get(i);
        }
    }

    private Random m_random = new Random();

    public String generateWords(int wordCount)
    {
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < wordCount; i++)
        {
            int wordLength = MIN_WORD_LENGTH + m_random.nextInt(MAX_WORD_LENGTH - MIN_WORD_LENGTH + 1);
            for (int j = 0; j < wordLength; j++)
            {
                int k = m_random.nextInt(CHARACTERS.length);
                b.append(CHARACTERS[k]);
            }
            b.append(SP);
        }

        return b.toString();
    }
}
