package com.medvision360.medrecord.itest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class StringGenerator
{
    private final static char[] characters;
    private final static char sp = ' ';
    private final static int minWordLength = 2;
    private final static int maxWordLength = 10;
    
    private final static Map<Character,Double> m = new HashMap<>();
    static {
        // http://en.wikipedia.org/wiki/Letter_frequency#Relative_frequencies_of_letters_in_the_English_language
        m.put('a',8.167);
        m.put('b',1.492);
        m.put('c',2.782);
        m.put('d',4.253);
        m.put('e',12.702);
        m.put('f',2.228);
        m.put('g',2.015);
        m.put('h',6.094);
        m.put('i',6.966);
        m.put('j',0.153);
        m.put('k',0.772);
        m.put('l',4.025);
        m.put('m',2.406);
        m.put('n',6.749);
        m.put('o',7.507);
        m.put('p',1.929);
        m.put('q',0.095);
        m.put('r',5.987);
        m.put('s',6.327);
        m.put('t',9.056);
        m.put('u',2.758);
        m.put('v',0.978);
        m.put('w',2.360);
        m.put('x',0.150);
        m.put('y',1.974);
        m.put('z',0.074);
        
        List<Character> cs = new ArrayList<>();
        for (Map.Entry<Character,Double> entry : m.entrySet())
        {
            Character c = entry.getKey();
            Double f = entry.getValue();
            int count = (int) (f * 10);
            for (int i = 0; i < count; i++)
            {
                cs.add(c);
            }
        }
        characters = new char[cs.size()];
        for (int i = 0; i < characters.length; i++)
        {
            characters[i] = cs.get(i);
        }
    }
    
    private final static Random random = new Random();
    
    public static String getWords(int wordCount)
    {
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; i < wordCount; i++)
        {
            int wordLength = minWordLength+random.nextInt(maxWordLength-minWordLength+1);
            for (int j = 0; j < wordLength; j++)
            {
                int k = random.nextInt(characters.length);
                b.append(characters[k]);
            }
            b.append(sp);
        }
        
        return b.toString();
    }
}
